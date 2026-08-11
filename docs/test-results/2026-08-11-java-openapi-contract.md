# Java OpenAPI Contract Test Report — 2026-08-11

## Result

| Scope | Covered | Failures | Skipped | Result |
| --- | ---: | ---: | ---: | --- |
| Java unit and local contract tests | 38 | 0 | 0 | Passed |
| Payment Engine callable operations | 2 | 0 | 0 | Passed |
| WaaS callable operations | 15 | 0 | 0 | Passed |
| Team API callable operations | 6 | 0 | 0 | Passed |
| Inbound webhook definitions | 5 | 0 | 0 | Passed with sanitized samples |
| Code generation tooling tests | 12 | 0 | 0 | Passed |

All 23 unique callable OpenAPI operations were observed by the strict Sandbox
contract probe. The probe checked the final request headers and JSON plus the
untouched response JSON before Java object mapping.

## Contract corrections found by Sandbox

- Team API response pagination uses `pageNum` and `pageSize`; wallet type uses
  `walletType`. The timestamp header example is now an integer.
- WaaS responses include token decimals, optional order coins, additional trade
  fields, camel-case response pagination, and nullable pending transaction
  metadata.
- Payment Engine can return nullable order fields, consolidated wallet QR codes,
  an `unsettled` settlement status, and a blank settlement type before settlement.

The Chinese and English OpenAPI files now use the same field structure. The
generated Java boundary contains 67 models and is reproducible byte-for-byte
from the canonical specifications.

## Verification commands

```bash
mvn --batch-mode --no-transfer-progress clean verify

CREGIS_ALLOW_MUTATING_TESTS=true \
  mvn --batch-mode --no-transfer-progress -Pintegration-tests verify

python3 -m unittest discover -s codegen/tests -v

./codegen/scripts/check-java-openapi.py \
  --spec-dir ../cregis-developer-docs/api-sources/specs

./codegen/scripts/generate-java-models.sh \
  --spec-dir ../cregis-developer-docs/api-sources/specs \
  --check
```

The full Sandbox run created test-only orders, addresses, and transaction
requests. No credential, address, order identifier, transaction identifier,
signature, or callback payload is stored in this report.

The five webhook checks use sanitized local samples. A real callback delivered
by the backend is still required before GA for end-to-end sender verification.
