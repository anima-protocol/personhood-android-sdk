package io.synaps.types;

import org.json.JSONObject;

public class App {
    public String name;
    public String logo;
    public String logoUrl;
    public boolean verify;
    public boolean forceWallet;

    public App(JSONObject obj) {
        name = obj.optString("name");
        logo = obj.optString("logo");
        logoUrl = obj.optString("logo_url");
        verify = obj.optBoolean("verify");
        forceWallet = obj.optBoolean("force_wallet");
    }
}
