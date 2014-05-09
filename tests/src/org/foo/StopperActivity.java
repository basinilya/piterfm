package org.foo;

import ru.piter.fm.test.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class StopperActivity extends Activity {

	public static final String ACTION = StopperActivity.class.getName() + ".ACTION";
	public static final String PARAM_TEST_NAME = "testName";
	public static final String PARAM_KILL = "kill";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_stopper);

		findViewById(R.id.stop_button).setOnClickListener(
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
