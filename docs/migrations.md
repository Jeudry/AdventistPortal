# Database migrations

Three pieces, one job each. They only stay out of each other's way because each has
exactly one role — the earlier setup broke precisely because two of them both claimed
to own "what is already migrated".

| Piece | Role | Where |
| ----- | ---- | ----- |
| **JPA Buddy** | writes migrations | IntelliJ, *File type* = **SQL** |
| **`scripts/gen-migration.sh`** | **verifies** — never writes into the flow | CLI |
| **Liquibase** | applies them on startup | `spring.liquibase` |

Migrations live in `app/src/main/resources/db/changelog/yyyy/MM/dd-NN-name.sql`. The
master changelog scans that directory with `includeAll`, so **a file runs by existing**
— there is nothing to register by hand, and nothing to forget. Order comes from the
path, which is why the date-based naming matters. `endsWithFilter=".sql"` keeps the
master (an `.xml`) out of its own scan.

## The one rule

> **Point JPA Buddy at the database, never at a snapshot file.**

JPA Buddy can diff the model against `db/snapshot/data-model-snapshot.json`, a file only
it updates. Everything else diffs against the changelogs, so that file is a second
source of truth — and it is the one that goes stale silently. It once sat five months
behind the baseline while still being trusted to generate migrations. It is deleted;
generate with **Source = Model, Target = DB** instead, against a database the changelogs
built.

That holds as long as nothing edits the database by hand. `gen-migration.sh` is what
tells you if it did, because it compares against the changelogs rather than the DB.

## Day to day

**Add a migration.** Change the entity, then generate from JPA Buddy in SQL. Review it
— see below. Start the app; Liquibase applies it.

**Iterate on one you have not committed yet.** Do not stack V2, V3, V4 while you get
the shape right. Reset and regenerate the same file:

```bash
scripts/db-reset.sh          # drops the *_service schemas and Liquibase's ledger
```

The next start rebuilds from the changelogs, so your hand edits survive (they live in
the file, not the database) and there is no checksum conflict from editing a changeset
that had already run. Only local data is lost.

Roll-forward — a new migration that corrects — applies to what has already left your
machine. In dev, reset.

**Check the model and the migrations still agree.**

```bash
scripts/gen-migration.sh CheckDrift
```

An empty delta means they agree. This is the script's only job now; it does not
generate what you ship.

You also get this for free at every startup: `ddl-auto` is `validate` in **all**
profiles, so if the model and the schema drift, the app refuses to start on your
machine instead of in production. Do not set it back to `update` — that is what hid
the drift before, and it silently undoes `liquibase rollback`.

## Review what was generated

No generator gets these right. Check every one:

- **Renames come out as `DROP COLUMN` + `ADD COLUMN`** → data loss. Rewrite by hand.
- **`modifyDataType` picks the wrong direction** in Liquibase 5.x — confirm the target
  type is what you meant (e.g. `timestamp with time zone` for `Instant`).
- Anything you did not intend: the diff reports *every* structural difference.

## Rollback

`liquibase rollback-count 1` is free here — Flyway charges for `undo`. But a changeset
can only be undone if it says how:

- XML/YAML changesets are reversible automatically.
- **SQL changesets are not.** Add `--rollback <statement>` yourself.

Worth writing only on destructive changes (`DROP COLUMN`, `DROP TABLE`). Everything
else is cheaper to reset. And remember rollback restores *structure*, never *data* —
the same limitation as EF's `Down()`.

## Requirements

Docker, and the Postgres JDBC driver in the Gradle cache (any prior build resolves it).
The Liquibase image is pinned to the Spring-managed version.

## Gotchas paid for already

- **`liquibase drop-all` only touches the schemas you name.** Without `--schemas` it
  empties the default one and leaves `*_service` untouched, reporting success. It also
  leaves the schemas themselves standing, which is why the baseline creates them with
  `if not exists`.
- **Naming strategies belong under `spring.jpa.hibernate.naming.*`**, not under
  `spring.jpa.properties.hibernate.naming.*`. The latter passes the key to Hibernate
  verbatim, and `hibernate.naming.implicit-strategy` is not a Hibernate key, so the
  setting silently does nothing and embedded fields lose their component path.
- **`host.docker.internal` does not reach the compose containers** — they publish on
  loopback only. Go through the compose network or the orb DNS name.

## Evaluated and rejected

**`org.liquibase.ext:liquibase-hibernate7`** — would replace the model export and the
second throwaway database. It exists, works with Hibernate 7, and is **unusable here**:
`TableSnapshotGenerator` walks every Hibernate namespace and then stamps each table
with the container's schema, and `HibernateDatabase.getDefaultSchemaName()` is
hardcoded to `"HIBERNATE"`. Every table collapses into one synthetic schema, so a diff
against a multi-schema database is not incomplete — it is wrong. Asking for a single
schema returns every table labelled with it. Patchable through Liquibase's
`replaces()` extension point, but that is half a dozen classes tracking Liquibase
internals to save one `docker run`.

**Exposed** — its `MigrationUtils` diffs against a **live** database and has no offline
DDL export, so the model side would get worse, not better. 292 Stack Overflow questions
against Hibernate's 95,215; absent from Spring Boot's BOM and from Initializr.

**Flyway** — dropped; `undo` is a paid feature and JPA Buddy generates for Liquibase
just as well.
