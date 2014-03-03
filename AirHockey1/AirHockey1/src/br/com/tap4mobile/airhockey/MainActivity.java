package br.com.tap4mobile.airhockey;

import br.com.tap4mobile.airhockey.renderer.AirHockeyRender;

import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.view.Menu;
import android.widget.Toast;

public class MainActivity extends Activity {

	private GLSurfaceView glSurfaceView;
	private boolean renderSet = false;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		glSurfaceView = new GLSurfaceView(this);

		final ActivityManager activityManger = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);

		final ConfigurationInfo configurationInfo = activityManger.getDeviceConfigurationInfo();

		final boolean supportEs2 = configurationInfo.reqGlEsVersion >= 0x20000;

		if (supportEs2) {

			glSurfaceView.setEGLContextClientVersion(2);

			glSurfaceView.setRenderer(new AirHockeyRender(this));

			renderSet = true;

		} else {
			Toast.makeText(this, "This device does not support OpenGL ES 2.0.", Toast.LENGTH_LONG).show();
		}

		setContentView(glSurfaceView);

	}

	@Override
	protected void onResume() {
		super.onResume();
		if (renderSet) {
			glSurfaceView.onResume();
		}
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();

		if (renderSet) {
			glSurfaceView.onPause();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
