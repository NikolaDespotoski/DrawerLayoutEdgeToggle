
package com.nikola.despotoski.drawerlayoutedgetoggle;

import android.app.Activity;
import android.content.res.Configuration;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.ViewDragHelper;
import android.support.v7.app.ActionBarActivity;
import android.view.Display;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;

import com.nineoldandroids.view.ViewHelper;
import com.nineoldandroids.view.ViewPropertyAnimator;
/* Author: Nikola Despotoski
 * Email: nikola[dot]despotoski(at)gmail[dot]com
 * 
 *   Copyright (c) 2012 Nikola Despotoski

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */
public class DrawerLayoutEdgeToggle implements DrawerLayout.DrawerListener{

	private Activity mActivity;
	private DrawerLayout mDrawerLayout;
	private ImageView mHandle;
	private float mMinusShadow;
    private Drawable mOpenDrawable;
    private Drawable mCloseDrawable;
	private boolean mKeepShadowOnHandle;
	private int MAX_ALPHA = 255;
	private float MAX_VIEW_ALPHA = 1.2f;
	private OnClickListener mHandleClickListener;
	private OnClickListener mHandleClickListenerInt = new OnClickListener(){

		@Override
		public void onClick(View v) {
			if(mDefaultOk){
				if(!mDrawerLayout.isDrawerOpen(mGravity))
					mDrawerLayout.openDrawer(mGravity);
				else
					mDrawerLayout.closeDrawer(mGravity);
			}
			if(mHandleClickListener != null)
				mHandleClickListener.onClick(v);
		}
		
	};
	private OnTouchListener mHandleTouchListenerInt = new OnTouchListener(){

		private float mInitialX = 0.0f;

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			  if(event.getAction() == MotionEvent.ACTION_DOWN){
				  mInitialX = event.getX();
			  }else if(event.getAction() == MotionEvent.ACTION_UP){
				  mInitialX = 0.0f;
			  }
				MotionEvent copy = MotionEvent.obtain(event);
				copy.setEdgeFlags(ViewDragHelper.EDGE_ALL);
				copy.setLocation(event.getRawX() - mInitialX, event.getY());
				//Log.i("copy.getRawX()", ""+copy.getRawX());
				mDrawerLayout.onTouchEvent(copy);
				return mHandleTouchListener != null?  mHandleTouchListener.onTouch(v, event) :false;
		}};
	
	private boolean mDefaultOk = true;
	private int mCloseTitleRes = 0x0;
	private int mOpenTitleRes =  0x0;
	private OnTouchListener mHandleTouchListener;
	protected int mGravity;
	
	private View.OnAttachStateChangeListener mOnAttachListener = new View.OnAttachStateChangeListener() {
		
		@Override
		public void onViewDetachedFromWindow(View v) {
		   
		}
		
		@Override
		public void onViewAttachedToWindow(View v) {
			if(v == mDrawerLayout){
				   mDrawerLayout.measure(MeasureSpec.EXACTLY, MeasureSpec.EXACTLY);
				   syncState();
			 }
		}
	};
	private FrameLayout rootLayout;
	private Drawable mCurrentDrawable;
	private int mScreenWidth;
	
 public DrawerLayoutEdgeToggle(Activity a, DrawerLayout l, int drawerOpen, int drawerClose, int handleGravity, boolean keepShadowOnHandle, int drawerGravity){
		if(handleGravity == Gravity.CENTER || handleGravity == Gravity.CENTER_HORIZONTAL || handleGravity == Gravity.CENTER_HORIZONTAL ){
			throw new IllegalArgumentException("Don't use any CENTER gravity");
		}
		if(drawerGravity != GravityCompat.END && drawerGravity != GravityCompat.START && drawerGravity != Gravity.LEFT && drawerGravity != Gravity.RIGHT )
			throw new IllegalArgumentException("Use: GravityCompat.END, GravityCompat.START, Gravity.LEFT or Gravity.RIGHT for drawerGravity parameter");
		mGravity = drawerGravity;
		mActivity = a;
		mDrawerLayout = l;
		mOpenDrawable = a.getResources().getDrawable(drawerOpen);
	    mCloseDrawable = a.getResources().getDrawable(drawerClose);
	    rootLayout = (FrameLayout)mActivity.findViewById(android.R.id.content);
	    if(Build.VERSION.SDK_INT <= 12){
			mHandle = new ImageView(a){
	
				@Override
				protected void onAttachedToWindow() {
					mDrawerLayout.measure(MeasureSpec.EXACTLY, MeasureSpec.EXACTLY);
					syncState();
				}
				
			};
	    }else{
	    	mHandle = new ImageView(a);
	    	mDrawerLayout.measure(MeasureSpec.EXACTLY, MeasureSpec.EXACTLY);
	    	
	    }
		mHandle.setOnClickListener(mHandleClickListenerInt);	
		mHandle.setOnTouchListener(mHandleTouchListenerInt);
		mHandle.setSaveEnabled(true);
		rootLayout.addView(mHandle, new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, handleGravity));
		mKeepShadowOnHandle = keepShadowOnHandle;
		mCurrentDrawable = mCloseDrawable;
		mScreenWidth = getScreenWidth();
		if(Build.VERSION.SDK_INT >= 12){
			mDrawerLayout.addOnAttachStateChangeListener(mOnAttachListener);
			
		}
		
		
		
	
	}
 
	public void setOverrideDefaultHandleAction(boolean ok){
		mDefaultOk  = ok;
	}
	public View getDrawerLayoutHandle(){
		return mHandle;
	}
	public void setOnHandleClickListener(View.OnClickListener listener){
		 mHandleClickListener = listener;
	}
	public void setOnHandleTouchListener(View.OnTouchListener listener){
		mHandleTouchListener = listener;
	}
	
	@Override
	public void onDrawerClosed(View arg0) {
		mHandle.setImageDrawable(mCloseDrawable);
		updateActionBar();
		mCurrentDrawable = mCloseDrawable;
	}

	@Override
	public void onDrawerOpened(View arg0) {
		mHandle.setImageDrawable(mOpenDrawable);	
		updateActionBar();		
		mCurrentDrawable = mOpenDrawable;
	}
	public void setActionBarCloseTitle(int res){
		mCloseTitleRes = res;
	}
	public void setActionBarOpenTitle(int res){
		mOpenTitleRes = res;
	}

	@Override
	public void onDrawerSlide(View arg0, float slideOffset) {	
		getDrawerMinusShadow();
		  float translationX = checkForLeftDrawer(mGravity,slideOffset*mMinusShadow);
		 // Log.i("translationX ", " "+translationX);
		if(Build.VERSION.SDK_INT >= 11){
			mHandle.setTranslationX(translationX);
			mHandle.setX(mHandle.getTranslationX());
		}else{
			ViewPropertyAnimator.animate(mHandle).translationX(translationX).setDuration(0).start();
		}
		if(!mKeepShadowOnHandle)
			 noShadow();
		else
			keepShadow(slideOffset);
		
	}

	@Override
	public void onDrawerStateChanged(int arg0) {
	
	}
	private float checkForLeftDrawer(int gravity, float x){
		return gravity == GravityCompat.END ||  gravity == Gravity.LEFT? mScreenWidth-x-mHandle.getDrawable().getIntrinsicWidth() : x;
	}

	private void getDrawerMinusShadow(){
		if(mMinusShadow == 0.0f){
			for(int i = 0 ; i < mDrawerLayout.getChildCount();i++){
				mMinusShadow = mMinusShadow == 0.0f ? mDrawerLayout.getChildAt(i).getMeasuredWidth() : mMinusShadow;
				mMinusShadow = Math.min(mMinusShadow, mDrawerLayout.getChildAt(i).getMeasuredWidth());	
		
			}			
		}
		return;
	}
	private void keepShadow(float offset) {
		if(Build.VERSION.SDK_INT >= 11)
			mHandle.setAlpha(MAX_VIEW_ALPHA - offset);
		else
			mHandle.getDrawable().setAlpha((int) (MAX_ALPHA*(1.0f - offset)));
	}

	public void syncState(){
			mCurrentDrawable = mDrawerLayout.isDrawerOpen(mGravity)? mOpenDrawable : mCloseDrawable;
			mScreenWidth = getScreenWidth();
			mHandle.setImageDrawable(mCurrentDrawable);
			updateActionBar();
			updateHandle();
			
	}
	private void noShadow(){
		mCloseDrawable.setAlpha(MAX_ALPHA);	
		mOpenDrawable.setAlpha(MAX_ALPHA);
	}
	private void updateHandle(){
		getDrawerMinusShadow();
		mScreenWidth = getScreenWidth();
		//Log.i("Minus Shadow: "+mHandle.getDrawable().getIntrinsicWidth()," Shadow: "+ checkForLeftDrawer(mGravity,mMinusShadow) + " Screen w: "+ mScreenWidth + " Difference: "+(mScreenWidth-mMinusShadow));
		if(isOpen()){
			//Log.i("isOpen()"+mGravity," X: "+checkForLeftDrawer(mGravity,mMinusShadow));
			if(Build.VERSION.SDK_INT >= 11)
				mHandle.setTranslationX(checkForLeftDrawer(mGravity,mMinusShadow));
			else
				mHandle.setImageDrawable(mCurrentDrawable);
				ViewHelper.setX(mHandle, checkForLeftDrawer(mGravity,mMinusShadow));
			
		}
	}
	@SuppressWarnings("deprecation")
	private int getScreenWidth(){
		
		  Point size = new Point();
		  WindowManager w = mActivity.getWindowManager();
		  if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2){
	          w.getDefaultDisplay().getSize(size);
	          return size.x; 
	        }else{
	          Display d = w.getDefaultDisplay(); 
	          return d.getWidth(); 
		 }
	}

	private void updateActionBar(){
		if(mOpenTitleRes != 0x0 && mCloseTitleRes != 0x0)
			if(mOpenTitleRes != 0x0 && mCloseTitleRes != 0x0)
			{
				String title = isOpen()? mActivity.getResources().getString(mOpenTitleRes) : mActivity.getResources().getString(mCloseTitleRes);
				if(Build.VERSION.SDK_INT >= 11)
					mActivity.getActionBar().setTitle(title);
				else if(mActivity instanceof ActionBarActivity){
					((ActionBarActivity)mActivity).getSupportActionBar().setTitle(title);
				}
			}
	}
	private boolean isOpen(){
		return mDrawerLayout.isDrawerOpen(mGravity);
	}
	public boolean onOptionsItemSelected(MenuItem item) {
		if(isOpen()){
			mDrawerLayout.closeDrawer(mGravity);
		}
		return false;
	}
//	public void setVerticalTopOffset(int topInPercentage){
//		if(topInPercentage > 100 || topInPercentage < 0){
//			throw new IllegalArgumentException("Invalid percentage.");
//		}
//	    int actionBarHeight = mActivity instanceof ActionBarActivity? ((ActionBarActivity)mActivity).getSupportActionBar().getHeight() : mActivity.getActionBar().getHeight();
//	    
//	}
	public void onConfigurationChanged(Configuration newConfig) {
		
	}

	
}