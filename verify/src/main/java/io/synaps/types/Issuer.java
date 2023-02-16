package io.synaps.types;

import org.json.JSONObject;

public class Issuer {
    public String id;
    public String publicAddress;
    public String chain;

    public Issuer(JSONObject obj) {
        id = obj.optString("id");
        publicAddress = obj.optString("public_address");
        chain = obj.optString("chain");
    }
}
