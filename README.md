Floating-Circles
=================

floating circles with spring like touch effects  

Just copy MagicCircles.java and OnMagicCircleClick.java 

usage: 
        Display Floating Circles:-
         
		mMagicCircle=new MagicCircles(MainActivity.this,0,150,R.drawable.sample,true);
        mMagicCircle.setCancelableOnTouchOutside(false);
        mMagicCircle.addSpinAnimationView(MainActivity.this);
        mMagicCircle.show(200);
          
        Destory Magic Circle:-
        
        mMagicCircle.destroy();  

dependencies: gradle 

    dependencies 
    {
    compile 'com.facebook.rebound:rebound:0.3.3'
    }       

See Circle Example For More Details 
   

