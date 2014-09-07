package versfeld.erin.assignment3;

import versfeld.erin.assignment3.adapter.FullScreenImageAdapter;
import versfeld.erin.assignment3.helper.Utils;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;

public class FullScreenViewActivity extends Activity{

	private Utils utils;
	private FullScreenImageAdapter adapter;
	private ViewPager viewPager;
	
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_fullscreen_view);
		
		viewPager = (ViewPager) findViewById(R.id.pager);
		
		utils = new Utils(getApplicationContext());
		
		Intent i = getIntent();
		int position = i.getIntExtra("position", 0);
		
		adapter = new FullScreenImageAdapter(FullScreenViewActivity.this, utils.getFilePaths());
		
		viewPager.setAdapter(adapter);
		
		//display the selected image first
		viewPager.setCurrentItem(position);
	}
}
