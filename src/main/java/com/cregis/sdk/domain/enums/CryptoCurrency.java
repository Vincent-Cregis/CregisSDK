package com.cregis.sdk.domain.enums;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Supported Crypto Currencies for Pay-in.
 */
public enum CryptoCurrency {
    USDT_TRC20("USDT-TRC20"),
    USDT_ERC20("USDT-ERC20"),
    USDT_BEP20("USDT-BEP20"),
    USDT_POLYGON("USDT-Polygon"),
    USDT_AVALANCHE_C("USDT-Avalanche-C"),
    USDT_ARBITRUM_ONE("USDT-Arbitrum One"),
    USDT_SOLANA("USDT-Solana"),

    USDC_BASE("USDC-Base"),
    USDC_ERC20("USDC-ERC20"),
    USDC_SOLANA("USDC-Solana"),
    USDC_BEP20("USDC-BEP20"),
    USDC_POLYGON("USDC-Polygon"),
    USDC_AVALANCHE_C("USDC-Avalanche-C"),
    USDC_ARBITRUM_ONE("USDC-Arbitrum One"),
    USDC_OPTIMISM("USDC-Optimism"),

    BTC("Bitcoin"),
    SOL("Solana"),
    ETH("Ethereum"),
    ETH_BASE("Base"),
    ETH_ARBITRUM_ONE("Arbitrum One"),
    ETH_OPTIMISM("Optimism"),
    TRX("TRON"),
    BNB("BNB-BSC");

    private final String value;

    CryptoCurrency(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }
}
