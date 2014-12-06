package com.roger.mathbee;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;

public class MainActivity extends Activity {

	// Directions for slideButtons
	public static final int LEFT = 0;
	public static final int RIGHT = 1;
	
	private ViewGroup container;
	private Button gPlay;
	private Animations animations;
	private Typeface comic_sans_bold;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		// Prevents screen from dimming throughout
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		// Get instance of Animations for custom MathBee animations
		animations = Animations.getInstance();
		comic_sans_bold = Typeface.createFromAsset(getAssets(), "fonts/comicbd.ttf");
		
		container = (ViewGroup) findViewById(R.id.main_activity);
		gPlay = (Button) findViewById(R.id.start_button);
		gPlay.setTypeface(comic_sans_bold);
		
		// Set a click listener to the 'Play!' button
		// runs the buttons' slide left animation and starts Runnable that
		// launches MenuActivity, and reverts 'Play' button to old position, but invisible
		gPlay.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {

				animations.slideButton(container, RIGHT, new Runnable() {

					@Override
					public void run() {
						Intent intent = new Intent(MainActivity.this, MenuActivity.class);
						overridePendingTransition(0,0);
						gPlay.setAlpha(0f);
						gPlay.setTranslationX(container.getWidth());
						startActivity(intent);
					}
					
				}, gPlay);
			}
		});
		animations.popButtonIn(1000, gPlay);
	}

	@Override
	protected void onResume() {
		super.onResume();
	}
	@Override
	protected void onPause() {
		super.onPause();
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
