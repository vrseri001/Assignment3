package versfeld.erin.assignment3.adapter;

import versfeld.erin.assignment3.R;
import versfeld.erin.assignment3.helper.Touch;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

@SuppressWarnings("unused")
public class FullScreenImageAdapter extends PagerAdapter{
	
	private Activity activity;
	private ArrayList<String> imagePaths;
	private LayoutInflater inflater;
	
	/**
	 * Constructor
	 * @param activity_ The activity to be displayed by this class
	 * @param imagePaths_ The images to be displayed in this view
	 */
	public FullScreenImageAdapter(Activity activity_, ArrayList<String> imagePaths_){
		this.activity = activity_;
		this.imagePaths = imagePaths_;
	}

	/**
	 * 
	 */
	@Override
	public int getCount() {
		return this.imagePaths.size();
	}

	/**
	 * 
	 */
	@Override
	public boolean isViewFromObject(View view, Object object) {
		return view == ((RelativeLayout)object);
	}
	/**
	 * 
	 */
	@Override
	public Object instantiateItem(ViewGroup container, int position){
		Touch imgDisplay;
		Button closeButton;
		
		inflater = (LayoutInflater) this.activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View viewLayout = inflater.inflate(R.layout.layout_fullscreen_image, container,
                false);
		
		imgDisplay = (Touch) viewLayout.findViewById(R.id.imgDisplay);
		closeButton = (Button)viewLayout.findViewById(R.id.btnClose);
		
		//scales the size of an image efficiently
		//used to pass options such as scale up or scale down to the bitmapfactory
		BitmapFactory.Options options = new BitmapFactory.Options();
		//stores each pixel on 4 bytes of memory
		options.inPreferredConfig = Bitmap.Config.ARGB_8888;
		//Decode the file path into a bitmap
		Bitmap bitmap = BitmapFactory.decodeFile(imagePaths.get(position), options);
		//sets the drawable content of the current view
		imgDisplay.setImageBitmap(bitmap);
		
		//close button click event
		closeButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				activity.finish();
			}
		});
		
		((ViewPager)container).addView(viewLayout);
		
		return viewLayout;
	}
	/**
	 * 
	 */
	@Override
	public void destroyItem(ViewGroup container, int position, Object object){
		((ViewPager)container).removeView((RelativeLayout)object);
	}
}
