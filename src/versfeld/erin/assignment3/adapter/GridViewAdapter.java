package versfeld.erin.assignment3.adapter;

import versfeld.erin.assignment3.FullScreenViewActivity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

public class GridViewAdapter extends BaseAdapter {

	private Activity activity;
	private ArrayList<String> filePaths = new ArrayList<String>();
	private int imgWidth;
	
	public GridViewAdapter(Activity activity_, ArrayList<String> filePaths_, int imgWidth_){
		
		this.activity = activity_;
		this.filePaths = filePaths_;
		this.imgWidth = imgWidth_;
	
	}

	@Override
	public int getCount() {
		return this.filePaths.size();
	}

	@Override
	public Object getItem(int position) {
		return this.filePaths.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ImageView imageView;
		
		if(convertView==null){
			imageView = new ImageView(this.activity);
		}
		else{
			imageView = (ImageView) convertView;
		}
		
		//get screen dimensions
		Bitmap image = decodeFile(filePaths.get(position), imgWidth, imgWidth);
		imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
		imageView.setLayoutParams(new GridView.LayoutParams(imgWidth, imgWidth));
		imageView.setImageBitmap(image);
		
		//add a click listener to the image
		imageView.setOnClickListener(new OnImageClickListener(position));
		
		return imageView;
	}
	
	class OnImageClickListener implements OnClickListener{
		
		int position;
		
		public OnImageClickListener(int position_){
			this.position = position_;
		}
		
		@Override
		public void onClick(View v){
			//launch full screen activity when clicked
			Intent i = new Intent(activity, FullScreenViewActivity.class);
			i.putExtra("position", this.position);
			activity.startActivity(i);
		}
	}
	
	/**
	 * Resize the image
	 */
	public static Bitmap decodeFile(String filePath, int width, int height){
		try{
			File f = new File(filePath);
			
			BitmapFactory.Options o = new BitmapFactory.Options();
			o.inJustDecodeBounds = true;
			BitmapFactory.decodeStream(new FileInputStream(f), null, o);
			

			final int REQUIRED_WIDTH = width;
			final int REQUIRED_HEIGHT = height;
			int scale = 1;
			while (o.outWidth / scale / 2 >= REQUIRED_WIDTH
					&& o.outHeight / scale / 2 >= REQUIRED_HEIGHT)
				scale *= 2;

			BitmapFactory.Options o2 = new BitmapFactory.Options();
			o2.inSampleSize = scale;
			return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}
}
