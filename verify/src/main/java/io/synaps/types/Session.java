package io.synaps.types;

import org.json.JSONException;
import org.json.JSONObject;

public class Session {
    public SessionInfo info;
    public SessionState state;
    public boolean sandbox;

    public Session(JSONObject obj) throws JSONException {
        sandbox = obj.optBoolean("sandbox", false);
        info = SessionInfo.valueOf(obj.getString("info"));
        state = SessionState.valueOf(obj.getString("state"));
    }
}
