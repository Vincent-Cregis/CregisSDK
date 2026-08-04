Title: 地址充值通知 - Cregis Developer

URL Source: https://developer-cn.cregis.com/api-reference/callback/address-deposit

Markdown Content:
地址充值通知 - Cregis Developer
===============

[Skip to main content](https://developer-cn.cregis.com/api-reference/callback/address-deposit#content-area)

[Cregis Developer home page![Image 1: light logo](https://mintcdn.com/cg/IRruUG5vxxiS-aUE/logo/light.png?fit=max&auto=format&n=IRruUG5vxxiS-aUE&q=85&s=abfe09ac56f425fd46b8e7822b7f4653)![Image 2: dark logo](https://mintcdn.com/cg/IRruUG5vxxiS-aUE/logo/dark.png?fit=max&auto=format&n=IRruUG5vxxiS-aUE&q=85&s=7eb94c127b9ebc9ff2c299f6f02750ef)](https://www.cregis.com/)

Search...

Ctrl K

*   [客服支持](https://chat.bytrack.xyz/?appId=7tAbr779)
*   [EN](https://developer.cregis.com/)
*   [官网](https://www.cregis.com/zh/)
*   [官网](https://www.cregis.com/zh/)

Search...

Navigation

子地址管理

地址充值通知

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
    *   [创建地址](https://developer-cn.cregis.com/api-reference/request-apis/address/address-generate)
    *   [批量创建地址](https://developer-cn.cregis.com/api-reference/request-apis/address/address-batch-generate)
    *   [校验地址是否存在](https://developer-cn.cregis.com/api-reference/request-apis/address/validation-address-existence)
    *   [更新地址信息](https://developer-cn.cregis.com/api-reference/request-apis/address/address-update)
    *   [地址充值通知](https://developer-cn.cregis.com/api-reference/callback/address-deposit)
    *   [地址资金归集](https://developer-cn.cregis.com/api-reference/request-apis/address/address-balance-collect)
    *   [查询地址余额](https://developer-cn.cregis.com/api-reference/request-apis/address/address-balance-query)

*   提币 
*   子地址出款 

##### 支付引擎

*   [创建订单](https://developer-cn.cregis.com/api-reference/request-apis/payment/payment-engine-create)
*   [查询订单信息](https://developer-cn.cregis.com/api-reference/request-apis/payment/payment-engine-query)
*   [订单支付通知](https://developer-cn.cregis.com/api-reference/callback/payment-engine)
*   [订单货币列表](https://developer-cn.cregis.com/api-reference/request-apis/payment/payment-order-currency)
*   [支付货币列表](https://developer-cn.cregis.com/api-reference/request-apis/payment/payment-pay-currency)

On this page
*   [回调 API](https://developer-cn.cregis.com/api-reference/callback/address-deposit#%E5%9B%9E%E8%B0%83-api)
*   [请求方式](https://developer-cn.cregis.com/api-reference/callback/address-deposit#%E8%AF%B7%E6%B1%82%E6%96%B9%E5%BC%8F)
*   [请求参数](https://developer-cn.cregis.com/api-reference/callback/address-deposit#%E8%AF%B7%E6%B1%82%E5%8F%82%E6%95%B0)

子地址管理

地址充值通知
======

获取地址充值信息通知

[​](https://developer-cn.cregis.com/api-reference/callback/address-deposit#%E5%9B%9E%E8%B0%83-api)

回调 API
----------------------------------------------------------------------------------------------------------

回调URL由对应业务发起方传入，具体了解[生成地址](https://developer-cn.cregis.com/api-reference/request-apis/address/address-generate)接口中的`callback_url`参数 该接口为`Cregis`向业务方发起回调通知请求，该回调分两种情况，根据回调内容中的`status`判断，且两种情况是互斥的，只会产生一次回调：

| 状态(status) | 描述 |
| --- | --- |
| 1 | 充值交易成功回调 |
| 2 | 充值交易失败回调 |

发起回调后当接收到返回内容为`success`字符串时为回调成功，否则为回调失败，回调失败可以进行重新推送，可登录`Cregis`客户端，在项目菜单中选择对应项目，在回调记录菜单中重新推送。
> 不同类型的业务，回调内容不同，强烈建议使用不同的接口接收回调，保证业务逻辑的清晰明确。

#### [​](https://developer-cn.cregis.com/api-reference/callback/address-deposit#%E8%AF%B7%E6%B1%82%E6%96%B9%E5%BC%8F)

请求方式

HTTP POST application/json
#### [​](https://developer-cn.cregis.com/api-reference/callback/address-deposit#%E8%AF%B7%E6%B1%82%E5%8F%82%E6%95%B0)

请求参数

| 名称 | 类型 | 描述 |
| --- | --- | --- |
| pid | integer(int64) | 项目编号 |
| cid | integer(int64) | 系统流水号 |
| chain_id | string | 链编号 |
| token_id | string | 代币编号 |
| currency | string | 币种标识 |
| address | string | 地址 |
| amount | string | 金额 |
| status | string | 状态 |
| txid | string | 交易hash |
| block_height | string | 区块高度 |
| block_time | string | 区块时间 |
| memo | string | memo、tag等上链信息，仅Ton、Xrp支持 |
| nonce | string | 6位随机字符串 |
| timestamp | integer(int64) | 时间戳 |
| sign | string | 签名 |

###### 示例

Copy

```
{
    "pid": 1382528827416576,
    "cid": 1391751691788288,
    "chain_id":"195",
    "token_id":"195",
    "currency": "TRX",
    "amount": "1.2",
    "address":"TXsmKpEuW7qWnXzJLGP9eDLvWPR2GRn1FS",
    "status": "1",
    "txid": "6dd05b0972075542219a3fcc116c58feaf9480f1f698cc46c4367ded83955cfd",
    "block_height": "34527604",
    "block_time": "1686814482000",
    "nonce": "ubqso3",
    "timestamp": 1687850657960,
    "sign": "f5be13fdd8c6f63951ca4427359457cb"
}
```

[更新地址信息](https://developer-cn.cregis.com/api-reference/request-apis/address/address-update)[地址资金归集](https://developer-cn.cregis.com/api-reference/request-apis/address/address-balance-collect)

Ctrl+I

[twitter](https://twitter.com/0xCregis)[github](https://github.com/0xcregis)[linkedin](https://linktr.ee/cregisofficial)

[Powered by](https://www.mintlify.com/?utm_campaign=poweredBy&utm_medium=referral&utm_source=cg)

Assistant

Responses are generated using AI and may contain mistakes.
