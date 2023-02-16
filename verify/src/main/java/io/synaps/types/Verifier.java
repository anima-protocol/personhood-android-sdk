package io.synaps.types;

import org.json.JSONObject;

public class Verifier {
    public String id;
    public String publicAddress;
    public String chain;

    public Verifier(JSONObject obj) {
        id = obj.optString("id");
        publicAddress = obj.optString("public_address");
        chain = obj.optString("chain");
    }
}
