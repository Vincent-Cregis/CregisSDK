#!/usr/bin/env python3
"""Check release metadata that requires inspecting a built Python wheel."""

from __future__ import annotations

import argparse
import zipfile
from pathlib import Path


def main() -> int:
    parser = argparse.ArgumentParser()
    parser.add_argument("wheel", type=Path)
    args = parser.parse_args()

    if not args.wheel.is_file():
        raise SystemExit(f"Wheel does not exist: {args.wheel}")
    with zipfile.ZipFile(args.wheel) as archive:
        names = archive.namelist()
        licenses = [
            name
            for name in names
            if ".dist-info/licenses/" in name and name.endswith("/LICENSE")
        ]
        metadata_names = [name for name in names if name.endswith(".dist-info/METADATA")]
        if len(licenses) != 1:
            raise SystemExit(f"Wheel must contain one LICENSE file; found: {licenses}")
        if len(metadata_names) != 1:
            raise SystemExit(f"Wheel must contain one METADATA file; found: {metadata_names}")
        metadata = archive.read(metadata_names[0]).decode("utf-8")
        if "License-Expression: Apache-2.0" not in metadata:
            raise SystemExit("Wheel metadata does not declare Apache-2.0")
        if "Vincent-Cregis" in metadata:
            raise SystemExit("Wheel metadata contains a personal repository URL")

    print(f"Wheel metadata and license are valid: {args.wheel}")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
