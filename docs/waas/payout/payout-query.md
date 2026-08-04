Title: 查询提币信息 - Cregis Developer

URL Source: https://developer-cn.cregis.com/api-reference/request-apis/payout/payout-query

Markdown Content:
查询提币信息 - Cregis Developer
===============

[Skip to main content](https://developer-cn.cregis.com/api-reference/request-apis/payout/payout-query#content-area)

[Cregis Developer home page![Image 1: light logo](https://mintcdn.com/cg/IRruUG5vxxiS-aUE/logo/light.png?fit=max&auto=format&n=IRruUG5vxxiS-aUE&q=85&s=abfe09ac56f425fd46b8e7822b7f4653)![Image 2: dark logo](https://mintcdn.com/cg/IRruUG5vxxiS-aUE/logo/dark.png?fit=max&auto=format&n=IRruUG5vxxiS-aUE&q=85&s=7eb94c127b9ebc9ff2c299f6f02750ef)](https://www.cregis.com/)

Search...

Ctrl K

*   [客服支持](https://chat.bytrack.xyz/?appId=7tAbr779)
*   [EN](https://developer.cregis.com/)
*   [官网](https://www.cregis.com/zh/)
*   [官网](https://www.cregis.com/zh/)

Search...

Navigation

提币

查询提币信息

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
    *   [发起提币申请](https://developer-cn.cregis.com/api-reference/request-apis/payout/payout-create)
    *   [发起提币申请（v2）](https://developer-cn.cregis.com/api-reference/request-apis/payout/payout-create-v2)
    *   [校验地址合法性](https://developer-cn.cregis.com/api-reference/request-apis/payout/validation-address-legality)
    *   [查询提币信息](https://developer-cn.cregis.com/api-reference/request-apis/payout/payout-query)
    *   [提币通知](https://developer-cn.cregis.com/api-reference/callback/payout)

*   子地址出款 

##### 支付引擎

*   [创建订单](https://developer-cn.cregis.com/api-reference/request-apis/payment/payment-engine-create)
*   [查询订单信息](https://developer-cn.cregis.com/api-reference/request-apis/payment/payment-engine-query)
*   [订单支付通知](https://developer-cn.cregis.com/api-reference/callback/payment-engine)
*   [订单货币列表](https://developer-cn.cregis.com/api-reference/request-apis/payment/payment-order-currency)
*   [支付货币列表](https://developer-cn.cregis.com/api-reference/request-apis/payment/payment-pay-currency)

On this page
*   [接入API](https://developer-cn.cregis.com/api-reference/request-apis/payout/payout-query#%E6%8E%A5%E5%85%A5api)
*   [请求](https://developer-cn.cregis.com/api-reference/request-apis/payout/payout-query#%E8%AF%B7%E6%B1%82)
*   [请求参数](https://developer-cn.cregis.com/api-reference/request-apis/payout/payout-query#%E8%AF%B7%E6%B1%82%E5%8F%82%E6%95%B0)
*   [返回](https://developer-cn.cregis.com/api-reference/request-apis/payout/payout-query#%E8%BF%94%E5%9B%9E)
*   [返回数据](https://developer-cn.cregis.com/api-reference/request-apis/payout/payout-query#%E8%BF%94%E5%9B%9E%E6%95%B0%E6%8D%AE)
*   [Response data Object](https://developer-cn.cregis.com/api-reference/request-apis/payout/payout-query#response-data-object)
*   [Response status 类型](https://developer-cn.cregis.com/api-reference/request-apis/payout/payout-query#response-status-%E7%B1%BB%E5%9E%8B)

提币

查询提币信息
======

查询提币订单信息

### [​](https://developer-cn.cregis.com/api-reference/request-apis/payout/payout-query#%E6%8E%A5%E5%85%A5api)

接入API

POST /api/v1/payout/query
### [​](https://developer-cn.cregis.com/api-reference/request-apis/payout/payout-query#%E8%AF%B7%E6%B1%82)

请求

#### [​](https://developer-cn.cregis.com/api-reference/request-apis/payout/payout-query#%E8%AF%B7%E6%B1%82%E5%8F%82%E6%95%B0)

请求参数

| 名称 | 类型 | 是否必须 | 描述 |
| --- | --- | --- | --- |
| pid | integer(int64) | 是 | 项目编号 |
| cid | integer(int64) | 是 | 订单编号 |
| nonce | string | 是 | 6位随机字符串 |
| timestamp | integer(int64) | 是 | 时间戳 |
| sign | string | 是 | 签名 |

###### 请求示例

Copy

```
{
  "pid": 1382528827416576,
  "cid": 1382626461605888,
  "nonce": "sw7kv5",
  "timestamp": 1687853722065,
  "sign": "af825ddb839e6796d62977caf18d3394"
}
```

### [​](https://developer-cn.cregis.com/api-reference/request-apis/payout/payout-query#%E8%BF%94%E5%9B%9E)

返回

#### [​](https://developer-cn.cregis.com/api-reference/request-apis/payout/payout-query#%E8%BF%94%E5%9B%9E%E6%95%B0%E6%8D%AE)

返回数据

| 名称 | 类型 | 描述 |
| --- | --- | --- |
| code | string | 返回码 |
| msg | string | 返回信息 |
| data | object | 返回数据 |

#### [​](https://developer-cn.cregis.com/api-reference/request-apis/payout/payout-query#response-data-object)

Response `data` Object

| 名称 | 类型 | 描述 |
| --- | --- | --- |
| pid | integer(int64) | 项目编号 |
| chain_id | string | 链编号 |
| token_id | string | 代币编号 |
| currency | string | 币种标识 |
| from_address | string | 发送方地址 |
| address | string | 接收方地址 |
| amount | string | 金额 |
| status | integer(int32) | 状态 |
| third_party_id | string | 调用方业务编号 |
| remark | string | 备注 |
| memo | string | memo、tag等上链信息，仅Ton、Xrp支持 |
| txid | string | 交易hash |
| block_height | string | 区块高度 |
| block_time | integer(int64) | 区块时间 |

#### [​](https://developer-cn.cregis.com/api-reference/request-apis/payout/payout-query#response-status-%E7%B1%BB%E5%9E%8B)

Response `status` 类型

| 状态(status) | 描述 |
| --- | --- |
| 0 | 待审批 |
| 3 | 审批取消 |
| 4 | 审批驳回 |
| 5 | 待签名 |
| 1 | 签名通过 |
| 2 | 签名驳回 |
| 6 | 交易成功 |
| 7 | 交易失败 |

###### 返回示例

Copy

```
{
  "code": "00000",
  "msg": "ok",
  "data": {
    "pid": 1382528827416576,
    "from_address": "TRsYEaEd1UbNsoXDza3jyJTnwavk6iWBdk",
    "address": "TXsmKpEuW7qWnXzJLGP9eDLvWPR2GRn1FS",
    "chain_id": "195",
    "token_id": "195",
    "currency": "TRX",
    "amount": "1.1",
    "third_party_id": "1e0fb3a0a9454ad8928d26b592e8b3c7",
    "remark": "payout",
    "status": 0,
    "txid": "6dd05b0972075542219a3fcc116c58feaf9480f1f698cc46c4367ded83955cfd",
    "block_height": "34527604",
    "block_time": 1686814482000
  }
}
```

[校验地址合法性](https://developer-cn.cregis.com/api-reference/request-apis/payout/validation-address-legality)[提币通知](https://developer-cn.cregis.com/api-reference/callback/payout)

Ctrl+I

[twitter](https://twitter.com/0xCregis)[github](https://github.com/0xcregis)[linkedin](https://linktr.ee/cregisofficial)

[Powered by](https://www.mintlify.com/?utm_campaign=poweredBy&utm_medium=referral&utm_source=cg)

Assistant

Responses are generated using AI and may contain mistakes.
