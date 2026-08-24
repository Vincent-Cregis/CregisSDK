# TypeScript SDK full Sandbox result — 2026-08-24

## Result

All 23 callable OpenAPI operations passed against Cregis Sandbox:

- Payment Engine: 2 of 2
- WaaS: 15 of 15
- Team API: 6 of 6

Every live request and successful response was checked against the canonical
OpenAPI contract. The operation coverage probe also confirmed that every
generated method and path pair was called.

## State created

The run created one Sandbox Payment order and three WaaS addresses. It submitted
two payouts, one sub-address withdrawal, and one collection using the default
test amount of `0.001`. Unique order and business IDs were used. No production
endpoint or credential was printed.

## Supporting checks

- `npm run verify`: passed
- TypeScript generated boundary: 77 models and 23 operations, consistent
- TypeScript client surface: 23 operations, consistent
- ESM, CommonJS, declarations, and package smoke tests: passed
- Full-suite mutation opt-in and non-Sandbox endpoint refusal: passed
