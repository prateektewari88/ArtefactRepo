package com.ms.mydemofirebaseapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.analytics.FirebaseAnalytics;

import com.google.firebase.analytics.FirebaseAnalytics.Event;
import com.google.firebase.analytics.FirebaseAnalytics.Param;

public class MainActivity extends AppCompatActivity {

    /**
     * The {@code FirebaseAnalytics} used to record screen views.
     */
    // [START declare_analytics]
    private FirebaseAnalytics mFirebaseAnalytics;
    // [END declare_analytics]

    private SharedPreferences preferences;
    private SharedPreferences.OnSharedPreferenceChangeListener deepLinkListener;

    @Override
    protected void onStart() {
        super.onStart();
        preferences.registerOnSharedPreferenceChangeListener(deepLinkListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        preferences.unregisterOnSharedPreferenceChangeListener(deepLinkListener);
        deepLinkListener = null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        preferences = getSharedPreferences("google.analytics.deferred.deeplink.prefs", MODE_PRIVATE);
        deepLinkListener = (sharedPreferences, key) -> {
            Log.i("DEEPLINK_LISTENER", "Deep link changed");
            if ("deeplink".equals(key)) {
                String deeplink = sharedPreferences.getString(key, null);
                Double cTime = Double.longBitsToDouble(sharedPreferences.getLong("timestamp", 0)); Log.d("DEEPLINK_LISTENER", "Deep link retrieved: " + deeplink);
                showDeepLinkResult(deeplink);
            }
        };


        String action = getIntent().getAction();
        Uri data = getIntent().getData();

        if(Intent.ACTION_VIEW.equals(action) && data != null){

            try {
                if((data.getQueryParameter("text")!= null)) {
                    TextView mainActivityText = (TextView) findViewById(R.id.main_activity_text);
                    mainActivityText.setText(data.getQueryParameter("text"));
                }
            } catch (Exception e) {
                Log.i("LOG",e.toString());
            }

            /* Uncomment to launch final activity upon hitting the deeplink URL /main */
            //Intent intent = new Intent(MainActivity.this, FinalActivity.class);
            //startActivity(intent);
        }


        // [START shared_app_measurement]
        // Obtain the FirebaseAnalytics instance.
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        // [END shared_app_measurement]

        Bundle bundle = new Bundle();


        Button button = (Button) findViewById(R.id.next_button);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                bundle.putString("next_button_clicked", "1");
                mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
                Intent intent = new Intent(MainActivity.this, FinalActivity.class);
                startActivity(intent);
            }
        });
    }

    public void showDeepLinkResult(String result) {
        String toastText = result;
        if (toastText == null) {
            toastText = "The deep link retrieval failed";
        } else if (toastText.isEmpty()) {
            toastText = "Deep link empty";
        }
        //Toast.makeText(MainActivity.this, toastText, Toast.LENGTH_LONG).show();
        Log.d("DEEPLINK", toastText);
    }

}