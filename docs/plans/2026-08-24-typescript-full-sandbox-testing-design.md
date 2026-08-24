# TypeScript full Sandbox testing design

## Goal

Prove that the TypeScript SDK can call every callable operation in the three
canonical OpenAPI specifications against the Cregis Sandbox. The full suite
covers 2 Payment Engine operations, 15 WaaS operations, and 6 Team operations,
for a total of 23. Every request and response continues to pass through the
independent OpenAPI contract validator used by the existing read-only tests.

## Safety boundary

The existing `test:sandbox` command remains read-only. State-changing coverage
is exposed through a separate `test:sandbox:all` command and requires the
caller to set `CREGIS_ALLOW_MUTATING_TESTS=true`. The full suite uses unique
order and business IDs, creates disposable Sandbox addresses, and defaults to
the smallest established test amount (`0.001`). The runner accepts the full
suite only for HTTPS hosts matching `t-*.cregis.dev`, and it never prints
credentials. A configured `WITHDRAW_ADDRESS` is
required because withdrawal and collection cannot be tested safely from a new,
empty address.

## Execution flow

Payment testing creates an order and immediately queries that same order. WaaS
testing first discovers a chain that supports both address creation and payout,
then creates and updates addresses, validates them, checks balances and trades,
submits both payout versions, queries the first payout, submits and queries a
sub-address withdrawal, and finally submits a collection. Team testing keeps
the existing six-operation read-only flow. A small fetch probe records method
and path pairs from the generated operation inventory and fails unless every
operation for that API was called.

The normal local test suite still skips all live tests. CI therefore remains
deterministic unless a dedicated job deliberately supplies Sandbox credentials
and opts into mutations.
