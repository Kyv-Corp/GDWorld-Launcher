# LauncherPrestarter

GravitLauncher prestarter written in Rust with [Tauri](https://v2.tauri.app/).

## Clone

```bash
git clone -b rust/5.7.x https://github.com/GravitLauncher/LauncherPrestarter.git
```

## Windows development environment

- Install [Visual Studio](https://visualstudio.microsoft.com/) (not Visual Studio Code) with the C++ desktop development workload.
- Follow the [Rust installation instructions](https://rust-lang.org/tools/install/).
- Install [NodeJS](https://nodejs.org/en/download/current).
- Install Yarn and dependencies from the prestarter directory:

```bash
npm install --global yarn
yarn
```

## Debugging and building

Run `yarn tauri dev` for debug mode. For debugging convenience, the download window is always shown. To prevent Java downloading while debugging, comment out `setTimeout(startDownload, appConfig.download.initialDelay);` in src/App.svelte, and restore the line afterwards.

Run `yarn tauri build` to build the final EXE in src-tauri/target/release.

## Design customization

Recommended IDE setup: [VS Code](https://code.visualstudio.com/) with [Svelte](https://marketplace.visualstudio.com/items?itemName=svelte.svelte-vscode), [Tauri](https://marketplace.visualstudio.com/items?itemName=tauri-apps.tauri-vscode) and [rust-analyzer](https://marketplace.visualstudio.com/items?itemName=rust-lang.rust-analyzer).

src/ contains the Svelte frontend, compiled to HTML/CSS/JS by Vite. src-tauri/ contains the Rust backend.

Useful references: [Svelte](https://svelte.dev/), [Tauri](https://v2.tauri.app/), [Rust](https://rust-lang.org/), [CSS](https://developer.mozilla.org/en-US/docs/Web/CSS), [HTML](https://developer.mozilla.org/en-US/docs/Web/HTML), [JavaScript](https://developer.mozilla.org/en-US/docs/Web/JavaScript).

## Icons

The in-app logo is src/lib/assets/images/logo.svg. Generate the taskbar icon with:

```bash
yarn tauri icon PATH_TO_ICON_PNG
```
