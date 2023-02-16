package io.synaps.types;

import org.json.JSONException;
import org.json.JSONObject;

public class PersonhoodOverview {
    public Session session;
    public App app;
    public Verifier verifier;
    public Issuer issuer;
    public User user;
    public Facegraph facegraph;

    public PersonhoodOverview(JSONObject obj) throws JSONException {
        session = new Session(obj.getJSONObject("session"));
        app = new App(obj.getJSONObject("app"));
        verifier = new Verifier(obj.getJSONObject("verifier"));
        issuer = new Issuer(obj.getJSONObject("issuer"));
        user = new User(obj.getJSONObject("user"));
        facegraph = new Facegraph(obj.getJSONObject("facegraph"));
    }
}
