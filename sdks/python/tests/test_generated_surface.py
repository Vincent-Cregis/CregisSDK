from __future__ import annotations

import json
from pathlib import Path

from cregis import CregisPaymentClient, CregisTeamClient, CregisWaasClient
from cregis.generated.payment.operations import OPERATIONS as PAYMENT_OPERATIONS
from cregis.generated.team.operations import OPERATIONS as TEAM_OPERATIONS
from cregis.generated.waas.operations import OPERATIONS as WAAS_OPERATIONS


def test_generated_operations_match_all_public_client_methods() -> None:
    repo_root = Path(__file__).resolve().parents[3]
    overrides = json.loads(
        (repo_root / "codegen/configs/python-overrides.json").read_text(encoding="utf-8")
    )
    surfaces = {
        "payment": (CregisPaymentClient, PAYMENT_OPERATIONS),
        "waas": (CregisWaasClient, WAAS_OPERATIONS),
        "team": (CregisTeamClient, TEAM_OPERATIONS),
    }

    total = 0
    for api_name, (client_type, operations) in surfaces.items():
        expected = set(overrides["apis"][api_name]["clientMethods"].values())
        assert set(operations) == expected
        for method in expected:
            assert callable(getattr(client_type, method, None))
        total += len(expected)
    assert total == 23
