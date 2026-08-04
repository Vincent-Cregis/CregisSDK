Title: 支付货币列表 - Cregis Developer

URL Source: https://developer-cn.cregis.com/api-reference/request-apis/payment/payment-pay-currency

Markdown Content:
支付货币列表 - Cregis Developer
===============

[Skip to main content](https://developer-cn.cregis.com/api-reference/request-apis/payment/payment-pay-currency#content-area)

[Cregis Developer home page![Image 1: light logo](https://mintcdn.com/cg/IRruUG5vxxiS-aUE/logo/light.png?fit=max&auto=format&n=IRruUG5vxxiS-aUE&q=85&s=abfe09ac56f425fd46b8e7822b7f4653)![Image 2: dark logo](https://mintcdn.com/cg/IRruUG5vxxiS-aUE/logo/dark.png?fit=max&auto=format&n=IRruUG5vxxiS-aUE&q=85&s=7eb94c127b9ebc9ff2c299f6f02750ef)](https://www.cregis.com/)

Search...

Ctrl K

*   [客服支持](https://chat.bytrack.xyz/?appId=7tAbr779)
*   [EN](https://developer.cregis.com/)
*   [官网](https://www.cregis.com/zh/)
*   [官网](https://www.cregis.com/zh/)

Search...

Navigation

支付引擎

支付货币列表

[文档](https://developer-cn.cregis.com/quickstart)[API 参考](https://developer-cn.cregis.com/api-reference/request)[SDK和工具](https://developer-cn.cregis.com/sdk-tool/sdk)[常见问题](https://developer-cn.cregis.com/faqs/Address)

##### 通用接口格式

*   [请求说明](https://developer-cn.cregis.com/api-reference/request)
*   [响应说明](https://developer-cn.cregis.com/api-reference/response)
*   [签名规则](https://developer-cn.cregis.com/api-reference/signature)
*   [返回码](https://developer-cn.cregis.com/api-reference/return-codes)
*   [币种码](https://developer-cn.cregis.com/api-reference/currency-identifiers)

##### WaaS项目

*   [查询项目币种](https://developer-cn.cregis.com/api-reference/request-apis/global/currency-query)
*   [查询交易记录](https://developer-cn.cregis.com/api-reference/request-apis/global/trade-query)
*   子地址管理 
*   提币 
*   子地址出款 

##### 支付引擎

*   [创建订单](https://developer-cn.cregis.com/api-reference/request-apis/payment/payment-engine-create)
*   [查询订单信息](https://developer-cn.cregis.com/api-reference/request-apis/payment/payment-engine-query)
*   [订单支付通知](https://developer-cn.cregis.com/api-reference/callback/payment-engine)
*   [订单货币列表](https://developer-cn.cregis.com/api-reference/request-apis/payment/payment-order-currency)
*   [支付货币列表](https://developer-cn.cregis.com/api-reference/request-apis/payment/payment-pay-currency)

On this page
*   [说明](https://developer-cn.cregis.com/api-reference/request-apis/payment/payment-pay-currency#%E8%AF%B4%E6%98%8E)
*   [加密货币](https://developer-cn.cregis.com/api-reference/request-apis/payment/payment-pay-currency#%E5%8A%A0%E5%AF%86%E8%B4%A7%E5%B8%81)

支付引擎

支付货币列表
======

系统支持的支付货币列表

### [​](https://developer-cn.cregis.com/api-reference/request-apis/payment/payment-pay-currency#%E8%AF%B4%E6%98%8E)

说明

创建订单时可以根据需要选择支付指定加密货币名称（tokens参数）, 如付款人选择的付款加密货币币种与订单币种不同, 系统将使用 CoinMarketCap 提供的汇率进行实时换算。如tokens不传，将默认支持所有可支持的加密货币币种进行支付
#### [​](https://developer-cn.cregis.com/api-reference/request-apis/payment/payment-pay-currency#%E5%8A%A0%E5%AF%86%E8%B4%A7%E5%B8%81)

加密货币

| 加密货币名称 | 货币标识 | 网络 |
| --- | --- | --- |
| USDT-TRC20 | USDT | TRON |
| USDT-ERC20 | USDT | Ethereum |
| USDT-BEP20 | USDT | BSC |
| USDT-Polygon | USDT | Polygon |
| USDT-Avalanche-C | USDT | Avalanche-C |
| USDT-Arbitrum One | USDT | Arbitrum One |
| USDT-Solana | USDT | Solana |
| USDC-Base | USDC | Base |
| USDC-ERC20 | USDC | Ethereum |
| USDC-Solana | USDC | Solana |
| USDC-BEP20 | USDC | BSC |
| USDC-Polygon | USDC | Polygon |
| USDC-Avalanche-C | USDC | Avalanche-C |
| USDC-Arbitrum One | USDC | Arbitrum One |
| USDC-Optimism | USDC | Optimism |
| Bitcoin | BTC | Bitcoin |
| Solana | SOL | Solana |
| Ethereum | ETH | Ethereum |
| Base | ETH | Base |
| Arbitrum One | ETH | Arbitrum One |
| Optimism | ETH | Optimism |
| TRON | TRX | TRON |
| BNB-BSC | BNB | BSC |

[订单货币列表](https://developer-cn.cregis.com/api-reference/request-apis/payment/payment-order-currency)

Ctrl+I

[twitter](https://twitter.com/0xCregis)[github](https://github.com/0xcregis)[linkedin](https://linktr.ee/cregisofficial)

[Powered by](https://www.mintlify.com/?utm_campaign=poweredBy&utm_medium=referral&utm_source=cg)

Assistant

Responses are generated using AI and may contain mistakes.
