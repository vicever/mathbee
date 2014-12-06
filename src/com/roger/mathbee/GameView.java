package com.roger.mathbee;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;

public class GameView extends SurfaceView implements Callback {
	private static final String TAG = MainThread.class.getSimpleName();

	private MainThread thread;

	public GameView(Context context, AttributeSet attrs, int defStyle) {
	    super(context, attrs, defStyle);
		init(context);
	}
	public GameView(Context context, AttributeSet attrs) {
	    super(context, attrs);
		init(context);
	}
	public GameView(Context context) {
		super(context);
		init(context);
	}
	
	private void init(Context context) {
		this.getHolder().addCallback(this);
		this.setFocusable(true);
		thread = new MainThread(getHolder(), this);
	}
	
	@Override
	public void onDraw(Canvas canvas) {
		canvas.drawColor(Color.CYAN);
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			if (event.getY() > this.getHeight() - 50) {
				thread.setRunning(false);
				((Activity)getContext()).finish();
			} else
				Log.d(TAG, "Coords: x=" + event.getX() + ",y=" + event.getY());
		}
		return super.onTouchEvent(event);
		
	}
	
	@Override
	public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void surfaceCreated(SurfaceHolder arg0) {
		thread.setRunning(true);
		thread.start();
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder arg0) {
		boolean retry = true;
		while (retry) {
			try {
				thread.join();
				retry = false;
			} catch (InterruptedException e) {}	
		}
			
		
	}

}

class MainThread extends Thread {
	
	private static final String TAG = MainThread.class.getSimpleName();

    private SurfaceHolder surfaceHolder;
    private GameView gameView;
    private boolean run = false;

    public MainThread(SurfaceHolder surfaceHolder, GameView gameView) {
        this.surfaceHolder = surfaceHolder;
        this.gameView = gameView;
    }

    public void setRunning(boolean run) { //Allow us to stop the thread
        this.run = run;
    }

    @Override
    public void run() {
        Canvas c;
        long tickCount = 0L;
        while (run) {     // When setRunning(false) occurs, run is 
            tickCount++;
        	c = null;      // set to false and loop ends, stopping thread
            try {
                c = surfaceHolder.lockCanvas();
                synchronized (surfaceHolder) {

                 gameView.onDraw(c);

                }
            } finally {
                if (c != null) {
                    surfaceHolder.unlockCanvasAndPost(c);
                }
            }
        }
        Log.d(TAG, "Game loop executed " + tickCount + " times");
    }
}