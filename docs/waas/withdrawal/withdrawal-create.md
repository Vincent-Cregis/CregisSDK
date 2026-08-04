Title: 发起出款申请 - Cregis Developer

URL Source: https://developer-cn.cregis.com/api-reference/request-apis/withdrawal/withdrawal-create

Markdown Content:
发起出款申请 - Cregis Developer
===============

[Skip to main content](https://developer-cn.cregis.com/api-reference/request-apis/withdrawal/withdrawal-create#content-area)

[Cregis Developer home page![Image 1: light logo](https://mintcdn.com/cg/IRruUG5vxxiS-aUE/logo/light.png?fit=max&auto=format&n=IRruUG5vxxiS-aUE&q=85&s=abfe09ac56f425fd46b8e7822b7f4653)![Image 2: dark logo](https://mintcdn.com/cg/IRruUG5vxxiS-aUE/logo/dark.png?fit=max&auto=format&n=IRruUG5vxxiS-aUE&q=85&s=7eb94c127b9ebc9ff2c299f6f02750ef)](https://www.cregis.com/)

Search...

Ctrl K

*   [客服支持](https://chat.bytrack.xyz/?appId=7tAbr779)
*   [EN](https://developer.cregis.com/)
*   [官网](https://www.cregis.com/zh/)
*   [官网](https://www.cregis.com/zh/)

Search...

Navigation

子地址出款

发起出款申请

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
    *   [发起出款申请](https://developer-cn.cregis.com/api-reference/request-apis/withdrawal/withdrawal-create)
    *   [出款通知](https://developer-cn.cregis.com/api-reference/callback/withdrawal)
    *   [查询出款信息](https://developer-cn.cregis.com/api-reference/request-apis/withdrawal/withdrawal-query)

##### 支付引擎

*   [创建订单](https://developer-cn.cregis.com/api-reference/request-apis/payment/payment-engine-create)
*   [查询订单信息](https://developer-cn.cregis.com/api-reference/request-apis/payment/payment-engine-query)
*   [订单支付通知](https://developer-cn.cregis.com/api-reference/callback/payment-engine)
*   [订单货币列表](https://developer-cn.cregis.com/api-reference/request-apis/payment/payment-order-currency)
*   [支付货币列表](https://developer-cn.cregis.com/api-reference/request-apis/payment/payment-pay-currency)

On this page
*   [接入 API](https://developer-cn.cregis.com/api-reference/request-apis/withdrawal/withdrawal-create#%E6%8E%A5%E5%85%A5-api)
*   [请求](https://developer-cn.cregis.com/api-reference/request-apis/withdrawal/withdrawal-create#%E8%AF%B7%E6%B1%82)
*   [请求参数](https://developer-cn.cregis.com/api-reference/request-apis/withdrawal/withdrawal-create#%E8%AF%B7%E6%B1%82%E5%8F%82%E6%95%B0)
*   [返回](https://developer-cn.cregis.com/api-reference/request-apis/withdrawal/withdrawal-create#%E8%BF%94%E5%9B%9E)
*   [返回结果](https://developer-cn.cregis.com/api-reference/request-apis/withdrawal/withdrawal-create#%E8%BF%94%E5%9B%9E%E7%BB%93%E6%9E%9C)
*   [返回数据 data 对象](https://developer-cn.cregis.com/api-reference/request-apis/withdrawal/withdrawal-create#%E8%BF%94%E5%9B%9E%E6%95%B0%E6%8D%AE-data-%E5%AF%B9%E8%B1%A1)

子地址出款

发起出款申请
======

创建子地址出款订单

### [​](https://developer-cn.cregis.com/api-reference/request-apis/withdrawal/withdrawal-create#%E6%8E%A5%E5%85%A5-api)

接入 API

POST /api/v1/sub_address_withdrawal
### [​](https://developer-cn.cregis.com/api-reference/request-apis/withdrawal/withdrawal-create#%E8%AF%B7%E6%B1%82)

请求

#### [​](https://developer-cn.cregis.com/api-reference/request-apis/withdrawal/withdrawal-create#%E8%AF%B7%E6%B1%82%E5%8F%82%E6%95%B0)

请求参数

| 名称 | 类型 | 是否必须 | 描述 |
| --- | --- | --- | --- |
| pid | integer(int64) | 是 | 项目编号 |
| currency | string | 是 | 币种标识 |
| from_address | string | 否 | 发送方地址，该地址必须是对应WaaS项目中的地址 |
| to_address | string | 是 | 接收方地址，在进行提币前建议先对接收方地址进行校验 |
| amount | string | 是 | 出款金额 |
| callback_url | string | 否 | 回调地址,该出款信息被处理时会向对应的callback_url发起回调通知,回调详情参考 [提币通知](https://developer-cn.cregis.com/api-reference/callback/withdrawal) |
| third_party_id | string | 是 | 调用方业务编号 |
| remark | string | 否 | 备注。不超过256位字符 |
| memo | string | 否 | memo、tag等上链信息，仅Ton、Xrp支持 |
| nonce | string | 是 | 6位随机字符串 |
| timestamp | integer(int64) | 是 | 时间戳 |
| sign | string | 是 | 签名 |

###### 请求示例

Copy

```
{
  "pid": 1382528827416576,
  "currency": "144@144",
  "from_address": "rhAZ673nG6fSXovPAkw3EcrhKyWwXducoi",
  "to_address": "rn5Jyu5J6PG64543KdGvS25rQBYFQo7GLc",
  "amount": "1.1",
  "remark": "payout",
  "memo": "123",
  "third_party_id": "c9231e604da54469a735af3f449c880f",
  "callback_url": "http://xxx.com/payout/callback",
  "nonce": "hwlkk6",
  "timestamp": 1688004243314,
  "sign": "d6eef2de79e39f434a38efb910213ba6"
}
```

### [​](https://developer-cn.cregis.com/api-reference/request-apis/withdrawal/withdrawal-create#%E8%BF%94%E5%9B%9E)

返回

#### [​](https://developer-cn.cregis.com/api-reference/request-apis/withdrawal/withdrawal-create#%E8%BF%94%E5%9B%9E%E7%BB%93%E6%9E%9C)

返回结果

| 名称 | 类型 | 描述 |
| --- | --- | --- |
| code | string | 返回状态码，成功码只有一个”code”: “00000”，其他所有返回码都是表示执行出现异常，请参考[返回码](https://developer-cn.cregis.com/api-reference/return-codes) |
| msg | string | 返回信息 |
| data | object | 返回数据 |

#### [​](https://developer-cn.cregis.com/api-reference/request-apis/withdrawal/withdrawal-create#%E8%BF%94%E5%9B%9E%E6%95%B0%E6%8D%AE-data-%E5%AF%B9%E8%B1%A1)

返回数据 `data` 对象

| 名称 | 类型 | 描述 |
| --- | --- | --- |
| cid | integer(int64) | 系统流水号 |

###### 返回示例

Copy

```
{
  "code": "00000",
  "msg": "ok",
  "data": {
    "cid": 1382688606330880
  }
}
```

[提币通知](https://developer-cn.cregis.com/api-reference/callback/payout)[出款通知](https://developer-cn.cregis.com/api-reference/callback/withdrawal)

Ctrl+I

[twitter](https://twitter.com/0xCregis)[github](https://github.com/0xcregis)[linkedin](https://linktr.ee/cregisofficial)

[Powered by](https://www.mintlify.com/?utm_campaign=poweredBy&utm_medium=referral&utm_source=cg)

Assistant

Responses are generated using AI and may contain mistakes.
