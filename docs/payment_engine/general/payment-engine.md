Title: 订单支付通知 - Cregis Developer

URL Source: https://developer-cn.cregis.com/api-reference/callback/payment-engine

Markdown Content:
回调 API
------

根据创建订单填写的callback_url,当订单状态变化时会通过该回调地址通知调用方，具体了解[创建订单](https://developer-cn.cregis.com/api-reference/request-apis/payment/payment-engine-create)接口中的`callback_url`参数 该接口为Cregis向业务方发起回调通知请求，只会产生一次回调：

#### 回调事件

| 回调事件名称(event_name) | 回调事件类型(event_type) | 描述 |
| --- | --- | --- |
| order | expired | 订单超时通知 |
| order | paid | 订单支付完成通知 |
| order | paid_partial | 订单部分支付通知 |
| order | paid_over | 订单超额支付通知 |
| order | refunded | 订单退款通知 |
| order | paid_remain | 订单补款通知 |

#### 请求方式

HTTP POST application/json 发起回调请求后当接收到返回内容为`success`字符串时为回调成功，否则为回调失败

#### 请求参数

| 名称 | 类型 | 描述 |
| --- | --- | --- |
| event_name | string | 事件名称 |
| event_type | string | 事件类型 |
| data | JSONString | 订单信息 |
| pid | integer(int64) | 项目编号 |
| nonce | string | 6位随机字符串 |
| timestamp | integer(int64) | 时间戳 |
| sign | string | 签名 |

#### 支付订单 `data` 对象

| 名称 | 类型 | 描述 |
| --- | --- | --- |
| cregis_id | string | cregis订单编号 |
| order_id | string | 订单编号 |
| receive_amount | string | 汇率转换后的订单金额 |
| receive_currency | string | 汇率转换后的订单货币单位 |
| pay_amount | string | 实际到账金额 |
| pay_currency | string | 到账货币单位 |
| order_amount | string | 订单交易金额 |
| order_currency | string | 订单货币代码，即ISO 4217货币代码,例如 CNY、HKD，请参考[订单货币列表](https://developer-cn.cregis.com/api-reference/request-apis/payment/payment-order-currency) |
| exchange_rate | string | 货币汇率 |
| payment_address | string | 充值地址 |
| created_time | integer(int64) | 订单创建时间【13位时间戳】 |
| cancel_time | integer(int64) | 订单取消时间【13位时间戳】 |
| transact_time | integer(int64) | 订单支付完成时间【13位时间戳】 |
| valid_time | integer | 订单超时时间【单位/分钟】 |
| status | string | 订单状态【new-待支付,paid-已支付，expired-订单超出有效时间，paid_over-付款超出订单金额 paid_partial-付款小于订单金额】 |
| remark | string | 订单备注信息 |
| tx_id | string | 交易hash |
| payer_id | string | 付款人id标识 |
| payer_name | string | 付款人姓名 |
| payer_email | string | 付款人邮箱 |

#### 超时订单 `data` 对象

| 名称 | 类型 | 描述 |
| --- | --- | --- |
| cregis_id | string | cregis订单编号 |
| order_id | string | 商户订单编号 |
| order_amount | string | 订单交易金额 |
| order_currency | string | 订单货币代码，即ISO 4217货币代码,例如 CNY、HKD，参考法币信息列表接口 |
| created_time | integer(int64) | 订单创建时间【13位时间戳】 |
| cancel_time | integer(int64) | 订单取消时间【13位时间戳】 |
| valid_time | integer | 订单超时时间【单位/分钟】 |
| status | string | 订单状态【new -待支付 paid-被调用方收到付款 expired-订单超出有效时间】 |
| remark | string | 订单备注信息 |
| payer_id | string | 付款人id标识 |
| payer_name | string | 付款人姓名 |
| payer_email | string | 付款人邮箱 |

#### 订单退款 `data` 对象

| 名称 | 类型 | 描述 |
| --- | --- | --- |
| cregis_id | string | cregis订单编号 |
| order_id | string | 订单编号 |
| receive_amount | string | 汇率转换后的订单金额 |
| receive_currency | string | 汇率转换后的订单货币单位 |
| pay_amount | string | 实际到账金额 |
| pay_currency | string | 到账货币单位 |
| order_amount | string | 订单交易金额 |
| order_currency | string | 订单货币代码，即ISO 4217货币代码,例如 CNY、HKD，参考法币信息列表接口 |
| exchange_rate | string | 货币汇率 |
| payment_address | string | 充值地址 |
| created_time | integer(int64) | 订单创建时间【13位时间戳】 |
| cancel_time | integer(int64) | 订单取消时间 【13位时间戳】 |
| transact_time | integer(int64) | 订单支付完成时间 【13位时间戳】 |
| valid_time | integer | 订单超时时间【单位/分钟】 |
| status | string | 订单状态【new -待支付 paid-被调用方收到付款 expired-订单超出有效时间】 |
| remark | string | 订单备注信息 |
| tx_id | string | 交易哈希 |
| payer_id | string | 付款人id标识 |
| payer_name | string | 付款人姓名 |
| payer_email | string | 付款人邮箱 |
| refund_requested | string | 订单是否发生了退款 no：无退款记录 yes：有退款记录 |
| type | string | 0: 部分退款 1: 全额退款 |
| refund_id | long | Cregis退款订单编号 |
| refund_address | string | 退款收款人的链上地址 |
| refund_currency | string | 退款订单的币种填入加密货币token_name，格式：USDT-BEP20，USDT-TRC20 |
| refund_amount | string | 退款金额 |
| refund_fee |  | 退款所需gas fee |
| actual_refund_amount |  | 扣减矿工费后的实际退款金额 = 退款订单金额amount-gas_fee_amount |
| refund_status | string | 退款状态 0: 退款处理中 1：退款成功 2：退款失败 |
| refund_tx_id | string | 退款交易哈希 |
| refund_created_time | string | 退款订单创建时间【13位时间戳】 e.g. 1720508269092 即 2024-07-09 14:57:49 092 |
| refund_transact_time | string | 退款订单最后更新时间【13位时间戳】 e.g. 1720508269092 即 2024-07-09 14:57:49 092 |

#### 订单补款 `data` 对象

| 名称 | 类型 | 描述 |
| --- | --- | --- |
| cregis_id | string | cregis订单编号 |
| order_id | string | 订单编号 |
| receive_amount | string | 汇率转换后的订单金额 |
| receive_currency | string | 汇率转换后的订单货币单位 |
| pay_amount | string | 实际到账金额 |
| pay_currency | string | 到账货币单位 |
| order_amount | string | 订单交易金额 |
| order_currency | string | 订单货币代码，即ISO 4217货币代码,例如 CNY、HKD，参考法币信息列表接口 |
| exchange_rate | string | 货币汇率 |
| payment_address | string | 充值地址 |
| created_time | integer(int64) | 订单创建时间【13位时间戳】 |
| cancel_time | integer(int64) | 订单取消时间 【13位时间戳】 |
| transact_time | integer(int64) | 订单支付完成时间 【13位时间戳】 |
| valid_time | integer | 订单超时时间【单位/分钟】 |
| status | string | 订单状态【new -待支付 paid-被调用方收到付款 expired-订单超出有效时间】 |
| remark | string | 订单备注信息 |
| tx_id | string | 交易哈希 |
| payer_id | string | 付款人id标识 |
| payer_name | string | 付款人姓名 |
| payer_email | string | 付款人邮箱 |
| additional_pay_currency | string | 补款货币单位 |
| additional_pay_amount | string | 补款金额 |
| additional_payment_address | string | 补款地址 |
| additional_payment_tx_id | string | 补款交易hash |
| additional_payment_tx_id | integer(int64) | 补款订单支付完成时间 【13位时间戳】 |

###### 请求示例

```
{
    "event_name": "order",
    "event_type": "paid",
    "data": {
        "cregis_id": "po20240703132452000",
        "order_id": "c9231e604da54469a735af3f449c880f",
        "receive_amount": "12.86",
        "receive_currency": "USDT",
        "pay_amount": "12.86",
        "pay_currency": "USDT",
        "order_amount": "100",
        "order_currency": "HKD",
        "exchange_rate": "0.1286",
        "payment_address": "0xd38c2cf366a731dcbe4a32c7ef24ff96d080ca7e",
        "created_time": 1719993183015,
        "cancel_time": null,
        "transact_time": 1719993183325,
        "valid_time": 30,
        "status": "paid",
        "payer_id": "p_001",
        "payer_name": "",
        "payer_email": "test@mail.com"
        "remark":"购买商品",
        "tx_id": "0x0502f2bfd96cd0f55edea3343513940f3af7fe594eae77f08d2f46ea24829b11"
    }
}
```
