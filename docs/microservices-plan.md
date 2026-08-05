# Breaking the monolith into services

The backend is one deployable with four features inside it. This is the plan to make
each feature a service, with a gateway in front, contracts in protobuf, and everything
still in this repository.

## Target

```
                        ┌──────────────┐
  KMP client  ────────► │   gateway    │  JWT · rate limit · routing · CORS
  (REST/GraphQL)        └──────┬───────┘
                               │ REST/GraphQL
              ┌────────────┬───┴────────┬──────────────┐
              ▼            ▼            ▼              ▼
          ┌───────┐   ┌────────┐   ┌─────────┐   ┌──────────────┐
          │ user  │   │  chat  │   │inventory│   │ notification │
          └───┬───┘   └───┬────┘   └────┬────┘   └──────┬───────┘
              │  gRPC     │             │               │
              └───────────┴──── protobuf over RabbitMQ ─┘
                               │
                    Postgres (one schema per service) · Redis
```

```
contracts/                  the .proto files, versioned, compatibility checked in CI
  user/v1/ · chat/v1/ · inventory/v1/ · notification/v1/

services/
  gateway/
  user/          domain · service · infra · api + Application + Dockerfile
  chat/
  inventory/
  notification/

core/                       JVM, source dependency between services
shared/                     KMP, composite build with the client
Client/AdventistPortal KMP/
```

## Decisions, and why

### One repository

Repository layout and service architecture are independent: five independently
deployable services can live in one repository. Independence comes from separate
artifacts and pipelines, not separate gits.

With one developer, multi-repo costs everything and buys nothing — its benefits are
about coordinating *teams*. And splitting later is a `git filter-repo` afternoon;
merging later is not.

### One Postgres, one schema per service

Cheaper to operate than four databases, and the cut is already clean: there are no
cross-schema foreign keys left.

**This only holds if it is enforced.** Each service gets its own Postgres role with
grants on its own schema only. Without that, nothing stops a `JOIN` across schemas and
the boundary is decoration — a distributed monolith, which is worse than either.

### What is versioned, and what is not

```
core       no version   source dependency
shared     no version   KMP + composite build with the client
contracts  VERSIONED    protobuf, compatibility enforced in CI
images     VERSIONED    user-service:1.4.0 — what you deploy and roll back
```

A package version buys exactly one thing: consumers migrating at different times. In
one repository with four consumers built from the same commit, nothing consumes `core`
asynchronously, so the number would describe nothing. The twenty modules in this repo
have all said `0.0.1-SNAPSHOT` since the first commit and it has never mattered.

What *does* need versioning is the wire. Service A deployed on Tuesday talks to service
B deployed on Thursday; each carries its own compiled copy of `core`, so no version on
a jar helps. What helps is a message that stays readable across those two builds, which
is what protobuf plus a breaking-change check gives.

### Protobuf for events and gRPC, REST/GraphQL outward

Events currently travel as Jackson polymorphic JSON with an embedded `@class`. That is
an unversioned contract defined by Kotlin sealed classes in `core`. It moves to
`contracts/`, generated per service, with `buf` failing the build on an incompatible
change.

The external API stays REST and GraphQL: the KMP client is the harshest deployment skew
of all — a phone can hold a three-month-old build — and that surface needs to stay
readable and versionable on its own terms (`/api/v1/...`).

Note that every synchronous gRPC call couples two services at runtime. Today none
exists: features talk only through events. Keep it that way unless a call earns itself.

### The gateway runs on the reactive stack

Only because of WebSockets. The servlet variant of Spring Cloud Gateway cannot proxy
them, which left the chat handshake reaching that service directly and verifying its own
token — a hole in the rule that a token is read in exactly one place, and the kind of
exception that quietly becomes permanent.

A browser cannot set headers on a handshake, so the token arrives there as a query
parameter. It is still read in the same filter as every other request.

### JWT validated at the gateway only

The gateway validates once and propagates identity inward as trusted headers. Services
drop their own `JwtAuthFilter`. This removes a cross-service contract from `core`:
otherwise the token's claims are a wire format shared by every service, versioned by
nobody.

The split that fell out of building it: the gateway answers *who is calling* and the
service answers *may they call*. The gateway holds no list of public paths — that list
belongs to whoever owns the endpoints, and a second copy at the edge is a copy that
drifts. So a request with no token is forwarded as anonymous and refused by the service;
a request with a *broken* token is refused at the edge, since that is an error under any
policy.

One thing this leaves open: the signing key is symmetric, so the gateway can mint tokens
as well as read them. Moving to RS256 would leave signing with the user service alone.

The trusted header is only trustworthy while the service ports are unreachable except
through the gateway. compose.yaml is what makes that true rather than hoped for.

### Queue names move to configuration

They are shared Kotlin constants today. Two services disagreeing on a queue name fails
silently — the message simply never arrives. Configuration can be changed without
recompiling every consumer.

## Order

1. **Foundations** — `shared` to KMP, composite build, `contracts/` with `buf` in CI.
2. **`notification`** — one table, no public API, only consumes events. The cheapest
   place to discover a design mistake.
3. **Gateway**, with `notification` behind it. Auth and routing get settled here.
   Routes are named per feature, never per deployable: two features sharing a URI is a
   deployment detail, and extracting one is then a change of address rather than a change
   of routing. A catch-all named after whatever is left over describes the past, and gets
   less true with every step.
4. **`user`** — the delicate one: authentication, and everyone depends on its events.
5. **`inventory`** and **`chat`**. With these out, `app` has nothing left to assemble
   and is deleted.
6. **Outbox** in all of them.
7. **Tracing** and a development compose file.

All seven are done. What follows is what was learned doing them.

## Risks worth naming

**Almost no tests.** Splitting a monolith with no coverage means failures surface at
runtime, in production, with five processes to search instead of one.

Auth, chat and inventory now have end-to-end coverage against containers, written before
each was moved. That order paid for itself: the chat and inventory tests found two bugs
in the asset register that nothing had ever run into. `notification` still has none.

**~~No transactional outbox.~~** Events are now written in the transaction that caused
them and relayed afterwards. Delivery is at-least-once, so consumers must tolerate seeing
an event twice.

The trace has to be carried through it by hand. The relay runs long after the request, so
the originating context is stored on the row and re-entered before sending — Spring AMQP
injects whatever context is *current* as it sends, which means the current one has to be
the right one, not a header written alongside it.

**Observation is off by default in more places than you would guess.** Spring AMQP
discards the trace context on both send and receive unless asked; Spring Boot 4 moved the
Zipkin endpoint property and left the old key binding silently to nothing; and a
hand-built RabbitTemplate ignores the properties that would have enabled it. Each of
these fails by producing a plausible-looking trace that simply stops somewhere.

**Operational load for one developer.** From one `bootRun` to five processes, a gateway,
generated protobuf and a seven-container compose file. That cost is real, permanent, and
paid on every small change.

## Prerequisites for `shared` becoming multiplatform

- `shared/domain/inventory/model/Article.kt` imports `core.domain.types.ArticleId`.
  `core` is JVM, so that import stops `shared` compiling for iOS. The ids move to
  `shared` or the model stops using them.
- `java.util.UUID` and `java.time.Instant` do not exist on Kotlin/Native. Inside
  `shared` they become `kotlin.uuid.Uuid` or `String`, and `kotlinx.datetime.Instant`.
- Nothing from Spring, JPA or `jakarta.*` can enter `shared`. The moment it does, the
  client stops building.
- `shared/infra` and `shared/api` are declared and empty. They are removed: shared
  infrastructure and web layers are exactly what cannot cross to the client.
