// Rasterises a hand-authored SVG diagram to a high-resolution PNG for embedding
// in the assignment report (Word docs need raster/embeddable images, not raw SVG).
const { chromium } = require('playwright');
const path = require('path');
const fs = require('fs');

const [,, svgPath, outPath, scale] = process.argv;
if (!svgPath || !outPath) {
    console.error('Usage: node render-svg.js <input.svg> <output.png> [scale]');
    process.exit(1);
}

const svg = fs.readFileSync(svgPath, 'utf8');
const viewBoxMatch = svg.match(/viewBox="0 0 (\d+) (\d+)"/);
const width = viewBoxMatch ? parseInt(viewBoxMatch[1], 10) : 1400;
const height = viewBoxMatch ? parseInt(viewBoxMatch[2], 10) : 900;

(async () => {
    const browser = await chromium.launch();
    const page = await browser.newPage({
        viewport: { width, height },
        deviceScaleFactor: scale ? parseFloat(scale) : 2,
    });
    await page.setContent(`<!DOCTYPE html><html><head><style>
        html,body{margin:0;padding:0;background:#ffffff;}
    </style></head><body>${svg}</body></html>`);
    await page.screenshot({ path: outPath });
    await browser.close();
    console.log('rendered', outPath);
})();
