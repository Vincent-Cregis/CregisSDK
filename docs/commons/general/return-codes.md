Title: 返回码 - Cregis Developer

URL Source: https://developer-cn.cregis.com/api-reference/return-codes

Markdown Content:
成功码
---

| 返回码 | 描述 |
| --- | --- |
| 00000 | 成功码只有一个，其他所有返回码都是表示执行出现异常，是错误码 |

失败码
---

| 返回码 | 返回内容 | 描述 |
| --- | --- | --- |
| Axxxx | Param Error | 参数错误 |
| B0001 | Signature Error | 签名错误 |
| T0062 | Please expand the number of API addresses or upgrade the subscription version first | 请先扩容API地址数量或者升级订阅版本 |
| E0001 | The IP is not added to the whitelist list | 白名单列表未添加该ip值 |
| E0002 | Address alias must be a combination of 40 characters or less | 备注必须是40位字符以内组合 |
| E0003 | Item does not exist or item status is anomalous | 项目不存在或项目状态异常 |
| E0004 | Unsupported interface type | 不支持的接口类型 |
| E0005 | Unsupported coin | 不支持的币种 |
| E0006 | The withdrawal amount must be greater than 0 | 提币金额需大于0 |
| E0007 | Incorrect accuracy of the withdrawal amount | 提币金额精度错误 |
| E0008 | Address is invalid | 地址不合法 |
| E0009 | Duplicate business number | 三方业务编号重复 |
| E0010 | Wallet does not exist | 钱包不存在 |
| E0014 | Transfer out of business unqueried to order information | 转出业务未查询到订单信息 |
| E0015 | Item order does not exist | 项目订单不存在 |
| E0016 | Amount error | 金额错误 |
| E0018 | Order number must be unique | 订单编号不允许重复 |
| E0019 | Order number exceeds 128 characters | 订单编号长度超过128位字符 |
| E0020 | Unsupported currency | 不支持的法币 |
| E0021 | Order remark exceeds 256 characters | 订单备注长度超过256位字符 |
| E0022 | Order amount must be greater than 0 | 订单金额需大于0 |
| E0023 | Order amount precision error | 订单金额精度错误 |
| E0024 | Order does not exist | 订单不存在 |
| E0025 | Incorrect payer information | 付款人信息有误 |
| E0026 | Please input in minutes. Minimum is 10 minutes,maximum 60 minutes | 订单超时时间至少十分钟 |
| E0027 | Number of addresses set incorrectly | 批量生成地址数量超出限制 |
| E0028 | Unsupported address status value | 不支持的地址状态值 |
| E0029 | At least one modification required | 至少一个修改项 |
| E0030 | Address update failed | 地址更新失败 |
| E0031 | Failed to create the checkout counter | 创建收银台失败 |
| E0032 | Memo content only supports numeric characters and must be less than 4294967295 | 无效的tag |
| E0033 | Please expand the number of API addresses or upgrade the subscription version first | 超出地址使用数量 |
| E0034 | The number of available withdrawals is insufficient | 超出提币次数 |
