package io.synaps;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.hardware.camera2.CameraAccessException;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.util.Log;
import android.webkit.ConsoleMessage;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import androidx.core.content.ContextCompat;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.String;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;

import io.synaps.types.PersonhoodOverview;

public class Personhood extends WebView {
    public OnInitListener onInitListener;
    public OnFinishListener onFinishListener;
    public OnSignListener onSignListener;
    public String sessionID;
    public boolean loaded = false;
    private String baseUrl = "https://pop.anima.io";

    public Personhood(Context context) {
        super(context);
        try {
            this.init(context, null, 0);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        this.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
                Log.d("PersonhoodWeb", consoleMessage.message() + " -- From line "
                        + consoleMessage.lineNumber() + " of "
                        + consoleMessage.sourceId());
                return super.onConsoleMessage(consoleMessage);
            }
        });
    }

    public Personhood(Context context, AttributeSet attrs) {
        super(context, attrs);
        try {
            this.init(context, attrs, 0);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    public Personhood(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        try {
            this.init(context, attrs, defStyleAttr);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) throws URISyntaxException {
        this.getSettings().setJavaScriptEnabled(true);
        this.addJavascriptInterface(new SynapsWebInterface(context, this), "__pop_android_client");
        this.onInitListener = () -> {
            this.loaded = true;
        };
        this.setBackgroundColor(Color.TRANSPARENT);
    }

    public void setOnInitListener(OnInitListener onInitListener) {
        this.onInitListener = () -> {
            this.loaded = true;
            onInitListener.onInit();
        };
    }

    public void setOnFinishListener(OnFinishListener onFinishListener) {
        this.onFinishListener = onFinishListener;
    }

    public void setOnSignListener(OnSignListener onSignListener) {
        this.onSignListener = onSignListener;
    }

    public void launch(String sessionID, String walletAddress) throws CameraAccessException {
        this.sessionID = sessionID;

        String query = String.format("session_id=%s&platform=android", sessionID);
        if (walletAddress != null) {
            query += String.format("&wallet_address=%s", walletAddress);
        }

        String url = String.format("%s?%s", this.baseUrl, query);

        if (ContextCompat.checkSelfPermission(
                this.getContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            this.setWebChromeClient(new PoPWebViewClient());
            this.loadUrl(url);
        } else {
            throw new CameraAccessException(CameraAccessException.CAMERA_DISABLED);
        }

    }

    public void open(String overview) {
        this.loadUrl(String.format("javascript:window.__pop_android_open('%s')", overview));
    }

    public void sign(String payload, String signature) {
        this.loadUrl(String.format("javascript:window.__pop_android_sign('%s', '%s')", payload, signature));
    }
}
