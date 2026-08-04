Title: 订单货币列表 - Cregis Developer

URL Source: https://developer-cn.cregis.com/api-reference/request-apis/payment/payment-order-currency

Markdown Content:
订单货币列表 - Cregis Developer
===============

[Skip to main content](https://developer-cn.cregis.com/api-reference/request-apis/payment/payment-order-currency#content-area)

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

订单货币列表

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
*   [说明](https://developer-cn.cregis.com/api-reference/request-apis/payment/payment-order-currency#%E8%AF%B4%E6%98%8E)
*   [法定货币](https://developer-cn.cregis.com/api-reference/request-apis/payment/payment-order-currency#%E6%B3%95%E5%AE%9A%E8%B4%A7%E5%B8%81)
*   [加密货币](https://developer-cn.cregis.com/api-reference/request-apis/payment/payment-order-currency#%E5%8A%A0%E5%AF%86%E8%B4%A7%E5%B8%81)

支付引擎

订单货币列表
======

系统支持的订单货币order_currency列表

### [​](https://developer-cn.cregis.com/api-reference/request-apis/payment/payment-order-currency#%E8%AF%B4%E6%98%8E)

说明

创建订单时可以根据需要选择法定货币或者加密货币代码（order_currency参数）, 如付款人选择的付款币种与订单币种不同, 系统将使用 CoinMarketCap 提供的汇率进行实时换算
#### [​](https://developer-cn.cregis.com/api-reference/request-apis/payment/payment-order-currency#%E6%B3%95%E5%AE%9A%E8%B4%A7%E5%B8%81)

法定货币

| 代码 | 名称 | 精度 |
| --- | --- | --- |
| HKD | 港币 | 2 |
| USD | 美元 | 2 |
| TWD | 台币 | 2 |
| AED | 阿联酋迪拉姆 | 2 |
| SGD | 新加坡元 | 2 |
| PHP | 菲律宾比索 | 2 |
| CNY | 人民币 | 2 |
| GBP | 英镑 | 2 |
| EUR | 欧元 | 2 |
| MYR | 马来西亚林吉特 | 2 |
| BRL | 巴西货币 | 2 |
| AUD | 澳元 | 2 |
| BGN | 保加利亚列弗 | 2 |
| DKK | 丹麦克朗 | 2 |
| CAD | 加拿大元 | 2 |
| SEK | 瑞典克朗 | 2 |
| CHF | 瑞士法郎 | 2 |
| INR | 印度卢比 | 2 |

#### [​](https://developer-cn.cregis.com/api-reference/request-apis/payment/payment-order-currency#%E5%8A%A0%E5%AF%86%E8%B4%A7%E5%B8%81)

加密货币

| 代码 | 名称 | 精度 |
| --- | --- | --- |
| USDT | USDT | 2 |
| USDC | USDC | 2 |
| BTC | BTC | 8 |
| SOL | SOL | 2 |
| ETH | ETH | 2 |
| TRX | TRX | 2 |
| BNB | BNB | 2 |

[订单支付通知](https://developer-cn.cregis.com/api-reference/callback/payment-engine)[支付货币列表](https://developer-cn.cregis.com/api-reference/request-apis/payment/payment-pay-currency)

Ctrl+I

[twitter](https://twitter.com/0xCregis)[github](https://github.com/0xcregis)[linkedin](https://linktr.ee/cregisofficial)

[Powered by](https://www.mintlify.com/?utm_campaign=poweredBy&utm_medium=referral&utm_source=cg)

Assistant

Responses are generated using AI and may contain mistakes.
