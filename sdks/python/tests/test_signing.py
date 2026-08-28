import pytest

from cregis import (
    CregisClientError,
    canonicalize_json,
    sign_project_parameters,
    sign_team_request,
)


def test_project_signer_matches_published_payout_vector() -> None:
    signature = sign_project_parameters(
        {
            "pid": 1382528827416576,
            "currency": "195@195",
            "address": "TXsmKpEuW7qWnXzJLGP9eDLvWPR2GRn1FS",
            "amount": "1.1",
            "remark": "payout",
            "third_party_id": "c9231e604da54469a735af3f449c880f",
            "callback_url": "https://your-domain.com/callback",
            "nonce": "hwlkk6",
            "timestamp": 1688004243314,
        },
        "f502a9ac9ca54327986f29c03b271491",
    )
    assert signature == "f76fb193e9d34d2e59fef64e3418f79b"


def test_project_signer_canonicalizes_nested_webhook_data() -> None:
    signature = sign_project_parameters(
        {
            "event_name": "order",
            "event_type": "refunded",
            "pid": 123456789,
            "nonce": "abc123",
            "timestamp": 1719994383015,
            "data": {
                "cregis_id": "po-test",
                "order_id": "merchant-test",
                "refund_id": "rf-test",
                "refund_status": 1,
            },
        },
        "fixture-api-key",
    )
    assert signature == "9e2b39ae45341e1d912dda3e31a3dddf"


def test_team_signer_matches_independent_hmac_vector() -> None:
    body = canonicalize_json({"name": "Demo Team"})
    signature = sign_team_request(
        "/openapi/team/profile",
        1717380000000,
        "9f7c6a2b47e34f19",
        body,
        "team-secret",
    )
    assert signature == "cef9805fa4ee7bf9376b140153c5e3f80e74e4f950eb6f92ef780fa42aa44289"


def test_rfc_8785_canonicalization_is_stable() -> None:
    assert canonicalize_json('{ "b": 2, "a": 1 }') == '{"a":1,"b":2}'
    assert (
        canonicalize_json({"z": [3, {"b": True, "a": None}], "a": "text"})
        == '{"a":"text","z":[3,{"a":null,"b":true}]}'
    )


def test_signers_reject_invalid_inputs() -> None:
    with pytest.raises(CregisClientError):
        sign_project_parameters({}, "")
    with pytest.raises(CregisClientError):
        sign_team_request("relative", 1, "1234567890123456", "{}", "secret")
    with pytest.raises(CregisClientError):
        canonicalize_json({"invalid": float("nan")})
