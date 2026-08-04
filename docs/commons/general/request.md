Title: 请求说明 - Cregis Developer

URL Source: https://developer-cn.cregis.com/api-reference/request

Markdown Content:
请求说明 - Cregis Developer
===============

[Skip to main content](https://developer-cn.cregis.com/api-reference/request#content-area)

[Cregis Developer home page![Image 2: light logo](https://mintcdn.com/cg/IRruUG5vxxiS-aUE/logo/light.png?fit=max&auto=format&n=IRruUG5vxxiS-aUE&q=85&s=abfe09ac56f425fd46b8e7822b7f4653)![Image 3: dark logo](https://mintcdn.com/cg/IRruUG5vxxiS-aUE/logo/dark.png?fit=max&auto=format&n=IRruUG5vxxiS-aUE&q=85&s=7eb94c127b9ebc9ff2c299f6f02750ef)](https://www.cregis.com/)

Search...

Ctrl K

*   [客服支持](https://chat.bytrack.xyz/?appId=7tAbr779)
*   [EN](https://developer.cregis.com/)
*   [官网](https://www.cregis.com/zh/)
*   [官网](https://www.cregis.com/zh/)

Search...

Navigation

通用接口格式

请求说明

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
*   [系统参数](https://developer-cn.cregis.com/api-reference/request#%E7%B3%BB%E7%BB%9F%E5%8F%82%E6%95%B0)
*   [业务参数](https://developer-cn.cregis.com/api-reference/request#%E4%B8%9A%E5%8A%A1%E5%8F%82%E6%95%B0)
*   [请求示例](https://developer-cn.cregis.com/api-reference/request#%E8%AF%B7%E6%B1%82%E7%A4%BA%E4%BE%8B)
*   [客户端](https://developer-cn.cregis.com/api-reference/request#%E5%AE%A2%E6%88%B7%E7%AB%AF)
*   [curl](https://developer-cn.cregis.com/api-reference/request#curl)

通用接口格式

请求说明
====

项目接口，目前只支持http post 一种请求方式，且指定的body Content-Type为: application/json。请特别注意，所有的请求参数通过body的json格式传递，其他传递参数的方式无效。

项目接口，接口参数分为两部分：系统参数和业务参数。
*   **系统参数**: 每个 API 调用都需要系统参数
*   **业务参数**: 特定于各个 API 调用或端点的参数

### [​](https://developer-cn.cregis.com/api-reference/request#%E7%B3%BB%E7%BB%9F%E5%8F%82%E6%95%B0)

系统参数

| 参数 | 类型 | 描述 | 示例 | 是否必须 |
| --- | --- | --- | --- | --- |
| timestamp | long | 当前请求时间戳，单位毫秒 | 1687846491667 | 是 |
| nonce | string | 6 位随机字符串 | 24abxo | 是 |
| sign | string | 请求的数字签名，防止参数被非法篡改，签名规则参考以下 [签名规则](https://developer-cn.cregis.com/api-reference/signature) 章节 | eb881023045a167d3e4a7378bc212f53 | 是 |

### [​](https://developer-cn.cregis.com/api-reference/request#%E4%B8%9A%E5%8A%A1%E5%8F%82%E6%95%B0)

业务参数

除了以上三个参数外，其他参数是业务参数，以下是一个请求示例。
[​](https://developer-cn.cregis.com/api-reference/request#%E8%AF%B7%E6%B1%82%E7%A4%BA%E4%BE%8B)

请求示例
-----------------------------------------------------------------------------------------------------

### [​](https://developer-cn.cregis.com/api-reference/request#%E5%AE%A2%E6%88%B7%E7%AB%AF)

客户端

**使用 Postman 示例:**![Image 4](https://mintcdn.com/cg/UQq624DegtbTXkkX/images/requestDemo.png?fit=max&auto=format&n=UQq624DegtbTXkkX&q=85&s=a765435d4ec02387447e90c650a19481)
### [​](https://developer-cn.cregis.com/api-reference/request#curl)

curl

Copy

```
curl --location --request POST 'https://xx.xx.xx/api/v1/payout' \
--header 'Content-Type: application/json' \
--data-raw '{
    "pid": 1394705669275648,
    "currency": "195@195",
    "address": "TXsmKpEuW7qWnXzJLGP9eDLvWPR2GRn1FS",
    "amount": "0.1",
    "remark": "payout",
    "third_party_id": "1828fc39daaf4d318777e1211c055edf",
    "callback_url": "http://xxx.com/payout/callback",
    "nonce": "rR7ngZ",
    "timestamp": 1709708726668,
    "sign": "e95ba1a7e9bcadb92d9c3405a932715d"
}'
```

[响应说明](https://developer-cn.cregis.com/api-reference/response)

Ctrl+I

[twitter](https://twitter.com/0xCregis)[github](https://github.com/0xcregis)[linkedin](https://linktr.ee/cregisofficial)

[Powered by](https://www.mintlify.com/?utm_campaign=poweredBy&utm_medium=referral&utm_source=cg)

Assistant

Responses are generated using AI and may contain mistakes.

![Image 5](https://mintcdn.com/cg/UQq624DegtbTXkkX/images/requestDemo.png?w=1100&fit=max&auto=format&n=UQq624DegtbTXkkX&q=85&s=2b6daaf886af9030ba15e4ebda70729b)
