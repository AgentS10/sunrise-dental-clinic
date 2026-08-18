// Rasterises a local HTML file's `.window` element to a PNG (used for the
// terminal-style evidence screenshots of real command output in the report).
const { chromium } = require('playwright');
const path = require('path');

const [,, htmlPath, outPath, selector] = process.argv;
if (!htmlPath || !outPath) {
    console.error('Usage: node render-html.js <input.html> <output.png> [selector=.window]');
    process.exit(1);
}

(async () => {
    const browser = await chromium.launch();
    const page = await browser.newPage({ deviceScaleFactor: 2 });
    await page.goto('file://' + path.resolve(htmlPath));
    const el = await page.$(selector || '.window');
    await el.screenshot({ path: outPath });
    await browser.close();
    console.log('rendered', outPath);
})();
