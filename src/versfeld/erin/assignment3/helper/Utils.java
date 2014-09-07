package versfeld.erin.assignment3.helper;

import java.io.File;
import java.util.ArrayList;
import java.util.Locale;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Point;
import android.os.Build;
import android.view.Display;
import android.view.WindowManager;
import android.widget.Toast;

/**
 * This class contains a series of functions which will be reused across activities and classes
 * @author Erin Versfeld VRSERI001
 * @date 7 September 2014
 */
//allows for the accessing of API 13 methods without logging system errors
@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
public class Utils {
	
	private Context context;
	
	/**
	 * Constructor method
	 * @param context_ The context of the calling class
	 */
	public Utils(Context context_){
		this.context = context_;
	}
	
	/**
	 * Reads in the file paths for all the images in the specified directory on the SD card
	 * @return Returns an ArrayList of Strings representing the file paths
	 */
	public ArrayList<String> getFilePaths(){
		
		ArrayList<String> filePaths = new ArrayList<String>();
		
		//gets the top level external storage directory for the images, followed by the specific separator used by the OS
		//and ending with the location of the photos
		File directory = new File(android.os.Environment.getExternalStorageDirectory()
				+File.separator+AppConstant.PHOTO_ALBUM);
		
		if(directory.isDirectory()){
			
			//get the file paths for the images in the directory
			File[] listFiles = directory.listFiles();
			
			//check that the directory is not empty
			if(listFiles.length>0){
				for(int i = 0; i<listFiles.length;i++){
					
					//get the file path
					//returns the absolute path of the current item
					String filePath = listFiles[i].getAbsolutePath();
					
					//check that the file type is supported
					if(isSupported(filePath)){
						
						//add the image to the list of files
						filePaths.add(filePath);
					}
				}
			}
			
			else{
				//Toast class provides feedback in a simple pop-up
				Toast.makeText(this.context, AppConstant.PHOTO_ALBUM+
						" is empty.Please load some images into it.", Toast.LENGTH_LONG).show();
			}
		}
		
		else{
			//displays an alert message
			AlertDialog.Builder alert = new AlertDialog.Builder(this.context);
			alert.setTitle("Error!");
			alert.setMessage(AppConstant.PHOTO_ALBUM +
					" directory path is not valid! Please set the image directory name in the AppConstant.java class");
			alert.setPositiveButton("OK", null);
			alert.show();
		}
		
		return filePaths;
	}

	/**
	 * Checks whether or not a file is of a format which the application supports
	 * @param filePath The absolute file path to the file in question
	 * @return true if the format is supported, false if it is not
	 */
	public boolean isSupported(String filePath){
		
		//stores the file extension which is supplied after the last occurance of . in the
		//file path
		String extension = filePath.substring((filePath.lastIndexOf(".")+1));
		
		//Locale represents a variant combination. This allows for the app to check the file extension
		//of alternative scripts, such as Arabic, accurately
		//This step also involved converting the extension to all lower case letters
		if(AppConstant.FILE_EXTENSIONS.contains(extension.toLowerCase(Locale.getDefault()))){
			return true;
		}
		
		else{
			return false;
		}
	}

	/**
	 * Detects the screen width for the Android device on which the application is currently being run
	 * @return returns an integer value representing the width of the device's screen
	 */
	@SuppressWarnings("deprecation")
	public int getScreenWidth(){
		
		int width;
		
		//retrieves the window manager for accessing the system's window manager
		WindowManager wm = (WindowManager) this.context.getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();
		
		final Point point = new Point();
		
		try{
			display.getSize(point);
		}
		//an error will be thrown if the device is too old for the current API
		//these methods have been deprecated, but may be used in this context
		catch(java.lang.NoSuchMethodError error){
			point.x = display.getWidth();
			point.y = display.getHeight();
		}
		
		width = point.x;
		return width;
		
	}
}
