package com.roger.mathbee;

import java.util.ArrayList;
import java.util.Collections;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class SummaryActivity extends Activity {

	public static final String CURRENT_USER = "CURRENT_USER";
	public static final String HIGH_SCORES = "HIGH_SCORES";
	public static final String NULL = "NULL";
	private int final_score;
	
	private SharedPreferences preferences;
	private ViewGroup sSummaries;
	private TextView sScore, sSummary;
	private Button sHighScores, sMenu;
	private Animations animations;
	private Typeface comic_sans_bold;
	private Bundle extra;
	private String highscores;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_summary);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		preferences = this.getSharedPreferences(CURRENT_USER, MODE_PRIVATE);
		animations = Animations.getInstance();
		comic_sans_bold = Typeface.createFromAsset(getAssets(), "fonts/comicbd.ttf");
		extra = getIntent().getExtras();
		init();
	}

	private void init() {
		// Access Views and setTypeFace
		sSummaries = (ViewGroup) findViewById(R.id.summaries);
		sSummary = (TextView) findViewById(R.id.summary);
		sSummary.setTypeface(comic_sans_bold);
		sScore = (TextView) findViewById(R.id.summary6);
		sScore.setTypeface(comic_sans_bold);
		sMenu = (Button) findViewById(R.id.btnMenu);
		sMenu.setTypeface(comic_sans_bold);
		sHighScores = (Button) findViewById(R.id.btnHighScores);
		sHighScores.setTypeface(comic_sans_bold);
		// Append results to all values from game
		String[] order = extra.getStringArray(GameActivity.ORDER);
		int diff_calc = 0;
		for (int i = 0; i < order.length; i++) {
			TextView tView = (TextView) sSummaries.getChildAt(i);
			tView.setTypeface(comic_sans_bold);
			if (i == 0) {
				switch (extra.getInt(order[0])) {
				case 0:
					diff_calc = 100;
					tView.append(getString(R.string.btnEasy));
					break;
				case 1:
					diff_calc = 150;
					tView.append(getString(R.string.btnMedium));
					break;
				case 2:
					diff_calc = 200;
					tView.append(getString(R.string.btnExpert));
					break;
				default: 
					tView.append("Unknown?");
					break;
				}
			} else {
				// For each summary, append values taken from last game
				tView.append("" + extra.getInt(order[i]));
			}
		}
		int correct_calc = extra.getInt(order[1]);
		int wrong_calc = extra.getInt(order[2]);
		int skipped_calc = extra.getInt(order[3]);
		int duration_calc = extra.getInt(order[4]);
		int total_count = correct_calc+wrong_calc;
		// Calculate final score.
		final_score = (int) ((diff_calc+total_count*2)*((double)correct_calc/(double)total_count)
				-5*skipped_calc-duration_calc/2);
		sScore.append("" + final_score + " 分");
		
		// Update High Scores
		SharedPreferences.Editor edit = preferences.edit();
		String s = preferences.getString(HIGH_SCORES, "");
		ArrayList<Integer> hsArray = toArrayFromString(s);
		replace5(hsArray, final_score);
		highscores = toStringFromArray(hsArray);
		edit.putString(HIGH_SCORES, highscores);
		edit.apply();
		
		// Set ClickListeners
		sMenu.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(SummaryActivity.this, MenuActivity.class);
				startActivity(intent);
			}
		});
		sHighScores.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Toast.makeText(SummaryActivity.this, highscores , Toast.LENGTH_LONG).show();
			}
		});
		
		// Pop in summaries
		animations.popButtonIn(1000, sSummaries);
	}
	
	private void replace5(ArrayList<Integer> integers, int replace) {
		integers.add(replace);
		Collections.sort(integers);
		Collections.reverse(integers);
		if (integers.size() > 5)
			integers.remove(integers.size()-1);
	}
	private ArrayList<Integer> toArrayFromString(String s) {
		ArrayList<Integer> integer = new ArrayList<Integer> ();
		String[] strings = s.split("\n");
		for (String str: strings) {
			str = str.replace(" ", "");
			Log.d("STRING1", str + "," + str.length());
			str = str.replace("Points", "");
			if (str.length() > 2) {
				str = str.substring(2);
				Log.d("STRING2", str + "," + str.length());
				integer.add(Integer.parseInt(str));
			}
		}
		return integer;
	}
	private String toStringFromArray(ArrayList<Integer> integers) {
		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < integers.size(); i++)
			buffer.append((i+1) + ": " + integers.get(i) + " Points\n");
		return buffer.toString();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.summary, menu);
		return true;
	}

}
