#!/usr/bin/env python3
"""Render vertical social promo videos from real Tax Calculator BD captures."""

from __future__ import annotations

import os
import subprocess
from pathlib import Path

from PIL import Image, ImageDraw


ROOT = Path(__file__).resolve().parent
RAW = ROOT / "raw"
FINAL = ROOT / "final"
ASSETS = ROOT / ".render-assets"
ICON = ROOT.parent.parent / "app/src/main/play-store/tax-calculator-bd-icon-512.png"
PANGO = "/opt/homebrew/bin/pango-view"
FFMPEG = "/opt/homebrew/bin/ffmpeg"

WIDTH, HEIGHT = 1080, 1920
GREEN = "#0B5D35"
MINT = "#F0F7EA"
WHITE = "#FFFFFF"


PROMOS = {
    "bd-tax-calculator-short-15s.mp4": [
        ("bdtax-home-flow.mp4", 0, 3, "আয়কর হিসাব এখন আরও সহজ"),
        ("bdtax-calculator-flow.mp4", 22, 3, "গত ৫ আয়বর্ষ একসাথে"),
        ("bdtax-calculator-flow.mp4", 62, 3, "আয় ও বোনাস যোগ করুন"),
        ("bdtax-calculator-flow.mp4", 84, 3, "বিনিয়োগ রিবেট দেখুন"),
        ("bdtax-faq-flow.mp4", 20, 3, "৭৯টি Tax FAQ • এখনই ইনস্টল করুন"),
    ],
    "bd-tax-calculator-reel-30s.mp4": [
        ("bdtax-home-flow.mp4", 0, 5, "বাংলাদেশের আয়কর সহায়ক"),
        ("bdtax-home-flow.mp4", 21, 5, "Notice ও সরকারি সেবা এক জায়গায়"),
        ("bdtax-calculator-flow.mp4", 22, 5, "২০২১–২২ থেকে ২০২৫–২৬"),
        ("bdtax-calculator-flow.mp4", 62, 5, "আয়ের তথ্য দিন সহজেই"),
        ("bdtax-calculator-flow.mp4", 84, 5, "বিনিয়োগ ও রিবেট হিসাব"),
        ("bdtax-faq-flow.mp4", 20, 5, "৭৯টি Tax FAQ • Play Store-এ পাওয়া যাচ্ছে"),
    ],
    "bd-tax-calculator-tutorial-60s.mp4": [
        ("bdtax-home-flow.mp4", 0, 8, "Tax Calculator BD"),
        ("bdtax-home-flow.mp4", 17, 8, "Notice ও গুরুত্বপূর্ণ সরকারি সেবা"),
        ("bdtax-calculator-flow.mp4", 22, 8, "আয়বর্ষ নির্বাচন করুন"),
        ("bdtax-calculator-flow.mp4", 48, 8, "করদাতার শ্রেণি বাছুন"),
        ("bdtax-calculator-flow.mp4", 64, 8, "আয়ের তথ্য যোগ করুন"),
        ("bdtax-calculator-flow.mp4", 84, 8, "বিনিয়োগ ও রিবেট হিসাব দেখুন"),
        ("bdtax-faq-flow.mp4", 20, 6, "৭৯টি Tax FAQ"),
        ("bdtax-faq-flow.mp4", 60, 6, "প্রশ্ন খুলে বিস্তারিত পড়ুন"),
    ],
}


def render_text(text: str, output: Path, size: int = 48, color: str = GREEN) -> Image.Image:
    subprocess.run(
        [
            PANGO,
            "--no-display",
            "--background=transparent",
            f"--foreground={color}",
            f"--font=Noto Sans Bengali {size}",
            "--pixels",
            "--align=left",
            f"--text={text}",
            f"--output={output}",
        ],
        check=True,
        env={**os.environ, "XDG_CACHE_HOME": "/tmp", "LC_ALL": "en_US.UTF-8"},
    )
    return Image.open(output).convert("RGBA")


def make_overlay(text: str, output: Path, final_scene: bool) -> None:
    canvas = Image.new("RGBA", (WIDTH, HEIGHT), (0, 0, 0, 0))
    draw = ImageDraw.Draw(canvas)

    icon = Image.open(ICON).convert("RGBA")
    icon.thumbnail((112, 112), Image.Resampling.LANCZOS)
    canvas.alpha_composite(icon, (38, 32))

    text_img = render_text(text, output.with_suffix(".text.png"))
    max_width = 855
    if text_img.width > max_width:
        ratio = max_width / text_img.width
        text_img = text_img.resize(
            (max_width, max(1, round(text_img.height * ratio))), Image.Resampling.LANCZOS
        )
    canvas.alpha_composite(text_img, (170, max(28, (175 - text_img.height) // 2)))

    if final_scene:
        draw.rounded_rectangle((170, 1810, 910, 1890), radius=40, fill=GREEN)
        cta = render_text(
            "Play Store থেকে ইনস্টল করুন",
            output.with_suffix(".cta.png"),
            size=34,
            color=WHITE,
        )
        canvas.alpha_composite(cta, ((WIDTH - cta.width) // 2, 1819))

    canvas.save(output)


def render_video(filename: str, segments: list[tuple[str, int, int, str]]) -> None:
    overlay_paths: list[Path] = []
    for index, (_, _, _, caption) in enumerate(segments):
        overlay = ASSETS / f"{Path(filename).stem}-{index}.png"
        make_overlay(caption, overlay, index == len(segments) - 1)
        overlay_paths.append(overlay)

    command = [FFMPEG, "-y", "-hide_banner", "-loglevel", "warning"]
    for source, _, _, _ in segments:
        command += ["-i", str(RAW / source)]
    for overlay, (_, _, duration, _) in zip(overlay_paths, segments):
        command += ["-loop", "1", "-framerate", "30", "-t", str(duration), "-i", str(overlay)]

    filters: list[str] = []
    segment_labels: list[str] = []
    overlay_offset = len(segments)
    for index, (_, start, duration, _) in enumerate(segments):
        filters.append(
            f"[{index}:v]trim=start={start}:duration={duration},setpts=PTS-STARTPTS,"
            f"fps=30,scale=756:1680:flags=lanczos,"
            f"pad={WIDTH}:{HEIGHT}:162:180:color=0xF0F7EA,setsar=1[base{index}]"
        )
        filters.append(
            f"[base{index}][{overlay_offset + index}:v]"
            f"overlay=0:0:format=auto,trim=duration={duration},"
            f"setpts=PTS-STARTPTS,format=yuv420p[v{index}]"
        )
        segment_labels.append(f"[v{index}]")

    filters.append(
        "".join(segment_labels)
        + f"concat=n={len(segments)}:v=1:a=0[outv]"
    )
    command += [
        "-filter_complex",
        ";".join(filters),
        "-map",
        "[outv]",
        "-an",
        "-c:v",
        "libx264",
        "-preset",
        "veryfast",
        "-crf",
        "21",
        "-movflags",
        "+faststart",
        str(FINAL / filename),
    ]
    subprocess.run(command, check=True)


def main() -> None:
    FINAL.mkdir(parents=True, exist_ok=True)
    ASSETS.mkdir(parents=True, exist_ok=True)
    for filename, segments in PROMOS.items():
        print(f"Rendering {filename}…", flush=True)
        render_video(filename, segments)
    print(f"Finished: {FINAL}", flush=True)


if __name__ == "__main__":
    main()
