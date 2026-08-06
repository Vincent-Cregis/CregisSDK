package com.cregis.sdk;

import com.cregis.sdk.core.signer.CregisSigner;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

public class SignerTest {

    @Test
    public void testSignatureMatchesDocs() {
        String apiKey = "f502a9ac9ca54327986f29c03b271491";
        String expectedSign = "f76fb193e9d34d2e59fef64e3418f79b";

        Map<String, Object> params = new HashMap<>();
        params.put("pid", 1382528827416576L);
        params.put("currency", "195@195");
        params.put("address", "TXsmKpEuW7qWnXzJLGP9eDLvWPR2GRn1FS");
        params.put("amount", "1.1");
        params.put("remark", "payout");
        params.put("third_party_id", "c9231e604da54469a735af3f449c880f");
        params.put("callback_url", "https://your-domain.com/callback");
        params.put("nonce", "hwlkk6");
        params.put("timestamp", 1688004243314L);

        String actualSign = CregisSigner.sign(params, apiKey);

        Assertions.assertEquals(expectedSign, actualSign);
    }
}
