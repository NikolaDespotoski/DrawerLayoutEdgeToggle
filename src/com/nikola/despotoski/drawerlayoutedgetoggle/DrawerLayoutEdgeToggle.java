
package com.example.android.navigationdrawerexample;

import android.app.Activity;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.ViewDragHelper;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
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
public class DrawerLayoutEdgeToggle implements DrawerLayout.DrawerListener, AnimationListener{

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
				if(!mDrawerLayout.isDrawerOpen(GravityCompat.START))
					mDrawerLayout.openDrawer(GravityCompat.START);
				else
					mDrawerLayout.closeDrawer(GravityCompat.START);
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
	
 public DrawerLayoutEdgeToggle(Activity a, DrawerLayout l, int drawerOpen, int drawerClose, int gravity, boolean keepShadowOnHandle){
		if(gravity == Gravity.CENTER || gravity == Gravity.CENTER_HORIZONTAL || gravity == Gravity.CENTER_HORIZONTAL ){
			throw new IllegalArgumentException("Don't use any CENTER gravity");
		}
		mActivity = a;
		mDrawerLayout = l;
		mOpenDrawable = a.getResources().getDrawable(drawerOpen);
	    mCloseDrawable = a.getResources().getDrawable(drawerClose);
	    FrameLayout rootLayout = (FrameLayout)mActivity.findViewById(android.R.id.content);
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
	    }
		mHandle.setOnClickListener(mHandleClickListenerInt);	
		mHandle.setOnTouchListener(mHandleTouchListenerInt);
		mHandle.setSaveEnabled(true);
		rootLayout.addView(mHandle, new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, gravity));
		mKeepShadowOnHandle = keepShadowOnHandle;
		if(Build.VERSION.SDK_INT >= 12)
			mDrawerLayout.addOnAttachStateChangeListener(mOnAttachListener);
		
	
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
		
	}

	@Override
	public void onDrawerOpened(View arg0) {
		mHandle.setImageDrawable(mOpenDrawable);	
		updateActionBar();		
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
		  float translationX = slideOffset*mMinusShadow;
		if(Build.VERSION.SDK_INT >= 11){
			mHandle.setTranslationX(translationX);
			mHandle.setX(mHandle.getTranslationX());
		}else{
			HandleTranslateAnimation translateAnimation = new HandleTranslateAnimation(0, translationX, 0,0);
			translateAnimation.setDuration(0);
			translateAnimation.setAnimationListener(this);
			mHandle.startAnimation(translateAnimation);
		}
		if(!mKeepShadowOnHandle)
			 noShadow();
		else
			keepShadow(slideOffset);
		
	}

	@Override
	public void onDrawerStateChanged(int arg0) {
	
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
		mHandle.setAlpha(MAX_VIEW_ALPHA - offset);
	}

	public void syncState(){
			mHandle.setImageDrawable(mDrawerLayout.isDrawerOpen(Gravity.START)? mOpenDrawable : mCloseDrawable);
			updateActionBar();
			updateHandle();
			
	}
	private void noShadow(){
		mCloseDrawable.setAlpha(MAX_ALPHA);	
		mOpenDrawable.setAlpha(MAX_ALPHA);
	}
	private void updateHandle(){
		getDrawerMinusShadow();
		Log.i("Minus Shadow: "," "+ mMinusShadow);
		if(isOpen()){
			mHandle.setX(mMinusShadow);
		}
	}
	
	private void updateActionBar(){
		if(mOpenTitleRes != 0x0 && mCloseTitleRes != 0x0)
			if(mOpenTitleRes != 0x0 && mCloseTitleRes != 0x0)
			    mActivity.getActionBar().setTitle(isOpen()? mActivity.getResources().getString(mOpenTitleRes) : mActivity.getResources().getString(mCloseTitleRes));
	}
	private boolean isOpen(){
		return mDrawerLayout.isDrawerOpen(GravityCompat.START);
	}
	public boolean onOptionsItemSelected(MenuItem item) {
		if(isOpen()){
			mDrawerLayout.closeDrawer(GravityCompat.START);
		}
		return false;
	}
	public void onConfigurationChanged(Configuration newConfig) {
		
	}
	@Override
	public void onAnimationEnd(Animation animation) {
		 HandleTranslateAnimation hand = (HandleTranslateAnimation)animation;
		 mHandle.setX(hand.getmToXDelta());
	}
	@Override
	public void onAnimationRepeat(Animation animation) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onAnimationStart(Animation animation) {
		// TODO Auto-generated method stub
		
	}
	
	private class HandleTranslateAnimation extends TranslateAnimation{

		private float mFromXDelta=0.0f;
		private float mToXDelta= 0.0f;

		public float getmFromXDelta() {
			return mFromXDelta;
		}

		public float getmToXDelta() {
			return mToXDelta;
		}

		public HandleTranslateAnimation(float fromXDelta, float toXDelta,
				float fromYDelta, float toYDelta) {
			super(fromXDelta, toXDelta, fromYDelta, toYDelta);
			this.mFromXDelta = fromXDelta; 
			this.mToXDelta  = toXDelta; 
		}
		
	}
	
}