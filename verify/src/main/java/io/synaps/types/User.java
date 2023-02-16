package io.synaps.types;

import org.json.JSONObject;

public class User {
    public boolean anime;
    public String wallet;

    public User(JSONObject obj) {
        anime = obj.optBoolean("anime");
        wallet = obj.optString("wallet");
    }
}
