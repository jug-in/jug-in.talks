const fs = require('fs');
const talks = fs.readdirSync("talks").filter(file => file.match(/(.*\.(adoc))/ig)).map(file => "talks/" + file);

const asciidoctor = require('asciidoctor.js')();
const asciidoctorRevealjs = require('asciidoctor-reveal.js');
asciidoctorRevealjs.register()

const renderOptions = { safe: 'safe', backend: 'revealjs' };

renderAllTalks()

if (process.argv[2] == "watch") {
    watchAndRenderContinuous()
}

function renderAllTalks() {
    talks.forEach(talk => render(talk));
}

function render(talk) {
    console.log(Date() + " - Rendering " + talk);
    asciidoctor.convertFile(talk, renderOptions);
    let html = talk.split(".adoc")[0] + ".html";
    fs.renameSync(html, html.split("/")[1]);
}

function watchAndRenderContinuous() {
    let fsWait = false;
    talks.forEach(talk => {
        console.log("Watching: " + talk)
        fs.watch(talk, (e, f) => {
            if (fsWait) {
                return;
            } else {
                fsWait = setTimeout(() => {
                    fsWait = false;
                }, 250);
                render(talk);
            }
        });
    });
    fs.watch("talks/_theme", (e, f) => {
        if (fsWait) {
            return;
        } else {
            fsWait = setTimeout(() => {
                fsWait = false;
            }, 250);
            renderAllTalks();
        }
    });
}