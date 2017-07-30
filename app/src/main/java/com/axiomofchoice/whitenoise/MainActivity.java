package com.axiomofchoice.whitenoise;

import android.graphics.Point;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends ActionBarActivity {
    protected GameSurfaceView gameView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        gameView = new GameSurfaceView(this);
        setContentView(gameView);
    }

    // Resume game when app lifecycle resumes
    @Override
    protected void onResume() {
        super.onResume();
        gameView.resume();
    }

    // Pause game when app lifecycle pauses - i.e game won't run if not currently active
    @Override
    protected void onPause() {
        super.onPause();
        gameView.pause();
    }

}
