Title: 查询交易记录 - Cregis Developer

URL Source: https://developer-cn.cregis.com/api-reference/request-apis/global/trade-query

Markdown Content:
### 接入API

POST /api/v1/trade/page 查询商户的交易记录信息

### 请求

#### 请求参数

| 名称 | 类型 | 是否必须 | 描述 |
| --- | --- | --- | --- |
| cid | integer(int64) | 否 | cregis订单编号 |
| tx_id | string | 否 | 交易hash |
| status | int | 否 | 交易状态【0-待确认 1-成功 2-失败】 |
| trade_type | int | 否 | 交易类型【1-转入 2-转出】 |
| business_type | int | 否 | 【0:普通交易 2:审核交易 3:地址收款 4:归集手续费 5:归集】 |
| blocktime_start | integer(int64) | 否 | 区块时间-查询开始时间戳【10位】 |
| blocktime_end | integer(int64) | 否 | 区块时间-查询结束时间戳【10位】 |
| chain_id | string | 否 | 链编号chain_id |
| token_id | string | 否 | 代币编号 |
| page_num | int | 否 | 分页页数，如果不填，默认为1 |
| page_size | int | 否 | 分页每页数量，如果不填默认为10，如果填写了，限制最大值为100 |
| pid | integer(int64) | 是 | 项目编号 |
| nonce | string | 是 | 6位随机字符串 |
| timestamp | integer(int64) | 是 | 时间戳 |
| sign | string | 是 | 签名 |

###### 请求示例

```
{
    "cid": 1410580986806272,
    "tx_id": "0xa795af0324b1e3b862ae0c271bc8b3d99c7a1b72a6a0088a10c84403d0e6f27d",
    "status": 1,
    "trade_type": 1,
    "business_type": 3,
    "blocktime_start": 1721900581,
    "blocktime_end":1721998633,
    "chain_id": "2610",
    "token_id": "0x337610d27c682e347c9cd60bd4b3b107c9d34ddd",
    "page_num": 1,
    "page_size": 10,
    "pid": 1382528827416576,
    "remark": "payout",
    "nonce": "hwlkk6",
    "timestamp": 1688004243314,
    "sign": "d6eef2de79e39f434a38efb910213ba6"
}
```

### 返回

#### 返回结果

| 名称 | 类型 | 描述 |
| --- | --- | --- |
| code | string | 返回码 |
| msg | string | 返回信息 |
| data | object | 返回数据 |

##### 返回数据 `data` 对象

| 名称 | 类型 | 描述 |
| --- | --- | --- |
| pid | integer(int64) | 项目编号 |
| cid | integer(int64) | 系统流水号 |
| chain_id | string | 链编号 |
| token_id | string | 代币编号 |
| currency | string | 币种标识 |
| to_address | string | to地址 |
| amount | string | 金额 |
| status | integer(int32) | 交易状态【0-待确认 1-成功 2-失败】 |
| txid | string | 交易hash |
| block_height | string | 区块高度 |
| block_time | integer(int64) | 区块时间 |
| fee | string | 交易费用 |
| from_address | string | from地址 |

###### 返回示例

```
{
    "code": "00000",
    "msg": "ok",
    "data": {
        "total": 1,
        "rows": [
            {
                "pid": 1409650233999360,
                "cid": 1410580986806272,
                "chain_id": "2610",
                "token_id": "0x337610d27c682e347c9cd60bd4b3b107c9d34ddd",
                "to_address": "0x2d4124dcf3ca13acaff5579e1aa962c968df9092",
                "amount": "0.1",
                "status": 1,
                "txid": "0xa795af0324b1e3b862ae0c271bc8b3d99c7a1b72a6a0088a10c84403d0e6f27d",
                "block_height": "42393516",
                "block_time": 1721900581,
                "fee": "0.00258015",
                "from_address": "0xbd5ba5a08849c8a2c8ef188778ec383682b0063b"
            }
        ],
        "pageNum": 1,
        "pageSize": 10
    }
}
```
