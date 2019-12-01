const slides = ["java.time.adoc"]

const asciidoctor = require('asciidoctor.js')();
const asciidoctorRevealjs = require('asciidoctor-reveal.js');
asciidoctorRevealjs.register()

renderSlides()

if (process.argv[2] == "watch") {
    watchAndRenderContinuous()
} 

function renderSlides() {
    const renderOptions = { safe: 'safe', backend: 'revealjs' };
    slides.forEach(talk => {
        console.log(Date() + " - Rendering " + talk);
        asciidoctor.convertFile(talk, renderOptions);
    });
}

function watchAndRenderContinuous() {
    const fs = require('fs');

    let fsWait = false;
    fs.readdirSync(".").filter(file => file.match(/(.*\.(adoc|css))|(theme)/ig)).forEach(file => {
        console.log("Watching: " + file)
        fs.watch(file, (e, f) => {
            if (fsWait) {
                return;
            } else {
                fsWait = setTimeout(() => {
                    fsWait = false;
                }, 250);
                renderSlides();
            }
        });
    });
}