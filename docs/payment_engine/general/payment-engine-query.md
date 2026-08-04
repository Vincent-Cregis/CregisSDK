> ## Documentation Index
> Fetch the complete documentation index at: https://developer-cn.cregis.com/llms.txt
> Use this file to discover all available pages before exploring further.

# 查询订单信息

> 查询订单详情

### 接入API

POST /api/v2/order/info

### 请求

#### 请求参数

| 名称         | 类型             | 是否必须 | 描述         |
| ---------- | -------------- | ---- | ---------- |
| pid        | integer(int64) | 是    | 项目编号       |
| nonce      | string         | 是    | 6位随机字符串    |
| timestamp  | integer(int64) | 是    | 时间戳        |
| sign       | string         | 是    | 签名         |
| cregis\_id | string         | 是    | cregis订单编号 |

###### 请求示例

```json  theme={null}
{
    "cregis_id": "po20240703132452000",
    "pid": 1382528827416576,
    "nonce": "hwlkk6",
    "timestamp": 1688004243314,
    "sign": "d6eef2de79e39f434a38efb910213ba6"
}
```

### 返回

#### 返回数据

| 名称   | 类型     | 描述   |
| ---- | ------ | ---- |
| code | string | 返回码  |
| msg  | string | 返回信息 |
| data | object | 返回数据 |

#### 返回数据 `data` 对象

| 名称                 | 类型             | 描述                                                                                                                                       |
| ------------------ | -------------- | ---------------------------------------------------------------------------------------------------------------------------------------- |
| cregis\_id         | string         | cregis订单编号                                                                                                                               |
| order\_id          | string         | 调用方订单编号                                                                                                                                  |
| order\_amount      | string         | 订单金额                                                                                                                                     |
| order\_currency    | string         | 订单币种                                                                                                                                     |
| created\_time      | integer(int64) | 订单创建时间以13位unix时间戳格式 <br /> e.g. 1750407942433 , i.e. 2024-07-09 14:57:49 092                                                             |
| cancel\_time       | integer(int64) | 订单取消时间以13位unix时间戳格式 <br /> e.g. 1750407942433 , i.e. 2024-07-09 14:57:49 092                                                             |
| transact\_time     | integer(int64) | 订单完成时间以13位unix时间戳格式 <br /> e.g. 1750407942433 , i.e. 2024-07-09 14:57:49 092                                                             |
| valid\_time        | integer(int64) | 单位：分钟（可填写：10 到 60 的整数） <br /> 订单的有效时间。如超过有效时间，将不可以再支付。                                                                                   |
| status             | string         | 订单支付状态 <br /> new - 订单初始状态 <br /> paid - 订单已成功支付 <br /> expired - 订单超出有效时间 <br /> paid\_over - 付款超出订单金额  <br /> paid\_partial - 付款小于订单金额 |
| refund\_requested  | string         | 订单是否发生了退款 <br /> no：无退款申请记录 <br /> yes：有退款申请记录                                                                                           |
| refund\_data       | object         | 退款订单相关讯息                                                                                                                                 |
| payer\_id          | string         | 付款人信息                                                                                                                                    |
| payer\_name        | string         | 付款人名称                                                                                                                                    |
| payer\_email       | string         | 付款人邮箱                                                                                                                                    |
| remark             | string         | 订单备注信息                                                                                                                                   |
| payment\_detail    | ArrayObject    | 付款记录                                                                                                                                     |
| payment\_info      | ArrayObject    | 请求付款信息                                                                                                                                   |
| order\_details     | Object         | 商品信息                                                                                                                                     |
| settlement\_status | string         | 结算状态 <br /> - unsettled：尚未结算 <br /> - settling：正在结算 <br /> - settled：结算已完成                                                               |
| settlement\_type   | string         | 结算类型 <br /> - system：自动结算 <br /> - manual：手动结算                                                                                           |
| sub\_merchant      | Object         | 子商户信息                                                                                                                                    |

**order\_details object**

| 名称             | 类型          | 描述         |
| -------------- | ----------- | ---------- |
| shopping\_cost | decimal     | 订单的运费      |
| tax\_cost      | decimal     | 订单的税费      |
| items          | ArrayObject | 订单内购买商品的详情 |

​**items object**

| 名称              | 类型             | 描述     |
| --------------- | -------------- | ------ |
| item\_id        | string         | 商品唯一ID |
| item\_name      | string         | 商品名称   |
| item\_price     | decimal        | 商品价格   |
| price\_currency | string         | 商品币种   |
| item\_quantity  | integer(int64) | 商品数量   |

**sub\_merchant object**

| 名称                  | 类型     | 描述      |
| ------------------- | ------ | ------- |
| sub\_merchant\_id   | string | 子商户唯一编号 |
| sub\_merchant\_name | string | 子商户名称   |

**payment\_detail object**

| 名称                | 类型     | 描述         |
| ----------------- | ------ | ---------- |
| payment\_address  | string | 充值地址       |
| from\_address     | string | 支付方地址      |
| receive\_amount   | string | 汇率转换后的订单金额 |
| receive\_currency | string | 汇率转换后的订单币种 |
| pay\_amount       | string | 实际到账金额     |
| pay\_currency     | string | 到账币种       |
| exchange\_rate    | string | 汇率         |
| tx\_id            | string | 交易hash     |
| blockchain        | string | 货币网络       |
| token\_name       | string | 货币名称       |

**payment\_info object**

| 名称                | 类型      | 描述                   |
| ----------------- | ------- | -------------------- |
| payment\_address  | string  | 充值地址                 |
| token\_symbol     | string  | 币种。可参考订单建立中的tokens参数 |
| blockchain        | string  | 货币网络                 |
| token\_name       | string  | 货币名称                 |
| logo\_url         | string  | 货币logo地址             |
| token\_decimals   | integer | 货币精度                 |
| receive\_amount   | string  | 汇率转换后的该收订单金额         |
| receive\_currency | string  | 汇率转换后订单货币代码          |
| exchange\_rate    | string  | 货币汇率                 |
| asset\_logo       | string  | 币种标识logo             |

**refund\_data object**

| 名称                     | 类型             | 描述                                                                              |
| ---------------------- | -------------- | ------------------------------------------------------------------------------- |
| refund\_id             | integer(int64) | Cregis退款订单编号                                                                    |
| cregis\_id             | string         | Cregis支付订单唯一编号                                                                  |
| reference\_id          | string         | 商家内部的原订单编号                                                                      |
| recipient\_id          | string         | 商家内部的客户编号                                                                       |
| recipient\_name        | string         | 退款收款人姓名                                                                         |
| recipient\_email       | string         | 退款收款人邮箱                                                                         |
| recipient\_address     | string         | 退款收款人的链上地址                                                                      |
| token                  | string         | 退款订单的币种填入加密货币token\_name，格式：USDT-BEP20，USDT-TRC20                               |
| network                | string         | 退款订单的网络 blockchain                                                              |
| refund\_amount         | string         | 退款金额                                                                            |
| refund\_fee            | string         | 退款所需gas fee                                                                     |
| actual\_refund\_amount | string         | 扣减矿工费后的实际退款金额                                                                   |
| type                   | integer        | 退款类型 <br /> 1：代表 部分退款 <br /> 2：代表 全额退款                                          |
| refund\_status         | integer        | 退款状态 <br /> 0: 代表 退款处理中 <br /> 1：代表 退款成功 <br /> 2：代表 退款失败                       |
| refund\_tx\_id         | string         | 退款交易哈希                                                                          |
| refund\_created\_time  | string         | 退款订单创建时间以13位unix时间戳格式 <br /> e.g. 1720508269092 ,i.e. 2024-07-09 14:57:49 092   |
| refund\_transact\_time | string         | 退款订单最后更新时间以13位unix时间戳格式 <br /> e.g. 1720508269092 ,i.e. 2024-07-09 14:57:49 092 |

###### 返回示例

```json  theme={null}
{
    "code": "00000",
    "msg": "ok",
    "data": {
        "cregis_id": "po1420761885130752",
        "order_id": "merchant_order_id_123",
        "order_amount": "5",
        "order_currency": "HKD",
        "created_time": 1734328473070,
        "cancel_time": null,
        "transact_time": 1734328473872,
        "valid_time": 60,
        "status": "paid",
        "payer_id": "payer001",
        "payer_name": "payer",
        "payer_email": "12345678@email.com",
        "remark": "收银台备注",
        "payment_detail": [
            {
                "payment_address": "TUX9zGoPFVXd6RC7pg18nfVLATeN9vgBeX",
                "from_address": "THMxY6QeMYqACWs8fiL4VGBz2ZJRp4mDhT",
                "receive_amount": "10.00",
                "receive_currency": "USDT",
                "pay_amount": "10",
                "pay_currency": "USDT",
                "exchange_rate": "1",
                "tx_id": "d51bc829da5ad05c14f8f0a6ae16708129e485346ad91b8d5f2d33e490b91815",
                "blockchain": "TRON",
                "token_name": "USDT-TRC20"
            }
        ],
        "payment_info": [
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
            }
        ],
        "order_details": {
            "items": [
                {
                    "item_id": "1",
                    "item_name": "Product1",
                    "item_price": 123,
                    "price_currency": "HKD",
                    "item_quantity": 1
                },
                {
                    "item_id": "1",
                    "item_name": "Product2",
                    "item_price": 124,
                    "price_currency": "HKD",
                    "item_quantity": 1
                }
            ],
            "shopping_cost": 10.88,
            "tax_cost": 100
        },
        "settlement_status": "unsettled",
        "settlement_type": "",
        "sub_merchant": {
            "sub_merchant_id": "123",
            "sub_merchant_name": "kg"
        }
    }
}
```
