"""Official synchronous Python SDK for Cregis APIs."""

from cregis.clients import CregisPaymentClient, CregisTeamClient, CregisWaasClient
from cregis.errors import (
    CregisApiError,
    CregisClientError,
    CregisContractError,
    CregisError,
    CregisHttpError,
)
from cregis.generated import *  # noqa: F403
from cregis.generated import __all__ as _generated_exports
from cregis.signing import canonicalize_json, sign_project_parameters, sign_team_request
from cregis.webhooks import CregisPaymentCallbackHandler, CregisWaasCallbackHandler

__version__ = "0.1.0"

__all__ = [
    "CregisApiError",
    "CregisClientError",
    "CregisContractError",
    "CregisError",
    "CregisHttpError",
    "CregisPaymentCallbackHandler",
    "CregisPaymentClient",
    "CregisTeamClient",
    "CregisWaasCallbackHandler",
    "CregisWaasClient",
    "canonicalize_json",
    "sign_project_parameters",
    "sign_team_request",
    *_generated_exports,
]
