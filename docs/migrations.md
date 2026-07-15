# Database migrations — EF-Core-style, offline

This project uses **Liquibase** at runtime (`spring.liquibase`, master changelog at
`app/src/main/resources/db/changelog/db.changelog-master.xml`), but generates new
migrations the way EF Core does: by **diffing the current JPA model against the state
of the already-written migrations** — never against your real database.

Nothing in this flow connects to Supabase, your local stack, or orb. It spins a
throwaway Postgres in Docker, uses it, and tears it down.

## TL;DR

```bash
scripts/gen-migration.sh AddSomethingDescriptive
```

This writes `app/src/main/resources/db/changelog/generated/<timestamp>-AddSomethingDescriptive.postgresql.sql`.
Review it, then wire it into the master changelog (see step 4).

## How it maps to EF Core

| EF Core                          | Here                                                        |
| -------------------------------- | ----------------------------------------------------------- |
| The model (`DbContext` entities) | The JPA `@Entity` classes                                   |
| `ModelSnapshot.cs`               | The applied Liquibase master changelog (`db.changelog-*`)   |
| `dotnet ef migrations add X`     | `scripts/gen-migration.sh X`                                |
| Diff model vs snapshot           | Diff **model DDL** vs **changelog-applied schema**          |
| `dotnet ef database update`      | Runtime `spring.liquibase` on app start                     |

The "snapshot" is not a DB — it is the SQL your changelogs already describe.

## What the script does

1. **Export the model** — `./gradlew :app:exportModelSchema` runs
   `ModelSchemaExportTest`, which scans every `@Entity` on the classpath and asks
   Hibernate to emit Postgres DDL to `app/build/model-schema.sql`. Fully offline; no
   database is touched. Naming strategies match the runtime config
   (`CamelCaseToUnderscores` + `ComponentPathImpl`).
2. **Throwaway Postgres** — a `postgres:16-alpine` container (`ap-shadow-migrate`,
   port 5433, `--rm`) with two databases: `current_db` and `model_db`.
3. **`current_db` = the snapshot** — applies `db.changelog-master.xml` with Liquibase
   `update`. This is the state your migrations describe today.
4. **`model_db` = the model** — loads `model-schema.sql`.
5. **Diff** — `diff-changelog` from `current_db` → `model_db` produces the delta
   changelog (only what the model has that the migrations don't).

## After generating — review, like any EF migration

Liquibase's `diff-changelog` is a starting point, not gospel. Always check:

- **Renames** appear as `DROP COLUMN` + `ADD COLUMN` → **data loss**. Replace with a
  `renameColumn` by hand.
- **`modifyDataType` direction is unreliable in Liquibase 5.0.3** — verify the target
  type is what you actually want (e.g. `timestamp with time zone` for `Instant`).
- Drop anything you didn't intend (the diff reports *every* structural difference).

Then add it to the master changelog, in order:

```xml
<include file="generated/<timestamp>-<Name>.postgresql.sql" relativeToChangelogFile="true"/>
```

Re-running the script afterwards should now yield an **empty** delta — that is the
signal that the migrations and the model agree (EF's "no changes" state).

## Requirements

- Docker (OrbStack/Docker Desktop). The script reaches the container via
  `host.docker.internal`.
- The Postgres JDBC driver in the Gradle cache (any prior build resolves it).
- Liquibase image pinned to `5.0.3` to match the Spring-managed runtime version.
