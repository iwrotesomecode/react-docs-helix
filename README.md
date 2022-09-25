# React Docs in ClojureScript Helix

Worked examples from the [(beta) React Docs](https://beta.reactjs.org/learn) using [Helix](https://github.com/lilactown/helix), a modern and optimized library for React development in ClojureScript. 

Minimal deployed example in Helix [here.](https://josephdumont.com/helix)
### part1.cljs 
[Quick Start](https://beta.reactjs.org/learn)
Creating basic components, conditional rendering, basic click events, useState and state placement. 

### part2.cljs
[Thinking in React](https://beta.reactjs.org/learn/thinking-in-react)
Breaking the UI into component hierarchy,
filter and map data within an interactive display.

### part3.cljs 
[Describing the UI](https://beta.reactjs.org/learn/describing-the-ui)
Configuring component attributes, passing props, conditional rendering, using map/filter with lists.

### part4.cljs 
[Adding Interactivity](https://beta.reactjs.org/learn/adding-interactivity)
Responding to user events.

### part5.cljs
[Managing State](https://beta.reactjs.org/learn/managing-state)
Sharing state between components: useContext, useReducer.

### part6.cljs
[Escape Hatches](https://beta.reactjs.org/learn/escape-hatches)
Synchronizing components with systems outside React: useRef, forwardRef, useEffect, custom hooks.
Dealing with race conditions and outdated API calls.

### TODO
- More custom hook examples in the final subsections of "Escape Hatches"
- Example of `useSyncExternalStore`
- Replace `main.css` with Tailwindcss  

## Tooling 
[helix](https://github.com/lilactown/helix)

[![Clojars Project](https://img.shields.io/clojars/v/lilactown/helix.svg)](https://clojars.org/lilactown/helix)

[shadow-cljs](https://github.com/thheller/shadow-cljs)

[![](https://img.shields.io/badge/Clojurians-shadow--cljs-lightgrey.svg)](https://clojurians.slack.com/messages/C6N245JGG/)
[![npm](https://img.shields.io/npm/v/shadow-cljs.svg)](https://github.com/thheller/shadow-cljs)
[![Clojars Project](https://img.shields.io/clojars/v/thheller/shadow-cljs.svg)](https://clojars.org/thheller/shadow-cljs)

[promesa](https://github.com/funcool/promesa)

promise/future library to simulate API calls with delays (e.g. `fetch-data` in `part6.cljs`) 

[![Clojars Project](http://clojars.org/funcool/promesa/latest-version.svg)](http://clojars.org/funcool/promesa)

### Quickstart

With these requirements installed on your system:

- [node.js](https://nodejs.org) (v6.0.0+, most recent version preferred)
- [npm](https://www.npmjs.com) (comes bundled with `node.js`) or [yarn](https://www.yarnpkg.com)
- [Java SDK](https://adoptium.net/) (Version 11+, Latest LTS Version recommended)

Run the following commands to setup a new project:

```
npx create-cljs-project <project-name>
cd <project-name> 
npm install react react-refresh react-dom 
```

Where `<project-name>` is `react-docs-helix` in this project. 

Check the `shadow-cljs` docs to setup `shadow-cljs.edn` for your project (or copy the template here). Then you can interactively watch your app refresh as you develop it. 

```
npx shadow-cljs watch app
```
