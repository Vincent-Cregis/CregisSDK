Title: 币种码 - Cregis Developer

URL Source: https://developer-cn.cregis.com/api-reference/currency-identifiers

Markdown Content:
格式
--

`currency`支持两种格式:

*   币种名称 (`name`).
*   链编号@币种编号 (`chain_id@token_id`).

举例
--

以下表格中前两个币种的`currency`参数分别为:

*   `Ethereum` 或 `60@60`
*   `USDT-ERC20` 或 `60@0xdac17f958d2ee523a2206206994597c13d831ec7`

币种编码列表
------

| 币种名称 | 链编号(chain_id) | 币种编号(token_id) | 币种精度 |
| --- | --- | --- | --- |
| Bitcoin | 0 | 0 | 8 |
| Ethereum | 60 | 60 | 18 |
| USDT-ERC20 | 60 | 0xdac17f958d2ee523a2206206994597c13d831ec7 | 6 |
| USDC-ERC20 | 60 | 0xa0b86991c6218b36c1d19d4a2e9eb0ce3606eb48 | 6 |
| POL-ERC20 | 60 | 0x455e53cbb86018ac2b8092fdcd39d8444affc3f6 | 18 |
| Uniswap-ERC20 | 60 | 0x1f9840a85d5af5bf1d1762f925bdaddc4201f984 | 18 |
| ChainLink-ERC20 | 60 | 0x514910771af9ca656af840dff83e8264ecf986ca | 18 |
| SHIB-ERC20 | 60 | 0x95ad61b0a150d79219dcf64e1e6cc01f0b64c4ce | 18 |
| TRON | 195 | 195 | 6 |
| USDT-TRC20 | 195 | TR7NHqjeKQxGTCi8q8ZY4pL8otSzgjLj6t | 6 |
| USDC-TRC20 | 195 | TEkxiTehnzSmSe2XqrBj4w32RUN966rdz8 | 6 |
| BNB-BSC | 2510 | 2510 | 18 |
| USDT-BEP20 | 2510 | 0x55d398326f99059ff775485246999027b3197955 | 18 |
| USDC-BEP20 | 2510 | 0x8ac76a51cc950d9822d68b83fe1ad97b32cd580d | 18 |
| Dogecoin | 3 | 3 | 8 |
| Litecoin | 2 | 2 | 8 |
| Bitcoin Cash | 5 | 5 | 8 |
| POL-Polygon | 62 | 62 | 18 |
| USDT-Polygon | 62 | 0xc2132d05d31c914a87c6611c10748aeb04b58e8f | 6 |
| USDC-Polygon | 62 | 0x3c499c542cef5e3811e1192ce70d8cc03d5c3359 | 6 |
| Arbitrum One | 63 | 63 | 18 |
| USDT-Arbitrum One | 63 | 0xfd086bc7cd5c481dcc9c85ebe478a1c0b69fcbb9 | 6 |
| USDC-Arbitrum One | 63 | 0xaf88d065e77c8cc2239327c5edb3a432268e5831 | 6 |
| Optimism | 64 | 64 | 18 |
| USDT-Optimism | 64 | 0x94b008aa00579c1307b0ef2c499ad98a8ce58e58 | 6 |
| USDC-Optimism | 64 | 0x0b2c639c533813f4aa9d7837caf62653d097ff85 | 6 |
| Avalanche-C | 65 | 65 | 18 |
| USDT-Avalanche-C | 65 | 0x9702230a8ea53601f5cd2dc00fdbc13d4df4a8c7 | 6 |
| USDC-Avalanche-C | 65 | 0xb97ef9ef8734c71904d8002f8b6bc66dd9c48a6e | 6 |
| Base | 66 | 66 | 18 |
| USDT-Base | 66 | 0xfde4c96c8593536e31f229ea8f37b2ada2699bb2 | 6 |
| USDC-Base | 66 | 0x833589fcd6edb6e08f4c7c32d4f71b54bda02913 | 6 |
| opBNB | 67 | 67 | 18 |
| Solana | 1000 | 1000 | 9 |
| USDT-Solana | 1000 | Es9vMFrzaCERmJfrF4H2FYD4KCoNkY11McCe8BenwNYB | 6 |
| USDC-Solana | 1000 | EPjFWdd5AufqSSqeM2qN1xzybapC8G4wEGGkZwyTDt1v | 6 |
| TON | 2000 | 2000 | 9 |
| USDT-Jetton | 2000 | EQCxE6mUtQJKFnGfaROTKOt1lZbDiiX1kCixRv7Nw2Id_sDs | 6 |
| Ethereum Classic | 61 | 61 | 18 |
| Filecoin | 2307 | 2307 | 18 |
| XRP-Ripple | 144 | 144 | 6 |
| SUI | 3000 | 3000 | 9 |
