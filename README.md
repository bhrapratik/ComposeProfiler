# Compose Profiler (source)

Private source for [Compose Profiler](https://github.com/bhrapratik/ComposeProfiler), an on-device performance overlay for Jetpack Compose.

The public repo contains only the README, docs, a sample project and the compiled artifacts (`maven/`). This repo contains the source.

## Modules

| Module | Artifact | Purpose |
|---|---|---|
| `profiler` | `io.github.bhrapratik.composeprofiler:profiler` | The real profiler. Obfuscated with R8 on release. |
| `profiler-noop` | `io.github.bhrapratik.composeprofiler:profiler-noop` | Same public API, empty implementations, for release builds. |
| `sample` | not published | Demo app using the modules directly. |

Public API (everything else is `internal`):

- `ComposeProfiler { content }`
- `TrackRecompositions(name)`
- `Modifier.trackSize(name)`

## Folder layout

The public repo must be cloned next to this one, because publishing writes into `../ComposeProfiler/maven`:

```
ComposeProfiler/
├── ComposeProfiler/        public repo (README, CHANGELOG, docs, sample, maven/)
└── ComposeProfiler-src/    this repo
```

## Release a new version

Always run git commands from the repo root, not from a subfolder like `sample/`.

**In this repo (`ComposeProfiler-src`):**

1. Set `VERSION_NAME` in `gradle.properties`.
2. Build and test the sample (debug and release).
3. Commit and push.
4. Publish into the public repo:
```bash
   ./gradlew clean publishAllPublicationsToPublicRepoRepository
```
5. Check the stdlib version in the `.pom` (must be 2.0.21):
```bash
   grep -A1 "kotlin-stdlib" ../ComposeProfiler/maven/io/github/bhrapratik/composeprofiler/profiler/<version>/profiler-<version>.pom
```
6. Check the obfuscation:
```bash
   rm -rf /tmp/aar && mkdir -p /tmp/aar
   unzip -o -q ../ComposeProfiler/maven/io/github/bhrapratik/composeprofiler/profiler/<version>/profiler-<version>.aar -d /tmp/aar
   unzip -o -q /tmp/aar/classes.jar -d /tmp/aar/classes
   find /tmp/aar/classes -name "*.class"
```
   Only `ComposeProfilerKt` and `TrackingKt` keep their names. Everything else is `a`, `b`, `c`…

**In the public repo (`ComposeProfiler`):**

7. Update the version in `README.md`, `CHANGELOG.md` (newest on top) and `sample/app/build.gradle.kts`.
8. Push **without** a tag (the sample downloads from GitHub, so the files must be online first):
```bash
   git add -A
   git commit -m "Release <version>"
   git push origin main
```
9. Check it's online (can take 1–2 minutes):
```bash
   curl -s https://raw.githubusercontent.com/bhrapratik/ComposeProfiler/main/maven/io/github/bhrapratik/composeprofiler/profiler/<version>/profiler-<version>.pom | head -2
```
10. Sync and run the sample: debug shows the overlay, release has no overlay.
11. Tag:
```bash
    git tag <version>
    git push origin --tags
```

## Build notes

- **Keep rules:** `profiler/src/main/keepRules/rules.keep` keeps the public API and Kotlin metadata. It is referenced explicitly in `proguardFiles(...)`, because it was not picked up automatically.
- **Empty `classes.jar`:** the keep rules were not applied, so R8 removed everything as unused.
- **Kotlin compatibility:** both library modules use:
```kotlin
  kotlin {
      explicitApi()
      coreLibrariesVersion = "2.0.21"
      compilerOptions {
          languageVersion.set(KotlinVersion.KOTLIN_2_0)
          apiVersion.set(KotlinVersion.KOTLIN_2_0)
      }
  }
```
  `languageVersion`/`apiVersion` alone are not enough: without `coreLibrariesVersion`, the `.pom` requires the newest `kotlin-stdlib`, and apps on older Kotlin fail with "compiled with an incompatible version of Kotlin". The "language version 2.0 is deprecated" warning is expected. When Kotlin removes 2.0 support, move to 2.2 and update the public README.
- **Sample release crash:** do not use `optimization { packageScope = ... }` from the project template. It caused an `IllegalAccessError` in Kotlin stdlib classes at startup.
- **No sources jar:** `singleVariant("release")` without `withSourcesJar()`, so source is never published.
- **Never commit:** `local.properties`, `maven/`, recordings (`*.mp4`), `.DS_Store`.

## Versions

| Version | Change |
|---|---|
| 0.1.0 | First release |
| 0.1.1 | Compiled for Kotlin 2.0 compatibility |
| 0.1.2 | Depends on kotlin-stdlib 2.0.21 |

## License

Copyright 2026 Pratik Behera. All rights reserved.
