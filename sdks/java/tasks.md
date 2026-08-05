# Cregis SDK Tasks

## Phase 1: Foundation (Completed)
- [x] Project Scaffolding & Configuration (pom.xml)
- [x] Core Exception Hierarchy (`CregisException`)
- [x] Authentication & Security
    - [x] `CregisSigner` (MD5 + TreeMap Sorting)
    - [x] `CregisAuthInterceptor` (Auto-signing)
- [x] Network Layer
    - [x] `CregisBaseClient` (OkHttp + Jackson)
    - [x] `CregisLoggingInterceptor`
- [x] Client Split Architecture
    - [x] `CregisPaymentClient` (Skeleton)
    - [x] `CregisWaasClient` (Skeleton)
- [x] Verification
    - [x] Signature Algorithm Unit Tests

## Phase 2: Payment Engine (In Progress)
- [x] Create Order (`CreateOrderRequest`)
- [x] Query Order (`GetOrderRequest`)
- [x] Callback Handling
    - [x] `PaymentCallbackHandler` (Verify signatures from Webhook)
    - [x] Domain Models (`PaymentCallbackNotification`)
- [x] Currency Support
    - [x] `FiatCurrency` Enum
    - [x] `CryptoCurrency` Enum

## Phase 3: WaaS Module (Todo)
- [x] Address Management
    - [x] Generate Deposit Address
    - [x] Check Address Validity
- [x] Payouts
    - [x] Make Payout (`PayoutRequest`)
    - [x] Query Payout Status
    - [x] `PayoutCallbackHandler` (WaaS Payout Notification)
    - [x] Check Address Legality (`validation-address-legality.md`)
    - [ ] Make Payout V1 (Legacy) - Optional?
- [x] Project Management
    - [x] Query Address Balance (`AddressBalanceRequest`)
    - [x] Query Project Currencies (`currency-query.md`)
    - [x] Query Trade Records (`trade-query.md`)
- [x] Advanced Address Management
    - [x] Batch Generate Address (`address-batch-generate.md`)
    - [x] Update Address (`address-update.md`)
    - [x] Balance Collection (`address-balance-collect.md`)
    - [x] `AddressDepositCallbackHandler` (Deposit Notification)
- [x] Withdrawal (Sub-address Withdrawal)
    - [x] Create Withdrawal (`withdrawal-create.md`)
    - [x] Query Withdrawal (`withdrawal-query.md`)
    - [x] `WithdrawalCallbackHandler` (`withdrawal.md`)
- [x] Common Utils
    - [x] Callback Handler Factory (Unified handler for distinct callbacks?)

## Phase 4: Production Readiness
- [ ] Javadoc for Public API
- [ ] Publishing Strategy (Maven Central / JitPack)
- [ ] Integration Tests (MockServer interaction)
