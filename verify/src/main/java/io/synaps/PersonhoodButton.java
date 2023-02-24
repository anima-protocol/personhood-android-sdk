package io.synaps;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.hardware.camera2.CameraAccessException;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;

import io.synaps.types.PersonhoodOverview;
import io.synaps.types.Session;
import io.synaps.types.SessionState;
import kotlin.jvm.JvmOverloads;

public class PersonhoodButton extends FrameLayout {
    final String apiUrl = "https://api.pop.anima.io/v1/personhood/overview";
    String sessionId = null;
    String walletAddress = null;
    boolean opened = false;

    public PersonhoodOverview overview = null;
    private String overviewJson = null;

    private Personhood pop = null;
    private Dialog dialog = null;

    private TextView statusText = null;
    private ImageView statusIcon = null;
    private RotateAnimation rotate = null;

    public @JvmOverloads PersonhoodButton(Context context) {
        super(context);
        init();
    }

    public @JvmOverloads PersonhoodButton(Context context, AttributeSet ste) {
        super(context, ste);
        init();
    }

    public @JvmOverloads PersonhoodButton(Context context, AttributeSet ste, int defStyle) {
        super(context, ste, defStyle);
        init();
    }

    public void launch(String sessionId, String walletAddress) throws CameraAccessException {
        if (sessionId == null) {
            return;
        }

        statusText.setText("Loading...");

        this.sessionId = sessionId;
        this.walletAddress = walletAddress;
        pop.launch(sessionId, walletAddress);
        getOverview();
    }

    public void launch(String sessionId) throws CameraAccessException {
        launch(sessionId, null);
    }

    private void getOverview() {
        if (sessionId == null) {
            return;
        }

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            AtomicReference<String> overviewResponse = new AtomicReference<>();
            AtomicReference<PersonhoodOverview> popOverview = new AtomicReference<>();
            AtomicReference<String> error = new AtomicReference<>();

            try {
                URL apiUrl = new URL(this.apiUrl);
                HttpURLConnection con = (HttpURLConnection) apiUrl.openConnection();
                con.setRequestMethod("GET");
                con.setRequestProperty("Content-Type", "application/json");
                con.setRequestProperty("Accept", "application/json");
                con.setRequestProperty("User-Agent", "Synaps Android SDK");
                con.setRequestProperty("Session-Id", sessionId);
                if (walletAddress != null) {
                    con.setRequestProperty("Wallet", walletAddress);
                }

                int responseCode = con.getResponseCode();
                Log.d("Personhood", "GET Response Code: " + responseCode);
                if (responseCode == HttpURLConnection.HTTP_OK) { // success
                    BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                    String inputLine;
                    StringBuffer response = new StringBuffer();

                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }
                    in.close();

                    String responseString = response.toString();
                    overviewResponse.set(responseString);
                    Log.d("Personhood", responseString);
                    JSONObject responseJson = new JSONObject(responseString);
                    popOverview.set(new PersonhoodOverview(responseJson));
                } else {
                    Log.d("Personhood", "GET request failed");
                    error.set("Unexpected session error");
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.d("Personhood", "GET request failed" + e.getMessage());
                error.set("Unexpected session error");
            }

            handler.post(new Runnable() {
                @Override
                public void run() {
                    String err = error.get();
                    if (err != null) {
                        setError(err);
                        return;
                    }

                    if (popOverview.get() == null || overviewResponse.get() == null) {
                        return;
                    }

                    overview = popOverview.get();
                    overviewJson = overviewResponse.get();
                    openModal();
                    updateSession(overview.session);
                }
            });
        });
    }

    private void updateSession(Session session) {
        if (session == null) {
            return;
        }

        if (session.state == SessionState.APPROVED) {
            statusText.setText("Verified");
            statusText.setTextColor(Color.parseColor("#22B55D"));
            statusIcon.setImageResource(R.drawable.check_solid);
            statusIcon.clearAnimation();
        } else if (session.state == SessionState.REJECTED) {
            setError("Rejected");
        } else {
            statusIcon.clearAnimation();
            statusIcon.setImageResource(0);
            statusText.setText("Are you human?");
            statusText.setTextColor(Color.parseColor("#BABABA"));
        }
    }

    private void setError(String errorMessage) {
        statusText.setText(errorMessage);
        statusText.setTextColor(Color.parseColor("#EF4444"));
        statusIcon.setImageResource(R.drawable.xmark_solid);
        statusIcon.clearAnimation();
    }

    private void openModal() {
        this.post(new Runnable() {
            @Override
            public void run() {
                if (opened) {
                    if (pop.loaded && overview != null) {
                        if (overview.session.state == SessionState.IN_PROGRESS) {
                            statusIcon.clearAnimation();
                            statusIcon.setImageResource(0);
                        }
                        dialog.show();
                        pop.open(overviewJson);
                    } else {
                        statusIcon.setImageResource(R.drawable.loader);
                        statusIcon.startAnimation(rotate);
                    }
                }
            }
        });
    }

    private void closeModal() {
        opened = false;
        dialog.dismiss();
    }

    public void setOnFinishListener(OnFinishListener listener) {
        pop.setOnFinishListener(session -> {
            closeModal();
            this.post(() -> {
                updateSession(session);
            });
            listener.onFinish(session);
        });
    }

    public void setOnSignListener(OnSignListener listener) {
        pop.setOnSignListener((payload) -> {
            if (listener != null) {
                listener.onSign(payload, (signature) -> {
                    this.post(() -> {
                        pop.sign(payload, signature);
                    });
                }, () -> {
                    this.post(() -> {
                        closeModal();
                    });
                });
            }
        });
    }

    private void init() {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.personhood_button, this);

        rotate = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotate.setDuration(1000);
        rotate.setRepeatCount(Animation.INFINITE);
        rotate.setInterpolator(new LinearInterpolator());

        statusText = findViewById(R.id.text);
        statusIcon = findViewById(R.id.logo);
        statusIcon.startAnimation(rotate);

        dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.personhood);
        dialog.getWindow().setLayout(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        pop = dialog.findViewById(R.id.synaps);

        pop.setOnInitListener(() -> {
            openModal();
        });

        pop.setOnFinishListener(session -> {
            closeModal();
            this.post(() -> {
                updateSession(session);
            });
        });

        statusIcon.setOnClickListener(v -> {
            opened = true;
            openModal();
        });

        dialog.setOnDismissListener(dialog -> {
            dialog.dismiss();
        });

        dialog.setOnCancelListener(dialog -> {
            dialog.dismiss();
        });
    }
}
