package versfeld.erin.assignment3.helper;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.ImageView;

/**
 * This class allows for the 'pinch-to-zoom' functionality desired of many image viewing applications. 
 * The algorithm for it's implementation was inspired by a number of different blog threads on the topic
 * @author Erin Versfeld VRSERI001
 * @date 7 September 2014
 */
public class Touch extends ImageView {

	Matrix matrix;
	
	//There are in essence three states when using this kind of function: nothing is happening (NONE),
	//the user is dragging the image (DRAG), or the user is trying to zoom in on the image (ZOOM)
	static final int NONE = 0;
	static final int DRAG = 1;
	static final int ZOOM = 2;
	//the default mode is that nothing is happening on the screen
	int mode = NONE;
	
	//zooming constants
	PointF last = new PointF();
	PointF start = new PointF();
	float minScale = 1f;
	float maxScale = 3f;
	//represents the function of transformation which the image will undergo
	float[] m;
	
	//variables to track the size to which the user has zoomed, and to which the image must be restored
	//once the user moves to the next image
	int viewWidth;
	int viewHeight;
	static final int CLICK = 3;
	float saveScale = 1f;
	protected float originalWidth;
	protected float originalHeight;
	int oldMeasuredWidth;
	int oldMeasuredHeight;
	
	ScaleGestureDetector mScaleDetector;
	
	Context context;
	
	public Touch(Context context_){
		super(context_);
		sharedConstructing(context_);
	}
	
	public Touch(Context context_, AttributeSet attrs) {
        super(context_, attrs);
        sharedConstructing(context_);
    }
	
	@SuppressLint("ClickableViewAccessibility")
	private void sharedConstructing(Context context){
		
		super.setClickable(true);
		this.context = context;
		//ScaleListener is an inner class of Touch
		//A ScaleGestureDetector detects scaling transformation gestures using the supplied motion event
		mScaleDetector = new ScaleGestureDetector(context, new ScaleListener());
		matrix = new Matrix();
		m = new float[9];
		setImageMatrix(matrix);
		setScaleType(ScaleType.MATRIX);
		
		setOnTouchListener(new OnTouchListener(){
			
			//@Override
			@SuppressLint("ClickableViewAccessibility")
			public boolean onTouch(View v, MotionEvent event){
				mScaleDetector.onTouchEvent(event);
				PointF current = new PointF(event.getX(), event.getY());
				
				switch(event.getAction()){
				case MotionEvent.ACTION_DOWN:
					//animate the image being dragged across the screen
					last.set(current);
					start.set(last);
					mode = DRAG;
					break;
				
				case MotionEvent.ACTION_MOVE:
					//down and drag
					if(mode==DRAG){
						//measure change in X and Y coordinates
						float deltaX = current.x - last.x;
						float deltaY = current.y - last.y;
						//calculate the transformed position of the image
						float fixTransformationX = getFixedDragTransformation(deltaX, viewWidth, originalWidth*saveScale);
						float fixTransformationY = getFixedDragTransformation(deltaY, viewHeight, originalHeight*saveScale);
						//concatenates the transformation onto the end of the matrix
						matrix.postTranslate(fixTransformationX, fixTransformationY);
						fixedTransformation();
						last.set(current.x, current.y);
					}
					break;
				
				case MotionEvent.ACTION_UP:
					mode = NONE;
					int xDifference = (int) Math.abs(current.x-start.y);
					int yDifference = (int) Math.abs(current.y-start.y);
					
					if(xDifference<CLICK && yDifference<CLICK){
						//Call the onClickListener for this event
						performClick();
					}
					
					break;
				
				case MotionEvent.ACTION_POINTER_UP:
					mode = NONE;
					break;
				}
			//sets a matrix as the drawable graphic as the content of this view
			setImageMatrix(matrix);
			//forces the view to redraw itself
			invalidate();
			//indicate event was handled
			return true;
			}
		});
	}
	
	private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener{
		/**
		 * Responds to the beginning of a scaling gesture. Reported by new pointers going down.
		 *  @param detector The detector responding to this event is used to retrieve information about the event
		 *  @return true if the detector should continue to recognise this gesture
		 */
		@Override
		public boolean onScaleBegin(ScaleGestureDetector detector){
			mode = ZOOM;
			return true;
		}
		/**
		 * Responds to scaling events for a touch gesture in progress
		 * @return true if the detector should consider the event handled. The detector continues to accumulate movements 
		 *  until the event is handled.
		 */
		@Override
		public boolean onScale(ScaleGestureDetector detector){
			
			float mScaleFactor = detector.getScaleFactor();
			float originalScale = saveScale;
			saveScale*=mScaleFactor;
			
			//make sure that the scale which one is adjusting the image size by
			//does not get bigger than the maximum scale, and no smaller than the
			//minimum scale
			if(saveScale>maxScale){
				saveScale = maxScale;
				mScaleFactor = maxScale/originalScale;
			}
			
			else if(saveScale<minScale){
				saveScale = minScale;
				mScaleFactor = minScale/originalScale;
			}
			
			if(originalWidth*saveScale<=viewWidth || originalHeight*saveScale<=viewHeight){
				matrix.postScale(mScaleFactor, mScaleFactor, viewWidth/2, viewHeight/2);
			}
			else{
				matrix.postScale(mScaleFactor, mScaleFactor, detector.getFocusX(), detector.getFocusY());
			}
			
			fixedTransformation();
			return true;
		}
	}
	
	/**
	 * Setter method to redefine the maximum zoom level
	 * @param x The factor by which one desires the image to be able to be zoomed in by
	 */
	public void setMaxZoom(float x) {
	        maxScale = x;
	
	}
	
	/**
	 * Transforms the image and stores the new values in the matrix
	 */
	private void fixedTransformation(){
		//copy last nine values from the matrix into m
		matrix.getValues(m);
		float transformedX = m[Matrix.MTRANS_X];
		float transformedY = m[Matrix.MTRANS_Y];
		
		float fixedTransformationOfX = getFixedTransformation(transformedX, viewWidth, originalWidth*saveScale);
		float fixedTransformationOfY = getFixedTransformation(transformedY, viewHeight, originalWidth*saveScale);
		
		if(fixedTransformationOfX!=0 || fixedTransformationOfY!=0){
			matrix.postTranslate(fixedTransformationOfX, fixedTransformationOfY);
		}
	}
	
	/**
	 * Calculates the transformation of the image
	 * @param transformation the new coordinate value
	 * @param viewSize the relevant view dimension
	 * @param contentSize the relevant dimension of the translated image
	 * @return the size of the transformation applied to the specific dimension.
	 */
	private float getFixedTransformation(float transformation, float viewSize, float contentSize){
		
		float minTransformation;
		float maxTransformation;
		
		//if the image is smaller than or equal in size to the screen
		if(contentSize<=viewSize){
			minTransformation = 0;
			maxTransformation = viewSize - contentSize;
		}
		
		else{
			minTransformation = viewSize - contentSize;
			maxTransformation = 0;
		}
		
		if(transformation<minTransformation){
			return minTransformation-transformation;
		}
		
		else if(transformation>maxTransformation){
			return maxTransformation-transformation;
		}
		
		return 0;
	}
	
	/**
	 * Calculates the transformation and movement of an image
	 * @param delta the change in position
	 * @param viewSize the relevant view dimension
	 * @param contentSize the relevant image dimension
	 * @return the change applied to the image dimension
	 */
	private float getFixedDragTransformation(float delta, float viewSize, float contentSize){
		if(contentSize<=viewSize){
			return 0;
		}
		return delta;
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec){
		
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		viewWidth = MeasureSpec.getSize(widthMeasureSpec);
		viewHeight = MeasureSpec.getSize(heightMeasureSpec);
		
		//rescale an image when rotated
		if((oldMeasuredHeight==viewWidth && oldMeasuredHeight==viewHeight) ||
				(viewWidth==0) || (viewHeight==0)){
			return;
		}
		oldMeasuredHeight = viewHeight;
		oldMeasuredWidth = viewWidth;
		
		if(saveScale == 1){
			
			//fit the image to the screen
			float scale;
			
			Drawable drawable = getDrawable();
			if(drawable==null || drawable.getIntrinsicWidth()==0 || drawable.getIntrinsicHeight()==0){
				return;
			}
			int bmWidth = drawable.getIntrinsicWidth();
			int bmHeight = drawable.getIntrinsicHeight();
			
			Log.d("bmSize", "bmWidth: " + bmWidth + " bmHeight : " + bmHeight);
			
			float scaleX = (float) viewWidth/(float) bmWidth;
			float scaleY = (float) viewHeight/(float) bmHeight;
			scale = Math.min(scaleX, scaleY);
			matrix.setScale(scale, scale);
			
			//centre the image on the screen
			float redundantYSpace = (float) viewHeight
					- (scale * (float) bmHeight);
			float redundantXSpace = (float) viewWidth
					- (scale * (float) bmWidth);
			redundantYSpace /= (float) 2;
			redundantXSpace /= (float) 2;

			matrix.postTranslate(redundantXSpace, redundantYSpace);
			
			originalWidth = viewWidth - 2 * redundantXSpace;
			originalHeight = viewHeight - 2 * redundantYSpace;
			setImageMatrix(matrix);
		}
		fixedTransformation();
	}
}
