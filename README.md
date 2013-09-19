DrawerLayoutEdgeToggle
======================

#Description:

DrawerLayoutEdgeToggle is library that adds visible handle to the DrawerLayout. It allows you to slide the handle to open/close the DrawerLayout, or use click event to open/close the DrawerLayout. It is independent of the ActionBar home indicator as it is seen in ActionBarDrawerToggle.

#Usage:
1. Import the library project and set it to your project.

<pre><code>boolean keepShadowOnHandle = true;
int drawerGravity = GravityCompat.START; // or GravityCompat.END
mDrawerToggle = new DrawerLayoutEdgeToggle(this, mDrawerLayout, R.drawable.drawer_open, R.drawable.drawer_close, keepShadowOnHandle, drawerGravity){

                @Override
                public void onDrawerClosed(View arg0) {
                    super.onDrawerClosed(arg0); //must call super
                }

                @Override
                public void onDrawerOpened(View arg0) {
                    super.onDrawerOpened(arg0); //must call super
                }

                @Override
                public void onDrawerSlide(View arg0, float slideOffset) {
                    super.onDrawerSlide(arg0, slideOffset); //must call super

                }};
    mDrawerLayout.setDrawerListener(mDrawerToggle);.
</code></pre>
