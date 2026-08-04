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

### JWT validated at the gateway only

The gateway validates once and propagates identity inward as trusted headers. Services
drop their own `JwtAuthFilter`. This removes a cross-service contract from `core`:
otherwise the token's claims are a wire format shared by every service, versioned by
nobody.

### Queue names move to configuration

They are shared Kotlin constants today. Two services disagreeing on a queue name fails
silently — the message simply never arrives. Configuration can be changed without
recompiling every consumer.

## Order

1. **Foundations** — `shared` to KMP, composite build, `contracts/` with `buf` in CI.
2. **`notification`** — one table, no public API, only consumes events. The cheapest
   place to discover a design mistake.
3. **Gateway**, with `notification` behind it. Auth and routing get settled here.
4. **`user`** — the delicate one: authentication, and everyone depends on its events.
5. **`inventory`** and **`chat`**.
6. **Outbox** in all of them.
7. **Tracing** and a development compose file.

## Risks worth naming

**There are four tests.** Splitting a monolith with no coverage of auth, chat or
inventory means failures surface at runtime, in production, with four processes to
search instead of one. Writing tests for the critical flows before step 2 is the single
highest-value thing on this list.

**No transactional outbox.** `AuthService.completeRegistration` saves the user and then
publishes an event; if the publish fails after the commit, a user exists with no chat
participant. One database made that survivable. Separate ones make it corruption.

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
