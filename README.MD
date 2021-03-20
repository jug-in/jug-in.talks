# Talks and Presentations of the Java User Group Ingolstadt e.V.

## View
* Offline/Local: Checkout the complete repository and open one of the top level HTML files.
* Online: Browse to [Github Pages](https://jug-in.github.io/jug-in.talks/) for an overview of all talks.

## Edit
The slides are build with https://asciidoctor.org/docs/asciidoctor-revealjs/, use the existing ones under /talks as example.

## Render
For rendering the Asciidoctor files to reveal.js HTML presentations a Node.js installation is required.
* Initialize the dependencies first: `npm install`
* Render all talks at once: `node render-talks.js`
* Continuously watch for changes and render on demand: `node render-talks.js watch`

Generated html files are not checked in but rendered and published from the `master` branch by a [GitHub action](.github/workflows/render.yml).

# Don't forget to visit our appointments: https://jug-in.bayern