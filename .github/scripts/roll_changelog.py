#!/usr/bin/env python3
"""Roll the CHANGELOG "Unreleased" section into a dated version section.

Usage: roll_changelog.py <version> <date-YYYY-MM-DD>

Rewrites CHANGELOG.md in place: the "## Unreleased" heading is kept (empty,
ready for the next cycle) and its former contents move under a new
"## [<version>] - <date>" heading. The moved contents are also written to
release-notes.md for use as the GitHub Release body.
"""
import pathlib
import sys


def main() -> int:
    if len(sys.argv) != 3:
        print(__doc__, file=sys.stderr)
        return 2

    version, date = sys.argv[1], sys.argv[2]
    path = pathlib.Path("CHANGELOG.md")
    lines = path.read_text().splitlines()

    out: list[str] = []
    notes: list[str] = []
    in_unreleased = False
    for line in lines:
        if line.strip() == "## Unreleased":
            out.append(line)
            out.append("")
            out.append(f"## [{version}] - {date}")
            in_unreleased = True
            continue
        if in_unreleased and line.startswith("## "):
            in_unreleased = False
        if in_unreleased:
            notes.append(line)
        out.append(line)

    body = "\n".join(notes).strip()
    if not body:
        print("No entries under '## Unreleased' - aborting.", file=sys.stderr)
        return 1

    path.write_text("\n".join(out).rstrip() + "\n")
    pathlib.Path("release-notes.md").write_text(body + "\n")
    print(body)
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
