package mcnulty.apps.cheeseplease;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

public class MainActivity extends Activity {

	private Uri fileUri = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Intent startIntent = this.getIntent();
		String action = startIntent.getAction();
		
		if(action.equals(Cheese.ACTION_FIRE_CAMERA)) {
			//OPTION 1 - FAILED
//			Window window = getWindow();
//			window.addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
			
			//OPTION 2 - WORKED
			KeyguardManager km = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE); 
			final KeyguardManager.KeyguardLock kl = km .newKeyguardLock("MyKeyguardLock"); 
			kl.disableKeyguard(); 

			PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE); 
			WakeLock wakeLock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK
			                                 | PowerManager.ACQUIRE_CAUSES_WAKEUP
			                                 | PowerManager.ON_AFTER_RELEASE, "MyWakeLock");
			wakeLock.acquire();
			
			
            
            //OPTION 4 - SUPOSED TO WORK
//            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON |
//            					WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
//            					WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
//            					WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
			

	        Vibrator vibrator = (Vibrator) this.getSystemService(VIBRATOR_SERVICE);
	        vibrator.vibrate(200);
			
			Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			fileUri = getOutputMediaFileUri();
			Log.w("CHEESE", "uri: " + fileUri.toString());
		    i.putExtra(MediaStore.EXTRA_OUTPUT, fileUri); // set the image file name
		    
			startActivityForResult(i, Cheese.REQUEST_CODE);
		}
		else {
			int duration = Toast.LENGTH_SHORT;

			Toast.makeText(this, this.getString(R.string.started), Toast.LENGTH_LONG).show();
			startCheeseService();
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    if (requestCode == Cheese.REQUEST_CODE) {
	        if (resultCode == RESULT_OK) {
	        	Intent mediaScanIntent = new Intent( Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
	        	 mediaScanIntent.setData(fileUri);
	        	 sendBroadcast(mediaScanIntent);
	        } else {
	            // Image capture failed, advise user
	        }

    	startCheeseService();
	    }
	}
	
	private void startCheeseService () {
		Intent i = new Intent (this, MainService.class);
		Log.w ("CHEESE", "Starting Service...");
		this.startService(i);
		Log.w ("CHEESE", "Exiting Activity...");
		this.finish();
	}
	
	/** Create a file Uri for saving an image */
	private static Uri getOutputMediaFileUri(){
	      return Uri.fromFile(getOutputMediaFile());
	}

	/** Create a File for saving an image or video */
	private static File getOutputMediaFile(){
	    // To be safe, you should check that the SDCard is mounted
	    // using Environment.getExternalStorageState() before doing this.

	    File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
	              Environment.DIRECTORY_PICTURES), "Cheese Please");
	    // This location works best if you want the created images to be shared
	    // between applications and persist after your app has been uninstalled.

	    // Create the storage directory if it does not exist
	    if (! mediaStorageDir.exists()){
	        if (! mediaStorageDir.mkdirs()){
	            Log.d("CHEESE", "failed to create directory: " + mediaStorageDir);
	            return null;
	        }
	    }

	    // Create a media file name
	    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
	    File mediaFile = new File(mediaStorageDir.getPath() + File.separator +
	        "IMG_"+ timeStamp + ".jpg");

	    return mediaFile;
	}
	
}
