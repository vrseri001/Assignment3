package versfeld.erin.assignment3.helper;

import java.util.Arrays;
import java.util.List;

/**
 * This class stores a series of constant static values which will be used across the application
 * @author Erin Versfeld VRSERI001
 * @date 7 September 2014
 */
public class StaticConstants {
	
	//number of columns in which the images are displayed
	public static final int NUMBER_OF_COLUMNS = 3;
	
	//padding between images in the GridView
	public static final int IMAGE_PADDING = 8;
	
	//the image directory on the SD card
	public static final String PHOTO_ALBUM = "random";
	
	//supported file formats
	public static final List<String> FILE_EXTENSIONS = Arrays.asList("jpg", "jpeg", "png");
}
