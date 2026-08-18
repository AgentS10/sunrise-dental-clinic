// Renders a .mmd Mermaid definition file to a high-resolution PNG, using
// mermaid.js in a headless browser (loaded from CDN - internet required to run
// this script; the generated PNGs are what actually gets embedded in the report,
// so the running application has no dependency on this).
//
// Important: the screenshot is taken in the SAME page/document where
// mermaid.render() ran. Mermaid's returned <svg> string relies on CSS custom
// properties mermaid injects into the page's global stylesheet - extracting the
// svg string and re-inserting it into a brand new page loses that stylesheet,
// which silently renders every node as solid black.
const { chromium } = require('playwright');
const fs = require('fs');

const [,, mmdPath, outPath] = process.argv;
if (!mmdPath || !outPath) {
    console.error('Usage: node render-mermaid.js <input.mmd> <output.png>');
    process.exit(1);
}

const definition = fs.readFileSync(mmdPath, 'utf8');

(async () => {
    const browser = await chromium.launch();
    const page = await browser.newPage({ deviceScaleFactor: 2 });
    page.on('pageerror', err => console.error('PAGE ERROR:', err.message));

    await page.setContent(`<!DOCTYPE html><html><head><style>
        html,body{margin:0;padding:0;background:#ffffff;}
        #dgm{padding:24px;background:#ffffff;display:inline-block;}
    </style></head><body><div id="dgm"></div></body></html>`);
    await page.addScriptTag({ url: 'https://cdn.jsdelivr.net/npm/mermaid@10/dist/mermaid.min.js' });

    await page.evaluate(async (src) => {
        mermaid.initialize({
            startOnLoad: false,
            theme: 'base',
            themeVariables: {
                primaryColor: '#f0fdfa',
                primaryBorderColor: '#0d9488',
                primaryTextColor: '#0b3d3a',
                lineColor: '#1e3a5f',
                secondaryColor: '#fffaf0',
                tertiaryColor: '#f2f7ff',
                actorBkg: '#0d9488',
                actorTextColor: '#ffffff',
                actorBorder: '#0b3d3a',
                signalColor: '#1e3a5f',
                signalTextColor: '#1f2933',
                labelBoxBkgColor: '#f0fdfa',
                labelBoxBorderColor: '#0d9488',
                labelTextColor: '#0b3d3a',
                loopTextColor: '#0b3d3a',
                activationBorderColor: '#0d9488',
                activationBkgColor: '#ccfbf1',
                noteBkgColor: '#fffaf0',
                noteBorderColor: '#b98900',
                fontFamily: 'Segoe UI, Arial, sans-serif',
            },
        });
        const { svg } = await mermaid.render('renderedGraph', src);
        document.getElementById('dgm').innerHTML = svg;

        // mermaid emits width="100%" with no absolute size, which collapses to
        // the browser's tiny default replaced-element box inside our
        // display:inline-block container. Pin explicit pixel dimensions from
        // the viewBox so the diagram renders at its true natural size.
        const svgEl = document.querySelector('#dgm svg');
        const viewBox = svgEl.getAttribute('viewBox').split(/\s+/).map(Number);
        svgEl.removeAttribute('style');
        svgEl.setAttribute('width', String(viewBox[2]));
        svgEl.setAttribute('height', String(viewBox[3]));
    }, definition);

    await page.waitForTimeout(200);
    const el = await page.$('#dgm');
    await el.screenshot({ path: outPath });
    await browser.close();
    console.log('rendered', outPath);
})();
