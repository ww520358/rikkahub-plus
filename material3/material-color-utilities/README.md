# material-color-utilities (vendored)

Vendored sources from the Material Color Utilities project:
https://github.com/material-foundation/material-color-utilities

Pinned commit: `6fd88eb3e95ba1d457842e2a2bf847d06b3a018a`
(same revision as the upstream rikkahub submodule reference)

Only the `kotlin/` directory is consumed, by the `material3` module via:

```kotlin
android {
    sourceSets { named("main") { kotlin.srcDir("material-color-utilities/kotlin") } }
}
```

The upstream repository references this project as a git submodule; it is vendored here as plain files so CI builds do not require submodule checkout support.

License: Apache-2.0 (see `kotlin/LICENSE`)
