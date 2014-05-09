package org.foo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

public class StopperActivity extends Activity {

    public static final String ACTION = StopperActivity.class.getName() + ".ACTION";
    public static final String PARAM_TEST_NAME = "testName";
    public static final String PARAM_KILL = "kill";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Button button = new Button(this);

        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.gravity = Gravity.RIGHT;
        lp.setMargins(0, 16, 0, 0);
        button.setLayoutParams(lp);

        button.setPadding(32, 0, 32, 0);
        button.setText("Stop test");

        FrameLayout linearLayout = new FrameLayout(this);
        linearLayout.addView(button);
        setContentView(linearLayout);

        button.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        stopTest();
                    }
                });
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            displayTestName(extras);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Bundle extras = intent.getExtras();
        if (extras != null) {
            if (extras.containsKey(PARAM_KILL)) {
                finish();
            } else {
                displayTestName(extras);
            }
        }
    }

    private void stopTest() {
        sendBroadcast(new Intent(ACTION));
        finish();
    }

    private void displayTestName(Bundle extras) {
        String testName = extras.getString(PARAM_TEST_NAME);
        if (testName == null)
            testName = "";
        setTitle(testName);
    }
}
