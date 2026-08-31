#!/usr/bin/env python3
"""Validate Sandbox configuration and run the explicit integration suite."""

from __future__ import annotations

import argparse
import os
import subprocess
import sys
from pathlib import Path
from typing import Dict, List
from urllib.parse import urlsplit

PACKAGE_ROOT = Path(__file__).resolve().parents[1]


def load_dotenv(path: Path) -> None:
    if not path.is_file():
        return
    for raw_line in path.read_text(encoding="utf-8").splitlines():
        line = raw_line.strip()
        if not line or line.startswith("#") or "=" not in line:
            continue
        name, value = line.split("=", 1)
        value = value.strip()
        if len(value) >= 2 and value[0] == value[-1] and value[0] in {'"', "'"}:
            value = value[1:-1]
        os.environ.setdefault(name.strip(), value)


def present(name: str) -> bool:
    return bool(os.environ.get(name, "").strip())


def sandbox_endpoint(name: str) -> bool:
    try:
        parsed = urlsplit(os.environ[name])
    except (KeyError, ValueError):
        return False
    hostname = parsed.hostname or ""
    return (
        parsed.scheme == "https" and hostname.startswith("t-") and hostname.endswith(".cregis.dev")
    )


def main() -> int:
    parser = argparse.ArgumentParser()
    parser.add_argument("--all", action="store_true", help="run all 23 operations")
    args = parser.parse_args()
    load_dotenv(PACKAGE_ROOT / ".env")

    groups: Dict[str, List[str]]
    if args.all:
        groups = {
            "WaaS full suite": [
                "WAAS_PID",
                "WAAS_API_KEY",
                "WAAS_ENDPOINT",
                "WITHDRAW_ADDRESS",
            ],
            "Payment full suite": ["PAYMENT_PID", "PAYMENT_API_KEY", "PAYMENT_ENDPOINT"],
            "Team API suite": ["TEAM_ACCESS_KEY", "TEAM_ACCESS_SECRET", "TEAM_ENDPOINT"],
        }
        if os.environ.get("CREGIS_ALLOW_MUTATING_TESTS") != "true":
            print(
                "Full Sandbox tests require CREGIS_ALLOW_MUTATING_TESTS=true because they "
                "create orders and addresses and submit payout, withdrawal, and collection "
                "requests.",
                file=sys.stderr,
            )
            return 2
    else:
        groups = {
            "WaaS read-only suite": ["WAAS_PID", "WAAS_API_KEY", "WAAS_ENDPOINT"],
            "Payment read-only suite": [
                "PAYMENT_PID",
                "PAYMENT_API_KEY",
                "PAYMENT_ENDPOINT",
                "PAYMENT_CREGIS_ID",
            ],
            "Team API suite": ["TEAM_ACCESS_KEY", "TEAM_ACCESS_SECRET", "TEAM_ENDPOINT"],
        }

    missing = {
        suite: [name for name in names if not present(name)] for suite, names in groups.items()
    }
    missing = {suite: names for suite, names in missing.items() if names}
    if missing:
        print(
            "Sandbox tests did not run because required environment variables are missing:",
            file=sys.stderr,
        )
        for suite, names in missing.items():
            print(f"- {suite}: {', '.join(names)}", file=sys.stderr)
        return 2

    if args.all:
        unsafe = [
            name
            for name in ("WAAS_ENDPOINT", "PAYMENT_ENDPOINT", "TEAM_ENDPOINT")
            if not sandbox_endpoint(name)
        ]
        if unsafe:
            print(
                f"Full Sandbox tests refused non-Sandbox endpoints: {', '.join(unsafe)}",
                file=sys.stderr,
            )
            return 2

    environment = dict(os.environ)
    environment["CREGIS_RUN_SANDBOX_TESTS"] = "true"
    environment["CREGIS_SANDBOX_SUITE"] = "all" if args.all else "readonly"
    result = subprocess.run(
        [sys.executable, "-m", "pytest", "-q", "tests/test_sandbox.py"],
        cwd=PACKAGE_ROOT,
        env=environment,
        check=False,
    )
    return result.returncode


if __name__ == "__main__":
    raise SystemExit(main())
