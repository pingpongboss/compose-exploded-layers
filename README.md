# 💣 Exploded Layers for Jetpack Compose

[![License: MIT](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.0+-purple.svg)](https://kotlinlang.org)
[![Compose](https://img.shields.io/badge/Jetpack%20Compose-1.7+-teal.svg)](https://developer.android.com/jetpack/compose)

Turn any composable into an **interactive “3D exploded view”**.

---

## ✨ Overview

`compose-exploded-layers` lets you visually **separate your UI into layers**, spaced along an offset
vector — rendering your composable tree in 3D space.

Use it to:

- Reveal structure or hierarchy within complex layouts
- Create dynamic visualizations for cards, lists, or photos
- Add depth to transitions, intros, or editor UIs

<p align="center">
  <picture>
    <source srcset="./assets/demo.gif" type="image/gif">
    <img src="./assets/demo.jpg" width="600" alt="Exploded layers demo">
  </picture>
</p>

---

## 🚀 Features

✅ Animate between *collapsed* and *exploded* states  
✅ Draggable 2D exploration (pan and drag to rotate layers)  
✅ Customizable spread distance, easing, and axis  
✅ Lightweight — built only with Compose primitives  
✅ Works with any composable tree

---

## 📲 Sample App

[![Latest Release](https://img.shields.io/github/v/release/pingpongboss/compose-exploded-layers?color=brightgreen&label=release)](https://github.com/pingpongboss/compose-exploded-layers/releases)

Try out the sample app to see exploded layers in action:

- **Download the latest APK** from the release assets.
- Or **build and install locally** using: `./gradlew :sample:installDebug` (you can also run the
  sample configuration directly from Android Studio.)

The [`sample/`](sample) module demonstrates:

* Interactive explosion control via sliders
* Layer depth visualizations
* How to mark existing Composables with distinct visual layers

---

## 📦 Integration

[![JitPack](https://jitpack.io/v/pingpongboss/compose-exploded-layers.svg)](https://jitpack.io/#pingpongboss/compose-exploded-layers)

Add the library to your project with 2 easy steps.

**Step 1.** Add the JitPack repository to your build file:

```kotlin
// settings.gradle.kts
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
    }
}
```

**Step 2.** Add the latest version of the dependency to your module’s build file:

```kotlin
// build.gradle.kts
dependencies {
    implementation("com.github.pingpongboss:compose-exploded-layers:<latest-version>")
}
````

*💡 Tip: Replace `<latest-version>` with the version shown on the badge above.*

*ℹ️ For more details, visit
the repo's [main JitPack page](https://jitpack.io/#pingpongboss/compose-exploded-layers)*.

---

## 📝 Basic Usage

```kotlin
@Composable
fun MyCustomButton() {
    val state = rememberExplodedLayersState()

    ExplodedLayersRoot(state) {
        Box(
            Modifier.background(Color.Red) // Base layer.
                .separateLayer().background(Color.Blue) // Middle layer.
        ) {
            SeparateLayer { Text("Hello world") } // Top layer.
        }
    }
}

```

---

## ⚙️ API Highlights

| Component                       | Description                                                                  |
|---------------------------------|------------------------------------------------------------------------------|
| `ExplodedLayersRoot()`          | Root container that applies 3D offset transforms to the layers nested within |
| `Modifier.separateLayer()`      | Modifier that marks everything after it as belonging to a separate layer     |
| `SeparateLayer()`               | Composable that marks its children as belonging to a separate layer          |
| `rememberExplodedLayersState()` | Composable factory to create and remember state                              |
| `ExplodedLayersState`           | Holds configuration and state (`offset`, `spread`, etc.)                     |

---

## ⚙️ Configurable Behaviors

`rememberExplodedLayersState()` and `ExplodedLayersState` exposes these configurable behaviors:

* `interactive` — Whether you can drag the exploded view to control its `offset`.
* `offset` — Direction of explosion (e.g., `DpOffset(x = -40.dp, y = 40.dp)`).
* `spread` — Controls how far layers separate (0f–1f).

---

## 🤝 Contributing

Contributions welcome!
To get started:

1. Fork the repo
2. Create a new feature branch
3. Add your implementation and test with the sample app
4. Submit a Pull Request ✨

Please follow the existing code style and include KDoc for new public APIs.

---

## 🌐 Links

* 📘 **GitHub:**
  [pingpongboss/compose-exploded-layers](https://github.com/pingpongboss/compose-exploded-layers)
* 💬 **Issues / Feedback:**
  [Open an issue](https://github.com/pingpongboss/compose-exploded-layers/issues)
* 🧠 **Author:**
  [Mark Wei](https://www.linkedin.com/in/markwei/)
* ✨ **Inspiration:**
    * [Alvish Baldha - Keycap Button (Dribbble)](https://dribbble.com/shots/25117095)
    * [Clint Hess - Button Hover Effects (Dribbble)](https://dribbble.com/shots/25326661-Button-Hover-Effects)
    * [Syntax - CSS Challenge (Youtube)](https://www.youtube.com/watch?v=xtRx-aNrNe8)

---

> *“Compose your UI — then explode it.”*