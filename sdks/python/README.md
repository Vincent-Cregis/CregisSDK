# Cregis SDK for Python

The official synchronous Python SDK for Cregis Payment Engine, WaaS, and Team
APIs. It supports Python 3.9 through 3.14 and validates request and successful
response data with strict Pydantic v2 models generated from the canonical
OpenAPI specifications.

## Install

Until the package is published to PyPI, install it from the extracted
`sdks/python` directory or a repository checkout:

```bash
pip install .
```

After the first approved PyPI release, installation will be `pip install
cregis-sdk`.

## WaaS

```python
import os

from cregis import CregisWaasClient

with CregisWaasClient(
    base_url=os.environ["WAAS_ENDPOINT"],
    pid=os.environ["WAAS_PID"],
    api_key=os.environ["WAAS_API_KEY"],
) as waas:
    coins = waas.query_project_coins()
```

## Payment Engine

```python
import os

from cregis import CregisPaymentClient, QueryOrderRequest

with CregisPaymentClient(
    base_url=os.environ["PAYMENT_ENDPOINT"],
    pid=os.environ["PAYMENT_PID"],
    api_key=os.environ["PAYMENT_API_KEY"],
) as payment:
    order = payment.query_order(QueryOrderRequest(cregis_id="po_..."))
```

## Team API

```python
import os

from cregis import CregisTeamClient, ListTeamWalletsRequest

with CregisTeamClient(
    base_url=os.environ["TEAM_ENDPOINT"],
    access_key=os.environ["TEAM_ACCESS_KEY"],
    access_secret=os.environ["TEAM_ACCESS_SECRET"],
) as team:
    wallets = team.list_team_wallets(ListTeamWalletsRequest(page_num=1, page_size=10))
```

Payment Engine and WaaS clients add `pid`, `nonce`, `timestamp`, and `sign`
automatically. The Team client sends the four signed `Access-*` headers. Signed
POST requests are not retried and redirects are not followed.

## Errors

- `CregisContractError`: request, successful response, or webhook data violates
  the generated OpenAPI contract.
- `CregisHttpError`: the server returned a non-2xx HTTP status.
- `CregisApiError`: the response envelope contains a code other than `00000`.
- `CregisClientError`: configuration, JSON, timeout, or network failure.

Invalid values passed while constructing a generated request model raise
Pydantic's `ValidationError` before a client method is called. Once a request is
passed to a client, SDK response and webhook contract failures use
`CregisContractError`.

## Webhooks

```python
from cregis import CregisPaymentCallbackHandler

handler = CregisPaymentCallbackHandler("your-api-key")
event = handler.verify_and_parse(raw_request_body)

# Return this exact plain-text value after processing succeeds.
response_body = CregisPaymentCallbackHandler.CALLBACK_SUCCESS
```

`CregisWaasCallbackHandler` verifies and parses deposit, payout, external payout
verification, and sub-address withdrawal callbacks. Signature comparison is
constant-time, and documented wire fields and types are checked before a model
is returned.

## Development

```bash
python -m venv .venv
.venv/bin/pip install -e '.[dev]'
.venv/bin/pytest
.venv/bin/ruff check src tests
.venv/bin/mypy src/cregis
```

Regenerate OpenAPI-derived models from the repository root:

```bash
./codegen/scripts/generate-python-models.sh \
  --spec-dir ../cregis-developer-docs/api-sources/specs
```

Run the safe read-only Sandbox suites only when credentials are configured:

```bash
.venv/bin/python scripts/run_sandbox_tests.py
```

The full suite covers all 23 operations and creates orders and addresses and
submits payout, withdrawal, and collection requests. It requires an explicit
mutation opt-in and refuses endpoints outside `https://t-*.cregis.dev`:

```bash
CREGIS_ALLOW_MUTATING_TESTS=true \
  .venv/bin/python scripts/run_sandbox_tests.py --all
```
