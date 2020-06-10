Floating-Circles
=================

floating circles with spring like touch effects  

Just copy MagicCircles.java and OnMagicCircleClick.java 

Add permission to AndroidManifest.xml

		<uses-permission android:name="android.permission.SYSTEM_ALERT_WINDOW" />

usage: 

        Display Floating Circles:-
	    mMagicCircle=new MagicCircles(MainActivity.this,0,150,R.drawable.sample,true);
        mMagicCircle.setCancelableOnTouchOutside(false);
        myMagicCircle.setOnMagicCircleClick(MainActivity.this);
        mMagicCircle.show(200);
        
        Add Spinner:-
        mMagicCircle.addSpinAnimationView(MainActivity.this);
        
        Remove Spinner:-
        loadMagicCircle.removeSpinAnimation();
                  
        Destory Magic Circle:-
        mMagicCircle.destroy();  

dependencies: gradle 

    dependencies 
    {
    compile 'com.facebook.rebound:rebound:0.3.3'
    }       


See Circle Example For More Details 
   

