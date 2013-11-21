package com.squirrelbox.ioio.activities;

import ioio.lib.api.DigitalOutput;
import ioio.lib.api.exception.ConnectionLostException;
import ioio.lib.util.BaseIOIOLooper;
import ioio.lib.util.IOIOLooper;
import ioio.lib.util.android.IOIOActivity;

import com.squirrelbox.ioio.R;
import com.squirrelbox.ioio.R.id;
import com.squirrelbox.ioio.R.layout;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;

/**
 * This is the main activity of the HelloIOIO example application.
 * 
 * It displays a toggle button on the screen, which enables control of the
 * on-board LED. This example shows a very simple usage of the IOIO, by using
 * the {@link IOIOActivity} class. For a more advanced use case, see the
 * HelloIOIOPower example.
 */
public class OpenLockActivity extends IOIOActivity {
	private int timeout = 5000;
	private volatile boolean timeup = false;
	private TextView message;

	/**
	 * Called when the activity is first created. Here we normally initialize
	 * our GUI.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.open_lock);
		message = (TextView)findViewById(R.id.countdown_status);
		
		count_down();
	}

	
	private void count_down(){
		long start = System.currentTimeMillis();
		long elapsed = 0;
		while (elapsed < timeout) {
			message.setText("Box will lock in "+(5-elapsed/1000)+" seconds");
			//Log.i("handler", "set text complete, elapsed is "+elapsed);
			elapsed = System.currentTimeMillis()-start;
		}
		
		timeup = true;
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
		}
		/// launch home screen
		Log.i("OpenLock", "launching main activity");
		Intent intent = new Intent(OpenLockActivity.this, BoxMainActivity.class);
		startActivity(intent);
	}
	
	/**
	 * This is the thread on which all the IOIO activity happens. It will be run
	 * every time the application is resumed and aborted when it is paused. The
	 * method setup() will be called right after a connection with the IOIO has
	 * been established (which might happen several times!). Then, loop() will
	 * be called repetitively until the IOIO gets disconnected.
	 */
	class Looper extends BaseIOIOLooper {
		/** The on-board LED. */
		private DigitalOutput led;
		private DigitalOutput lock;

		/**
		 * Called every time a connection with IOIO has been established.
		 * Typically used to open pins.
		 * 
		 * @throws ConnectionLostException
		 *             When IOIO connection is lost.
		 * 
		 * @see ioio.lib.util.AbstractIOIOActivity.IOIOThread#setup()
		 */
		@Override
		protected void setup() throws ConnectionLostException {
			Log.i("HelloIOIO looper", "setup");
			led = ioio_.openDigitalOutput(0, true);
			lock = ioio_.openDigitalOutput(7, DigitalOutput.Spec.Mode.OPEN_DRAIN, false);
		}

		/**
		 * Called repetitively while the IOIO is connected.
		 * 
		 * @throws ConnectionLostException
		 *             When IOIO connection is lost.
		 * 
		 * @see ioio.lib.util.AbstractIOIOActivity.IOIOThread#loop()
		 */
		@Override
		public void loop() throws ConnectionLostException {
			led.write(!timeup);
			lock.write(!timeup);			
			
			try {
			Thread.sleep(100);
			} catch (InterruptedException e) {
			}
		}
	}

	/**
	 * A method to create our IOIO thread.
	 * 
	 * @see ioio.lib.util.AbstractIOIOActivity#createIOIOThread()
	 */
	@Override
	protected IOIOLooper createIOIOLooper() {
		return new Looper();
	}
}