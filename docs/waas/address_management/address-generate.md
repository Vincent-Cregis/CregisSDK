Title: 创建地址 - Cregis Developer

URL Source: https://developer-cn.cregis.com/api-reference/request-apis/address/address-generate

Markdown Content:
创建地址 - Cregis Developer
===============

[Skip to main content](https://developer-cn.cregis.com/api-reference/request-apis/address/address-generate#content-area)

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

创建地址

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
*   [接入 API](https://developer-cn.cregis.com/api-reference/request-apis/address/address-generate#%E6%8E%A5%E5%85%A5-api)
*   [请求](https://developer-cn.cregis.com/api-reference/request-apis/address/address-generate#%E8%AF%B7%E6%B1%82)
*   [请求参数](https://developer-cn.cregis.com/api-reference/request-apis/address/address-generate#%E8%AF%B7%E6%B1%82%E5%8F%82%E6%95%B0)
*   [返回](https://developer-cn.cregis.com/api-reference/request-apis/address/address-generate#%E8%BF%94%E5%9B%9E)
*   [返回结果](https://developer-cn.cregis.com/api-reference/request-apis/address/address-generate#%E8%BF%94%E5%9B%9E%E7%BB%93%E6%9E%9C)
*   [返回数据 data 对象](https://developer-cn.cregis.com/api-reference/request-apis/address/address-generate#%E8%BF%94%E5%9B%9E%E6%95%B0%E6%8D%AE-data-%E5%AF%B9%E8%B1%A1)

子地址管理

创建地址
====

创建新的收款地址

### [​](https://developer-cn.cregis.com/api-reference/request-apis/address/address-generate#%E6%8E%A5%E5%85%A5-api)

接入 API

POST /api/v1/address/create
### [​](https://developer-cn.cregis.com/api-reference/request-apis/address/address-generate#%E8%AF%B7%E6%B1%82)

请求

#### [​](https://developer-cn.cregis.com/api-reference/request-apis/address/address-generate#%E8%AF%B7%E6%B1%82%E5%8F%82%E6%95%B0)

请求参数

| 名称 | 类型 | 是否必须 | 描述 |
| --- | --- | --- | --- |
| pid | integer(int64) | 是 | 项目编号 |
| chain_id | string | 是 | 链编号chain_id |
| alias | string | 否 | 显示名称 |
| callback_url | string | 否 | 回调地址,该地址接收资金时会向对应的callback_url发起交易回调通知,回调详情参考 [地址充值通知](https://developer-cn.cregis.com/api-reference/callback/address-deposit) |
| nonce | string | 是 | 6位随机字符串 |
| timestamp | integer(int64) | 是 | 时间戳 |
| sign | string | 是 | 签名串 |

###### 请求示例

Copy

```
{
  "pid": 1382528827416576,
  "callback_url": "http://xxxx.com/deposit/callback",
  "chain_id": "60",
  "alias": "cc",
  "nonce": "ubqso3",
  "timestamp": 1687850657960,
  "sign": "f5be13fdd8c6f63951ca4427359457cb"
}
```

### [​](https://developer-cn.cregis.com/api-reference/request-apis/address/address-generate#%E8%BF%94%E5%9B%9E)

返回

#### [​](https://developer-cn.cregis.com/api-reference/request-apis/address/address-generate#%E8%BF%94%E5%9B%9E%E7%BB%93%E6%9E%9C)

返回结果

| 名称 | 类型 | 描述 |
| --- | --- | --- |
| code | string | 返回码 |
| msg | string | 返回信息 |
| data | object | 返回数据 |

#### [​](https://developer-cn.cregis.com/api-reference/request-apis/address/address-generate#%E8%BF%94%E5%9B%9E%E6%95%B0%E6%8D%AE-data-%E5%AF%B9%E8%B1%A1)

返回数据 `data` 对象

| 名称 | 类型 | 描述 |
| --- | --- | --- |
| address | string | 地址 |

###### 返回示例

Copy

```
{
  "code": "00000",
  "msg": "ok",
  "data": {
    "address": "TBotPUNNgeSJmqWkSE7JoHdAnsdMyVnUca"
  }
}
```

[查询交易记录](https://developer-cn.cregis.com/api-reference/request-apis/global/trade-query)[批量创建地址](https://developer-cn.cregis.com/api-reference/request-apis/address/address-batch-generate)

Ctrl+I

[twitter](https://twitter.com/0xCregis)[github](https://github.com/0xcregis)[linkedin](https://linktr.ee/cregisofficial)

[Powered by](https://www.mintlify.com/?utm_campaign=poweredBy&utm_medium=referral&utm_source=cg)

Assistant

Responses are generated using AI and may contain mistakes.
