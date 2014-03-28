package com.example.app;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ToggleButton;

public class MainActivity extends Activity implements View.OnClickListener,OnMagicCircleClick{

    MagicCircles myAwesomeCircle;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button button =(Button)findViewById(R.id.toggleButton);
        button.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
       switch (view.getId())
       {
           case R.id.toggleButton:
                if(MagicCircles.nCircleDisplayed()==0)
                {
                myAwesomeCircle =new MagicCircles(this,0,150,R.drawable.sample,true);
                myAwesomeCircle.setOnMagicCircleClick(this);
                myAwesomeCircle.setCancelableOnTouchOutside(false);
                myAwesomeCircle.show(200);
                }
                break;
       }
    }

    @Override
    public void onMagicCircleClick(ViewGroup viewGroup, int type) {
           new Loader().execute();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(myAwesomeCircle!=null)
        myAwesomeCircle.destroy();
    }

    class Loader extends AsyncTask<Void,Void,Void>{
        MagicCircles loadMagicCircle;
        @Override
        protected void onPreExecute(){
          loadMagicCircle=new MagicCircles(MainActivity.this,0,150,R.drawable.sample,true);
          loadMagicCircle.setCancelableOnTouchOutside(false);
          loadMagicCircle.addSpinAnimationView(MainActivity.this);
          loadMagicCircle.show(200);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
          loadMagicCircle.removeSpinAnimation();
          loadMagicCircle.destroy();
        }
    }
}
