"""Synchronous Cregis API clients."""

from cregis.clients.payment import CregisPaymentClient
from cregis.clients.team import CregisTeamClient
from cregis.clients.waas import CregisWaasClient

__all__ = ["CregisPaymentClient", "CregisTeamClient", "CregisWaasClient"]
