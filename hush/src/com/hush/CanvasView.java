package com.hush;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint.Style;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class CanvasView extends View{
	
	public final boolean MODE_DRAWING = true;
	public final boolean MODE_NOT_DRAWING = false;
	
	private boolean isTouchable = MODE_NOT_DRAWING;
	ShapeDrawable mCircleDrawable;
	
	public CanvasView(Context context, AttributeSet attrs, int defStyle) {
	    super(context, attrs, defStyle);
	    mCircleDrawable = new ShapeDrawable(new OvalShape());
	    mCircleDrawable.getPaint().setColor(getResources().getColor(R.color.action_bar));
	    mCircleDrawable.getPaint().setStyle(Style.STROKE);
	    mCircleDrawable.getPaint().setStrokeWidth(10);
	}

	public CanvasView(Context context, AttributeSet attrs) {
	    this(context, attrs, 0);
	}

	public CanvasView(Context context) {
	    this(context, null, 0);
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		mCircleDrawable.draw(canvas);
	}
	
	public boolean getTouchability(){
		return isTouchable;
	}
	
	public void setTouchability(boolean isTouchable){
		this.isTouchable = isTouchable;
	}
	
	public void clear(){
		mCircleDrawable.setBounds(0, 0, 0, 0);
		invalidate();
	}
		@Override
	public boolean onTouchEvent(MotionEvent event) {

		    switch (event.getActionMasked()) {
		    case MotionEvent.ACTION_DOWN:
		       clear();
		        break;   
		    case MotionEvent.ACTION_POINTER_DOWN:
		        break;
		    case MotionEvent.ACTION_MOVE:
		    	if(isTouchable)
		            prepareCircleDrawing(event);
		        break;
		    case MotionEvent.ACTION_POINTER_UP:
		        break;
		    case MotionEvent.ACTION_UP:
		        break;
		    default:
		        super.onTouchEvent(event);
		    }
		    return isTouchable;
	}

	private void prepareCircleDrawing(MotionEvent event) {

	    if (event.getActionIndex() > 1 || event.getPointerCount() < 2) {
	        return;
	    }
	    
	    isTouchable = true;
	    int top, right, bottom, left;
	    
	    if (event.getX(0) < event.getX(1)) {
	        left = (int) event.getX(0);
	        right = (int) event.getX(1);
	    } else {
	        left = (int) event.getX(1);
	        right = (int) event.getX(0);
	    }

	    if (event.getY(0) < event.getY(1)) {
	        top = (int) event.getY(0);
	        bottom = (int) event.getY(1);
	    } else {
	        top = (int) event.getY(1);
	        bottom = (int) event.getY(0);
	    }
	    
		int height = bottom - top;
		int width = right - left;

		if (height > width) {
		    int delta = height - width;
		    top += delta / 2;
		    bottom -= delta / 2;
		} else {
		    int delta = width - height;
		    left += delta / 2;
		    right -= delta / 2;
		}  		
	    mCircleDrawable.setBounds(left, top, right, bottom);
	    invalidate();
	}
}
