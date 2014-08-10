package mcnulty.apps.cheeseplease;

import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.provider.MediaStore;
import android.util.Log;

public class MainService extends Service implements SensorEventListener{

	private SensorManager manager = null;;
	private Sensor sensor = null;
	private boolean isInCamera = false;

	@Override
	public IBinder onBind(Intent intent) {
		// TODO: Return the communication channel to the service.
		throw new UnsupportedOperationException("Not yet implemented");
	}
	
   @Override
    public void onStart(Intent intent, int startid) {
		Log.w ("CHEESE", "Service Started...");
		isInCamera = false;
		
		if(manager == null && sensor == null) {
			Log.w ("CHEESE", "Starting Cheese Listener...");
			manager = (SensorManager) this.getBaseContext().getSystemService(SENSOR_SERVICE);
			sensor = manager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
			manager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
		}
	}
	
	@Override
	public void onAccuracyChanged(Sensor arg0, int arg1) {
	}

	@Override
	public void onSensorChanged(SensorEvent evt) {
		if (!isInCamera && Math.abs(evt.values[0]) >= Cheese.THRESHOLD) {
			isInCamera = true;
			Log.w("CHEESE", "-----------------------FIRE CAMERA-----------------------");
			
			Intent i = new Intent(this.getApplicationContext(), MainActivity.class);
			i.setAction(Cheese.ACTION_FIRE_CAMERA);
			i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
			startActivity(i);
		}
	}
}