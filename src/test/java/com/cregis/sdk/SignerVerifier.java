package com.cregis.sdk;

import com.cregis.sdk.core.signer.CregisSigner;
import java.util.HashMap;
import java.util.Map;

public class SignerVerifier {

    public static void main(String[] args) {
        System.out.println("Starting Signer Verification...");

        // Test vector from https://developer-cn.cregis.com/api-reference/signature
        String apiKey = "f502a9ac9ca54327986f29c03b271491";
        String expectedSign = "d6eef2de79e39f434a38efb910213ba6";

        Map<String, Object> params = new HashMap<>();
        params.put("pid", 1382528827416576L);
        params.put("currency", "195@195");
        params.put("address", "TXsmKpEuW7qWnXzJLGP9eDLvWPR2GRn1FS");
        params.put("amount", "1.1");
        params.put("remark", "payout");
        params.put("third_party_id", "c9231e604da54469a735af3f449c880f");
        params.put("callback_url", "http://192.168.2.29:9099/callback");
        params.put("nonce", "hwlkk6");
        params.put("timestamp", 1688004243314L);

        String actualSign = CregisSigner.sign(params, apiKey);

        System.out.println("Expected: " + expectedSign);
        System.out.println("Actual:   " + actualSign);

        if (expectedSign.equals(actualSign)) {
            System.out.println("SUCCESS: Signature matches!");
        } else {
            System.err.println("FAILURE: Signature mismatch!");
            System.exit(1);
        }
    }
}
