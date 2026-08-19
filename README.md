# forgekit-android

An Android starter kit that carries the ForgeKit AI development workflow. Kotlin, Jetpack
Compose with Material 3, and a Gradle build whose versions are declared once — plus OpenSpec,
CodeGraph, and the pre-push coverage hook that the rest of the ForgeKit family runs.

## Setup

```bash
git clone https://github.com/Zuexx/forgekit-android.git
cd forgekit-android
echo "sdk.dir=$HOME/Library/Android/sdk" > local.properties
pnpm install
git config core.hooksPath .githooks
pnpm exec codegraph init
pnpm preflight          # exits non-zero until the workflow genuinely works
pnpm verify             # assemble, test, confirm the tests ran
```

Requires a JDK and the Android SDK. The Gradle wrapper is committed, so no Gradle install is
needed.

## Generating a product

Fork, then change three lines:

```kotlin
// settings.gradle.kts
rootProject.name = "YourApp"

// app/build.gradle.kts
namespace = "com.yourcompany.yourapp"
applicationId = "com.yourcompany.yourapp"
```

Rename the package directories under `app/src/` to match, then `pnpm verify`.

Keep pulling base improvements afterwards:

```bash
git remote add upstream https://github.com/Zuexx/forgekit-android.git
git fetch upstream && git merge upstream/main
```

## Layout

```
settings.gradle.kts        modules, and the product name
gradle/libs.versions.toml  every dependency and plugin version, declared once
app/build.gradle.kts       the app module: SDK levels, namespace, dependencies by alias
app/src/main/.../app/      MainActivity
app/src/main/.../samples/  the one sample feature: model, provider interface, view model, screen
app/src/test/              JUnit unit tests
scripts/verify.sh          the acceptance gate, and what CI runs
scripts/preflight.sh       shared; reports whether the workflow is operational
scripts/sync-workflow.sh   shared; pulls workflow updates from forgekit-workflow
openspec/                  specifications, and the rules that shape them
AGENTS.md                  instructions for agents working in this repository
```

`local.properties` is machine-specific and gitignored.

## The sample feature

`SampleListScreen` renders whatever a `SampleResourceProvider` returns, through a view model
whose state is a sealed interface exposed as a `StateFlow`. It exists to prove the chain from
provider to view model to screen is wired and tested — replace it with a real domain rather
than building around it.

## Why verify.sh reads the test report

Gradle reports `BUILD SUCCESSFUL` when a test task runs zero tests. An acceptance gate whose
only signal is an exit code would pass a repository whose tests had silently stopped being
discovered, so `verify.sh` reads the result XML and fails when it finds no tests.

## The workflow

`AGENTS.md` is the entry point. In short: `/opsx:propose` to plan a change, `/opsx:apply` to
implement it, `codegraph_explore` instead of reading files to find what a change affects, and
`pnpm verify` before calling anything done.

Shared workflow files come from
[forgekit-workflow](https://github.com/Zuexx/forgekit-workflow) and are updated with
`pnpm sync-workflow`. Edit them there, not here.
