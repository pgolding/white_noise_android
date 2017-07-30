package com.axiomofchoice.whitenoise;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class GameSurfaceView extends SurfaceView implements Runnable {
    private boolean isRunning = false;
    private Thread gameThread;
    private SurfaceHolder holder;
    private long fps;
    private float step;
    private int screenWidth;
    private int screenHeight;
    int xPos = 0;
    int yPos = 0;
    int rVal = 0;
    int gVal = 0;
    int bVal = 0;

    private Bitmap mlk;
    private Wave testWave = new Wave(150,150,0,0,0,0,0,0,0,0,0);

    Paint p = null;
    public Canvas cv;

    @Override
    public boolean onTouchEvent(MotionEvent ev){
        switch (ev.getAction()){
            case MotionEvent.ACTION_MOVE :
                xPos = (int) ev.getX();
                yPos = (int) ev.getY();
                int pixel = mlk.getPixel(xPos,yPos);

                //then do what you want with the pixel data, e.g
                rVal = Color.red(pixel);
                bVal = Color.blue(pixel);
                gVal = Color.green(pixel);
                break;
        }

        return true;
    }

    private final static int MAX_FPS = 40; //desired fps
    private final static int FRAME_PERIOD = 1000 / MAX_FPS; // the frame period


    public GameSurfaceView(Context context) {
        super(context);

        mlk = BitmapFactory.decodeResource(this.getResources(), R.drawable.mlk);



        holder = getHolder();
        holder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                screenWidth = width;
                screenHeight = height;
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
            }

        });

    }

    /**
     * Start or resume the game.
     */
    public void resume() {
        isRunning = true;
        gameThread = new Thread(this);
        gameThread.start();
    }

    /**
     * Pause the game loop
     */
    public void pause() {
        isRunning = false;
        boolean retry = true;
        while (retry) {
            try {
                gameThread.join();
                retry = false;
            } catch (InterruptedException e) {
                // try again shutting down the thread
            }
        }
    }


    class Wave {
        int x; // position
        int y; // position
        // just for testing
        int width;
        int height;
        float r; // red
        float g; // green
        float b; // blue
        float freq; // "frequency" of sinewave
        float amplitude; // "amplitude" of sinewave
        float step; // kind of like the "sampling" frequency
        int shift = 1; // phase shift
        float rotate = 0; // x-axis rotation
        double period = 2*Math.PI; // periodicity - really a constant, but can be altered
        private Path mpath = null;
        Paint q = null;

        // Constructor
        Wave(int _x, int _y, float _r, float _g, float _b, float _f, float _a, float _s, int _shift, float _rotate, float _period) {
            x = _x; y = _y; r = _r; g = _g; b = _b;
            freq = _f; amplitude = _a; step = _s;
            shift = _shift; rotate = _rotate; period = _period;
            // Set some defaults for testing...
            shift = 1;
            freq = 5;
            amplitude = 20;
            period = 2*Math.PI;
            step = 2;
            q = new Paint();
            q.setColor(Color.rgb(rVal,  gVal, bVal));
            q.setStrokeWidth(2);
            q.setStyle(Paint.Style.STROKE);
        }

        void display(Canvas canvas) {
            int width = 300;
            int height = 50;
            mpath = new Path();
            float x_s = (float) (x - shift*(2*Math.PI/0.2)*step);
            mpath.moveTo(x_s, y);
            // Sine wave generator
            float x_rel = 0;
            for (float a = 0; a < period; a += 0.1) {
                mpath.lineTo(x_s+x_rel,  (float) (y + amplitude*Math.sin(freq*a) ) );
                x_rel+=step;
            }
            canvas.drawPath(mpath, q);
        }
    }

    protected void step() {

    }

    protected void render(Canvas canvas) {

        // Set background color
        //canvas.drawColor(Color.BLACK);
        // Add the MLK image
        canvas.drawBitmap(mlk, 0,0,p);
        // Set up some paint colors/style
        p = new Paint();
        p.setColor(Color.rgb(rVal,  gVal, bVal));
        p.setTextSize(25);
        // Add the frame per second and touch co-ords labels
        canvas.drawText("FPS:" + fps, 20, 20, p);
        canvas.drawText("Touch:" + xPos + ":" + yPos, 20, 50, p);
        //step = (float) (Math.random() - 1.5*Math.random());
        //w.step = step;
        testWave.q.setColor(Color.rgb(rVal,  gVal, bVal));
        testWave.display(canvas);
    }

    @Override
    public void run() {

        while(isRunning) {
            // We need to make sure that the surface is ready
            if (! holder.getSurface().isValid()) {
                continue;
            }
            long started = System.currentTimeMillis();

            // update
            step();
            // draw
            //Canvas cv = holder.lockCanvas();
            cv = holder.lockCanvas();
            if (cv != null) {
                render(cv);
                holder.unlockCanvasAndPost(cv);
            }

            float deltaTime = (System.currentTimeMillis() - started);
            long timeThisFrame = System.currentTimeMillis() - started;
            if (timeThisFrame > 0) {
                fps = 1000 / timeThisFrame;
            }

            int sleepTime = (int) (FRAME_PERIOD - deltaTime);
            if (sleepTime > 0) {
                try {
                    gameThread.sleep(sleepTime);
                }
                catch (InterruptedException e) {
                }
            }
            while (sleepTime < 0) {
                step();
                sleepTime += FRAME_PERIOD;
            }
        }
    }
}
