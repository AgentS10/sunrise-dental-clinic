// Rasterises static/img/favicon.svg into the PNG sizes browsers/OSes expect
// (favicon-16, favicon-32, apple-touch-icon-180) since a single SVG favicon
// is not yet honoured everywhere (Safari/iOS home-screen icons, some crawlers).
const { chromium } = require('playwright');
const path = require('path');
const fs = require('fs');

const svg = fs.readFileSync(
    path.join(__dirname, '..', 'src', 'main', 'resources', 'static', 'img', 'favicon.svg'), 'utf8');
const outDir = path.join(__dirname, '..', 'src', 'main', 'resources', 'static', 'img');

const sizes = [
    { file: 'favicon-16.png', size: 16 },
    { file: 'favicon-32.png', size: 32 },
    { file: 'apple-touch-icon.png', size: 180 },
];

(async () => {
    const browser = await chromium.launch();
    for (const { file, size } of sizes) {
        const page = await browser.newPage({ viewport: { width: size, height: size } });
        await page.setContent(`<!DOCTYPE html><html><head><style>
            html,body{margin:0;padding:0;background:transparent;}
            svg{display:block;width:${size}px;height:${size}px;}
        </style></head><body>${svg}</body></html>`);
        await page.screenshot({ path: path.join(outDir, file), omitBackground: true });
        await page.close();
        console.log('generated', file);
    }
    await browser.close();
})();
