package io.synaps;

import android.content.Context;
import android.util.Log;
import android.webkit.JavascriptInterface;

import org.json.JSONException;
import org.json.JSONObject;

import io.synaps.types.Session;

public class SynapsWebInterface {
    Context mContext;
    Personhood synaps;

    /** Instantiate the interface and set the context */
    SynapsWebInterface(Context c, Personhood synaps) {
        mContext = c;
        this.synaps = synaps;
    }

    @JavascriptInterface
    public void init(String sessionId) {
        if (!synaps.sessionID.equals(sessionId)) {
            Log.d("Synaps", "Session ID mismatch: " + synaps.sessionID + " != " + sessionId);
        }

        if(synaps.onInitListener != null) {
            this.synaps.onInitListener.onInit();
        }
    }

    @JavascriptInterface
    public void finish(String json) {
        try {
            JSONObject obj = new JSONObject(json);
            Session session = new Session(obj);
            if(synaps.onFinishListener != null) {
                this.synaps.onFinishListener.onFinish(session);
            }
        } catch (JSONException e) {
            Log.d("Synaps", "Error parsing session: " + e.getMessage());
        }
    }

    @JavascriptInterface
    public void sign(String payload) {
        if(synaps.onSignListener != null) {
            this.synaps.onSignListener.onSign(payload);
        }
    }
}
