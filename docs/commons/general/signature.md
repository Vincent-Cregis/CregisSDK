Title: 签名规则 - Cregis Developer

URL Source: https://developer-cn.cregis.com/api-reference/signature

Markdown Content:
签名算法
----

1.   请求参数中除去sign和空值参数外，其他参数**按字典序排序**.

字典序定义：在英文字典中，排列单词的顺序是先按照第一个字母以升序排列（即 a、b、c……z 的顺序）；如果第一个字母一样，那么比较第二个、第三个乃至后面的字母。如果比到最后两个单词不一样长（比如，sigh 和 sight），那么把短者排在前。

1.   排序完成后，按照”key1value1key2value2…”进行拼接
2.   将项目的API Key拼接到第 2 步中得到的字符串最前面
3.   将第 3 步中获取的字符串使用散列算法取md5并转为小写即为sign，将sign添加到请求参数中

签名示例
----

**步骤 1** 假设项目 `API Key` 为:

```
f502a9ac9ca54327986f29c03b271491
```

**除 sign 外请求参数** 为:

```
{
  "pid": 1382528827416576,
  "currency": "195@195",
  "address": "TXsmKpEuW7qWnXzJLGP9eDLvWPR2GRn1FS",
  "amount": "1.1",
  "remark": "payout",
  "third_party_id": "c9231e604da54469a735af3f449c880f",
  "callback_url": "http://192.168.2.29:9099/callback",
  "nonce": "hwlkk6",
  "timestamp": 1688004243314
}
```

**步骤 2** （非空参数key按字典序排序）得到的字符串为：

```
addressTXsmKpEuW7qWnXzJLGP9eDLvWPR2GRn1FSamount1.1callback_urlhttp://192.168.2.29:9099/callbackcurrency195@195noncemb8udupid1382528827416576remarkpayoutthird_party_id19faf9d3c8f34caf926f332f3021e887timestamp1688003966801
```

**步骤 3** （拼接项目key）的到字符串为

```
f502a9ac9ca54327986f29c03b271491addressTXsmKpEuW7qWnXzJLGP9eDLvWPR2GRn1FSamount1.1callback_urlhttp://192.168.2.29:9099/callbackcurrency195@195noncemb8udupid1382528827416576remarkpayoutthird_party_id19faf9d3c8f34caf926f332f3021e887timestamp1688003966801
```

**步骤 4** （取md5）得到的 sign 为：

```
c9bae061ae3f5f8d3bfde817f6966c36
```

**最后** 发送的请求参数为（增加了sign）:

```
{
  "pid": 1382528827416576,
  "currency": "195@195",
  "address": "TXsmKpEuW7qWnXzJLGP9eDLvWPR2GRn1FS",
  "amount": "1.1",
  "remark": "payout",
  "third_party_id": "c9231e604da54469a735af3f449c880f",
  "callback_url": "http://192.168.2.29:9099/callback",
  "nonce": "hwlkk6",
  "timestamp": 1688004243314,
  "sign": "d6eef2de79e39f434a38efb910213ba6"
}
```
