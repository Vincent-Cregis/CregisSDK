Title: 创建订单 - Cregis Developer

URL Source: https://developer-cn.cregis.com/api-reference/request-apis/payment/payment-engine-create

Markdown Content:
### 接入API

POST /api/v2/checkout

### 请求

#### 请求参数

| 名称 | 类型 | 是否必须 | 描述 |
| --- | --- | --- | --- |
| pid | integer(int64) | 是 | Cregis中该收单项目的唯一编号 |
| nonce | string | 是 | 6位随机字符串 |
| timestamp | integer(int64) | 是 | 13位unix时间戳格式 |
| sign | string | 是 | 对该接口请求进行的数字签名以防止非法请求，请参考[签名规则](https://developer-cn.cregis.com/api-reference/signature)章节。 |
| order_id | string | 是 | 商家内部的订单编号【不超过128位字符】 |
| order_amount | string | 是 | 付款总额（如order_currency是法币，则以法币金额计算／如order_currency是加密货币，则以加密货币金额计算） |
| order_currency | string | 是 | 请参阅[订单货币列表](https://developer-cn.cregis.com/api-reference/request-apis/payment/payment-order-currency) - 如订单币种为加密货币，例子为USDT、ETH等。 - 如订单币种为法币：则填入ISO 4217 3 位字元货币代码。例子为HKD、 USD等。 |
| order_details | JSONString | 否 | 订单商品明细包括价格、数量和商品描述。这显示在 Cregis 结帐页面的订单详情部分 |
| sub_merchant | JSONString | 否 | 商户可使用此字段来记录内部子商户的编号 |
| payer_id | string | 是 | 付款人编号 |
| payer_name | string | 否 | 付款人姓名 |
| payer_email | string | 否 | 付款人邮箱,若accept_partial_payment或accept_over_payment为false则必填 |
| tokens | ArrayString | 否 | 此字段中可限制您允许客户支付的代币。如此字段留空，则表示接受支持所有代币作为支付代币。请参阅[支付货币列表](https://developer-cn.cregis.com/api-reference/request-apis/payment/payment-pay-currency) 填写参数可参考付款及结算参数页面内支付货币及支付网络的单位：例如：USDT-BEP20、USDT-TRC20等 |
| stablecoin_realtime_rate | string | 否 | 默认值：false false：代表 USDT/USDC 兑 USD 使用固定1:1的汇率 true：代表 USDT/USDC兑USD 使用从CoinMarketCap上获取的实时汇率 |
| underpaid_tolerance | float | 否 | 部分支付限额,该订单可接受的支付金额容差下限,单位为order_currency,默认值为0。 此容差下限用于计算你允许订单可接受的最少支付金额。单位根据字段order_currency: - “已支付”订单状态：order_amount - underpaid_tolerance `<=` 实际支付金额 `<=` order_amount + overpaid_tolerance - “部分付款”订单状态：实际支付金额 < order_amount - underpaid_tolerance - “超额付款”订单状态：order_amount + overpaid_tolerance < 实际支付金额 例子：order_currency 为 HKD 及 underpaid_tolerance 为 3.50。订单状态为: - “已支付”如订单少付款小于或等于3.50 HKD - “部分支付”如订单少付款超过3.50 HKD |
| overpaid_tolerance | float | 否 | 超额支付限额，该订单可接受的支付金额容差上限,单位为order_currency,默认值为0。 此容差上限用于计算你允许订单可接受的最多支付金额。单位根据字段order_currency: - “已支付”订单状态：order_amount - underpaid_tolerance `<=` 实际支付金额 `<=` order_amount + overpaid_tolerance - “部分付款”订单状态：实际支付金额 < order_amount - underpaid_tolerance - “超额付款”订单状态：order_amount + overpaid_tolerance < 实际支付金额 例子：order_currency 为 HKD 及 overpaid_tolerance 为 3.50。订单状态为: - “已支付”如订单多付或等于3.50 HKD - “超额支付”如订单多付款超过3.50 HKD |
| accept_partial_payment | string | 否 | 默认值：“true” - “false”: 如订单不接受部分支付及实际支付金额少于order_amount时，付款人需对订单进行补款或进行退款 - “true”: 如订单接受部分支付，即允许付款人不需对订单进行补款或退款 |
| accept_over_payment | string | 否 | 默认值：“true” - “false”: 如订单不接受超额支付及实际支付金额大于order_amount时，付款人需对订单进行差价退款 - “true”: 如订单接受超额支付，即允许付款人不需对订单进行差价退款 |
| language | string | 否 | 默认值：en 用于指定收银台或邮件的默认语言，可选值为： en - 英文 tc - 繁体中文 sc - 简体中文 |
| callback_url | string | 否 | Cregis 会向此回调地址发送订单状态更新的通知,回调详情参考 [订单支付通知](https://developer-cn.cregis.com/api-reference/callback/payment-engine) |
| remark | string | 否 | 商户可对这笔支付订单做出备注【不超过256位字符】 |
| valid_time | integer | 是 | 单位：分钟（可填写：10 到 1440 的整数） 订单的有效时间。如超过有效时间，将不可以再支付。 |
| success_url | string | 是 | 付款成功后，点击“返回商店”按钮，付款人将被重定向到此 URL。请确保 URL 中包含“http://”或“https://” |
| cancel_url | string | 是 | 如果付款人不付款或点击“取消”按钮，则定向至此 url。请确保 url 中包含“http://”或“https://”。 |

**order_details object**

| 名称 | 类型 | 是否必须 | 描述 |
| --- | --- | --- | --- |
| shopping_cost | decimal | 否 | 运费 |
| tax_cost | decimal | 否 | 消费税 |
| items | JSONArrayString | 否 | 税项 |

​**items object**

| 名称 | 类型 | 是否必须 | 描述 |
| --- | --- | --- | --- |
| item_id | string | 否 | 商品唯一ID |
| item_name | string | 否 | 商品名称 |
| item_price | decimal | 否 | 商品价格 |
| price_currency | string | 否 | 请参阅[订单货币列表](https://developer-cn.cregis.com/api-reference/request-apis/payment/payment-order-currency) |
| item_quantity | integer(int64) | 否 | 商品数量 |

**sub_merchant object**

| 名称 | 类型 | 是否必须 | 描述 |
| --- | --- | --- | --- |
| sub_merchant_id | string | 否 | 子商户唯一编号 |
| sub_merchant_name | string | 否 | 商户名称 |

###### 请求示例

```
{
    "timestamp": 1734327655,
    "nonce": "dositl",
    "sign": "f5be13fdd8c6f63951ca4427359457cb",
    "order_id": "merchant_order_id_12334567",
    "order_amount": "5",
    "order_currency": "USD",
    "callback_url": "https://callback.com",
    "remark": "VIP order",
    "payer_id": "payer001",
    "payer_name": "payer",
    "payer_email": "payer@gmail.com",
    "valid_time": 60,
    "pid": 1419287201923072,
    "cancel_url": "https://cancel.merchant.com",
    "success_url": "https://success.merchant.com",
    "tokens": "[\"USDT-TRC20\",\"USDT-BEP20\"]",
    "order_details": "{\"items\": [{\"item_id\": \"10001\", \"item_name\": \"Product1\",\"item_price\": 123.00,\"item_quantity\": 1,\"price_currency\": \"USD\"},{\"item_id\": \"10002\", \"item_name\": \"Product2\",\"item_price\": 100.00,\"item_quantity\": 2,\"price_currency\": \"USD\"}],\"shopping_cost\": 10.88,\"tax_cost\": 10.00}"
    "sub_merchant": "{\"sub_merchant_id\": \"submerchant10001\",\"sub_merchant_name\": \"Merchant1\"}"
}
```

### 返回

#### 返回数据

| 名称 | 类型 | 描述 |
| --- | --- | --- |
| code | string | 返回码 |
| msg | string | 返回信息 |
| data | Object | 返回数据 |

#### `data` 对象

| 名称 | 类型 | 描述 |
| --- | --- | --- |
| cregis_id | string | Cregis唯⼀识标 |
| checkout_url | string | 收银台链接 |
| merchant_name | string | 收银台展示的商户名称 |
| merchant_logo_url | string | 收银台展示的商户logo |
| order_amount | string | 订单⾦额 |
| order_currency | string | 订单货币代码 |
| created_time | integer(int64) | 订单创建时间以13位unix时间戳格式 e.g. 1720508269092 , i.e. 2024-07-09 14:57:49 092 |
| expire_time | integer(int64) | 订单失效时间以13位unix时间戳格式 e.g. 1720508269092 , i.e. 2024-07-09 14:57:49 092 |
| payment_info | ArrayObject | 订单相关讯息 |

#### `payment_info` 对象

| 名称 | 类型 | 描述 |
| --- | --- | --- |
| payment_address | string | 充值地址 |
| token_symbol | string | 可用于支付的加密货币。可參考订单建立中的tokens参数 |
| blockchain | string | 货币⽹络 |
| token_name | string | 货币名称 |
| logo_url | string | 货币logo地址 |
| token_decimals | integer | 货币精度 |
| receive_amount | string | 汇率转换后的订单⾦额 |
| receive_currency | string | 汇率转换后订单货币代码 |
| exchange_rate | string | 货币汇率 |
| asset_logo | string | 资产标识logo |

###### 返回示例

```
{
    "code": "00000",
    "msg": "ok",
    "data": {
        "cregis_id": "po1420761885130752",
        "checkout_url": "http://192.168.2.60:9099?cid=b2fb433c953a43bd9bdfc3f611567887",
        "merchant_name": null,
        "merchant_logo_url": null,
        "order_amount": "5",
        "order_currency": "HKD",
        "created_time": 1734328473070,
        "expire_time": 1734332073070,
        "payment_info": [
            {
                "payment_address": "TX8GZJfLYPtmzRgd3FoiXuCHbptiQ37NSM",
                "token_symbol": "USDT",
                "blockchain": "TRON",
                "token_name": "USDT-TRC20",
                "logo_url": "https://static.cregis.io/pic/coin/USDT-Tron-TRC20@8x.png",
                "token_decimals": 6,
                "receive_amount": "0.642951",
                "receive_currency": "USDT",
                "exchange_rate": "0.1285902181",
                "asset_logo": "https://static.cregis.io/pic/coin/tether%20usd-usdt-with-currency@8x.png"
            },
            {
                "payment_address": "0xf9d70ab23195cfc9c0c14b384a1fd3a3f12cd4f8",
                "token_symbol": "USDT",
                "blockchain": "BNB-BSC",
                "token_name": "USDT-BEP20",
                "logo_url": "https://static.cregis.io/pic/coin/USDT-BNB%20Chain-BEP20@8x.png",
                "token_decimals": 18,
                "receive_amount": "0.642951",
                "receive_currency": "USDT",
                "exchange_rate": "0.1285902181",
                "asset_logo": "https://static.cregis.io/pic/coin/tether%20usd-usdt-with-currency@8x.png"
            },
        ]
    }
}
```
