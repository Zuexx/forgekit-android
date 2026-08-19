# Agent Instructions

forgekit-android is an Android starter kit used as a development base. Products are generated
from it by forking and renaming, then diverge, so changes should stay reusable across products
rather than being tailored to the sample resource domain.

## Read before changing the build

Two things about this build are easy to get wrong and both fail loudly only once:

- **AGP 9 supplies Kotlin itself.** Applying `org.jetbrains.kotlin.android` fails the build
  with a message saying so. The Compose compiler plugin is still applied separately.
- **Every version is declared once**, in `gradle/libs.versions.toml`. Module build files
  reference aliases and never carry a version literal. A version written in two build files is
  the one a dependency update reaches only half of — which is the failure that made the .NET
  member of this family adopt central package management.

Renaming a generated product is three lines: `rootProject.name` in `settings.gradle.kts`, and
`namespace` plus `applicationId` in `app/build.gradle.kts`.

## Planning changes

Specifications live in `openspec/`. Project context, artifact rules, and per-operation guidance
are in `openspec/config.yaml`, and OpenSpec delivers them at the step they apply to — read what
it hands you rather than working from memory of this file.

`/opsx:explore` to think a change through, `/opsx:propose` to create one, `/opsx:apply` to
implement, `/opsx:archive` when it ships.

When a request is too vague for a proposal to state what it includes and excludes, run
`grillme` first — `pnpm exec grillme` from the repository root. It opens a browser, asks one
decision question at a time, and writes a Markdown handoff; it implements nothing. That handoff
is the input to `/opsx:propose`.

Write a change proposal for new capabilities, breaking changes, architecture shifts, and
security work. Skip it for bug fixes, typos, dependency bumps, and configuration changes.

### Two loops, and who owns which

OpenSpec decides **what may be built and whether it counts as done**. Superpowers decides
**how it gets built and whether it was built correctly**. Neither knows the other exists, so
the seam is the `apply` and `archive` guidance in `openspec/config.yaml`.

Feature-level tasks live in `openspec/changes/<slug>/tasks.md`. Minute-level steps live in the
Superpowers plan, which cites those task ids under `## OpenSpec Coverage`. Keep the two
granularities apart; collapsing them makes the citation meaningless.

### Overlapping skills, resolved by trigger

| Job | Use | Because |
|---|---|---|
| Clarify a vague request | `superpowers:brainstorming` | Fires on its own before creative work |
| Test-first implementation | `superpowers:test-driven-development` | The inner loop already speaks its vocabulary |
| Execute a plan | `superpowers:subagent-driven-development`, **if its decision tree sends you there** | It routes tightly coupled tasks to manual execution; a task and its own verification are not independent |
| Review a change | `superpowers:requesting-code-review` | Whichever route the work took, including small changes done inline |
| Review an arbitrary diff | `/code-review` | Ad-hoc, outside a change |

Reach for `codegraph_explore` before reading files to answer "what does this affect" — it
returns the callers and the test-coverage gaps that reading cannot, in one call. It indexes
symbols, so a contract addressed by string — an intent action, a preference key, a route — is
invisible to it and needs a literal search of its own.

## Building and testing

```bash
pnpm install         # the workflow toolchain: OpenSpec, CodeGraph, grillme
pnpm verify          # assemble, unit test, then confirm the tests actually ran
```

`pnpm verify` is the acceptance gate and is what CI runs. It does one thing Gradle does not:
**it reads the test report afterwards.** A test task that runs zero tests still reports
`BUILD SUCCESSFUL`, so the exit code alone cannot say whether anything was verified.

`local.properties` points at this machine's Android SDK and is never committed. Create it once:

```bash
echo "sdk.dir=$HOME/Library/Android/sdk" > local.properties
```

`.githooks/pre-push` checks that implementation plans cite OpenSpec task ids that actually
resolve — the one link between the two systems that nothing else validates. Enabling it takes
two steps, because git ignores a hook it cannot execute without reporting anything:

```bash
git config core.hooksPath .githooks
chmod +x .githooks/*
```

`pnpm preflight` reports whether this workflow is operational here at all — the declared tools,
whether `openspec/config.yaml` still yields its rules through the installed version, whether
the CodeGraph index reflects current Kotlin source, whether the hooks can fire, and whether
every capability this file and `openspec/config.yaml` name still resolves. Run it after
cloning, and when something in the workflow behaves as though a piece is missing. Each failure
names its fix.

Invoke both through pnpm rather than as `./scripts/*.sh`, so that a copy which arrived without
the executable bit still runs.

## The shared workflow

`scripts/preflight.sh`, `scripts/sync-workflow.sh`, `.githooks/pre-push`, `.mcp.json`,
`.claude/settings.json`, and `openspec/rules.yaml` are owned by the forgekit-workflow
repository and shared with every ForgeKit-family repo. Edit them there, not here:

```bash
pnpm sync-workflow && pnpm preflight
```

overwrites them and re-splices the shared rules into `openspec/config.yaml` below the marker
line, so a local edit disappears without a word. What this repository owns is everything above
that marker — its `context:` block — plus `scripts/verify.sh`, `package.json`, and this file.

## Conventions that are easy to get wrong

- **The view model depends on an interface, never a concrete provider.** `SampleListViewModel`
  takes a `SampleResourceProvider`. That indirection is what lets it be tested without an
  emulator or the Android framework.
- **State is a sealed interface, not a set of booleans.** Idle, Loading, Loaded, Failed are
  cases a test asserts directly; `isLoading` plus `error` plus `items` has states that cannot
  happen and tests that cannot say so.
- **Loading suspends rather than launching.** A unit test awaits it directly, with no
  main-dispatcher rule and nothing to synchronise.
- **Assert the value, not the absence.** A test that only checks nothing failed still passes
  when the view model publishes an empty list.
- **Commits follow Conventional Commits.** Branch off main and open a PR so CI runs before
  merging.

## Where things are documented

| Topic | File |
|---|---|
| Setting up, and generating a product | `README.md` |
| Every dependency and plugin version | `gradle/libs.versions.toml` |
| Module configuration | `app/build.gradle.kts` |
