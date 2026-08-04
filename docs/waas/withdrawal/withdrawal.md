Title: 出款通知 - Cregis Developer

URL Source: https://developer-cn.cregis.com/api-reference/callback/withdrawal

Markdown Content:
回调 API
------

回调URL由对应业务发起方传入，具体了解[提币](https://developer-cn.cregis.com/api-reference/request-apis/withdrawal/withdrawal-create)接口中的`callback_url`参数 该接口为`Cregis`向业务发发起回调通知请求，该回调分四种情况，根据回调内容中的`status`判断，且四种情况是互斥的，只会产生一次回调：

| 状态(status) | 描述 |
| --- | --- |
| 2 | 签名驳回 |
| 4 | 审批驳回 |
| 6 | 交易成功 |
| 7 | 交易失败 |

发起回调后当接收到返回内容为`success`字符串时为回调成功，否则为回调失败，回调失败可以进行重新推送，可登录`Cregis`客户端，在项目菜单中选择对应项目，在回调记录菜单中重新推送。

> 不同类型的业务，回调内容不同，强烈建议使用不同的接口接收回调，保证业务逻辑的清晰明确。

#### 请求方式

HTTP POST application/json

#### 请求参数

| 名称 | 类型 | 描述 |
| --- | --- | --- |
| pid | integer(int64) | 项目编号 |
| cid | integer(int64) | 系统流水号 |
| chain_id | string | 链编号 |
| token_id | string | 代币编号 |
| currency | string | 币种标识 |
| from_address | string | 发送方地址 |
| to_address | string | 接收方地址 |
| amount | string | 出款金额 |
| third_party_id | string | 调用方业务编号D |
| remark | string | 备注,不超过256位字符 |
| status | integer(int32) | 状态 |
| txid | string | 交易hash |
| block_height | string | 区块高度 |
| block_time | integer(int64) | 区块时间 |
| memo | string | memo、tag等上链信息，仅Ton、Xrp支持 |
| nonce | string | 6位随机字符串 |
| timestamp | integer(int64) | 时间戳 |
| sign | string | 签名 |

###### 示例

```
{
  "pid": 1382528827416576,
  "cid": 1391751691788288,
  "from_address": "rhAZ673nG6fSXovPAkw3EcrhKyWwXducoi",
  "to_address": "rn5Jyu5J6PG64543KdGvS25rQBYFQo7GLc",
  "chain_id": "144",
  "token_id": "144",
  "currency": "XRP",
  "amount": "1.1",
  "third_party_id": "1e0fb3a0a9454ad8928d26b592e8b3c7",
  "remark": "payout",
  "memo": "123",
  "status": 6,
  "txid": "6dd05b0972075542219a3fcc116c58feaf9480f1f698cc46c4367ded83955cfd",
  "block_height": "34527604",
  "block_time": 1686814482000,
  "nonce": "ubqso3",
  "timestamp": 1687850657960,
  "sign": "f5be13fdd8c6f63951ca4427359457cb"
}
```
