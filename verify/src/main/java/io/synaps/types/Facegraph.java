package io.synaps.types;

import org.json.JSONObject;

public class Facegraph {
    public String id;
    public String hash;

    public Facegraph(JSONObject obj) {
        id = obj.optString("id");
        hash = obj.optString("hash");
    }
}
