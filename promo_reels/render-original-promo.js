const fs = require("fs");
const path = require("path");
const { chromium } = require("playwright-core");
const { spawnSync } = require("child_process");

const rootDir = path.resolve(__dirname, "..");
const reelDir = path.join(rootDir, "promo_reels");
const htmlPath = path.join(reelDir, "original-promo.html");
const framesDir = path.join(reelDir, "original_frames");
const outputPath = path.join(reelDir, "bd-tax-calculator-original-reels-1080x1920.mp4");
const fps = 30;
const duration = 14;
const totalFrames = Math.round(fps * duration);
const chromeExecutable = "/Applications/Google Chrome.app/Contents/MacOS/Google Chrome";

function cleanFramesDir() {
  fs.rmSync(framesDir, { recursive: true, force: true });
  fs.mkdirSync(framesDir, { recursive: true });
}

function runFfmpeg() {
  const framePattern = path.join(framesDir, "frame-%05d.jpg");
  const args = [
    "-y",
    "-framerate",
    String(fps),
    "-i",
    framePattern,
    "-f",
    "lavfi",
    "-i",
    "anullsrc=channel_layout=stereo:sample_rate=44100",
    "-t",
    String(duration),
    "-c:v",
    "libx264",
    "-profile:v",
    "high",
    "-level",
    "4.2",
    "-preset",
    "medium",
    "-crf",
    "18",
    "-pix_fmt",
    "yuv420p",
    "-c:a",
    "aac",
    "-b:a",
    "128k",
    "-movflags",
    "+faststart",
    outputPath,
  ];

  const result = spawnSync("ffmpeg", args, { stdio: "inherit" });
  if (result.status !== 0) {
    throw new Error(`ffmpeg failed with exit code ${result.status}`);
  }
}

async function main() {
  if (!fs.existsSync(chromeExecutable)) {
    throw new Error(`Chrome executable not found: ${chromeExecutable}`);
  }

  cleanFramesDir();

  const browser = await chromium.launch({
    executablePath: chromeExecutable,
    headless: true,
    args: [
      "--allow-file-access-from-files",
      "--disable-web-security",
      "--font-render-hinting=none",
    ],
  });

  const page = await browser.newPage({
    viewport: { width: 1080, height: 1920 },
    deviceScaleFactor: 1,
  });

  await page.goto(`file://${htmlPath}`);
  await page.evaluate(() => document.fonts.ready);

  for (let i = 0; i < totalFrames; i += 1) {
    const t = i / fps;
    await page.evaluate((time) => window.renderFrame(time), t);
    await page.screenshot({
      path: path.join(framesDir, `frame-${String(i + 1).padStart(5, "0")}.jpg`),
      type: "jpeg",
      quality: 95,
    });

    if (i % fps === 0) {
      process.stdout.write(`Captured ${i}/${totalFrames} frames\r`);
    }
  }

  await browser.close();
  process.stdout.write(`Captured ${totalFrames}/${totalFrames} frames\n`);
  runFfmpeg();
  console.log(outputPath);
}

main().catch((error) => {
  console.error(error);
  process.exit(1);
});
