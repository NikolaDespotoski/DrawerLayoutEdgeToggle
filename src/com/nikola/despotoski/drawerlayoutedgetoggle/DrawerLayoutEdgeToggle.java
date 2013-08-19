
package com.nikola.despotoski.drawerlayoutedgetoggle;

import android.app.Activity;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
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
				if(!mDrawerLayout.isDrawerOpen(Gravity.START))
					mDrawerLayout.openDrawer(Gravity.START);
				else
					mDrawerLayout.closeDrawer(Gravity.START);
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
		mHandle = new ImageView(a);
		mHandle.setOnClickListener(mHandleClickListenerInt);	
		mHandle.setOnTouchListener(mHandleTouchListenerInt);
		mHandle.setSaveEnabled(true);
		rootLayout.addView(mHandle, new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, gravity));
		mKeepShadowOnHandle = keepShadowOnHandle;
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
		mHandle.setTranslationX(slideOffset*mMinusShadow);
		mHandle.setX(mHandle.getTranslationX());
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
		return mDrawerLayout.isDrawerOpen(Gravity.START);
	}
	public boolean onOptionsItemSelected(MenuItem item) {
		if(isOpen()){
			mDrawerLayout.closeDrawer(Gravity.START);
		}
		return false;
	}
	public void onConfigurationChanged(Configuration newConfig) {
		
	}
	
	
	
}