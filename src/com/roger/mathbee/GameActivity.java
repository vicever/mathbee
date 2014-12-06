package com.roger.mathbee;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class GameActivity extends Activity {

	public static final String DIFFICULTY = "DIFFICULTY";
	public static final String CORRECT = "CORRECT";
	public static final String WRONG = "WRONG";
	public static final String SKIPPED = "SKIPPED";
	public static final String DURATION = "DURATION";
	public static final String ORDER = "ORDER";
	
	private int difficulty = 0;
	private int count = 0;
	private int correct = 0;
	private int wrong = 0;
	private int[] ops = null;
	private ArrayList<Operator> operators;
	private int answer = 0;
	private int skipped = 0;
	private int total_count = 10;
	private long game_start;
	
	private Typeface comic_sans_bold;
	private Bundle extra;
	private ViewGroup row1, row2;
	private Animations animations;
	private ViewGroup gAnswers;
	private TextView gQuestion;
	private ImageView gSuccess, gFailure;
	private Button gMenu, gSkip;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_game);
		overridePendingTransition(0,0);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		animations = Animations.getInstance();
		comic_sans_bold = Typeface.createFromAsset(getAssets(), "fonts/comicbd.ttf");
		extra = getIntent().getExtras();
		init();
		game_start = System.nanoTime();
	}
	
	// Provides access to views, sets up and animates in 'Play!' button
	private void init() {
		// Saves difficulty info passed from Intent from MainActivity
		difficulty = extra.getInt(MenuActivity.BTNDIFF);
		total_count = extra.getInt(MenuActivity.BTNSET);
		ops = extra.getIntArray(MenuActivity.BTNOP);
		operators = new ArrayList<Operator> ();
		for (int i = 0; i < ops.length; i++) {
			if (ops[i] == 1)
				operators.add(Operator.getOperator(i));
		}
		row1 = (ViewGroup) findViewById(R.id.row1);
		row2 = (ViewGroup) findViewById(R.id.row2);
		gAnswers = (ViewGroup) findViewById(R.id.answers);
		gSuccess = (ImageView) findViewById(R.id.flash_success);
		gFailure = (ImageView) findViewById(R.id.flash_failure);
		gQuestion = (TextView) findViewById(R.id.slate_question);
		gMenu = (Button) findViewById(R.id.game_menu_back);
		gMenu.setTypeface(comic_sans_bold);
		gSkip  = (Button) findViewById(R.id.game_skip);
		gSkip.setTypeface(comic_sans_bold);
		gQuestion.setTypeface(comic_sans_bold);

		// Set click Listeners to all views
		gMenu.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(GameActivity.this, MenuActivity.class);
				startActivity(intent);
			}
		});
		gSkip.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				answer = displayOneIO();
                skipped ++;
			}
		});
		answer = displayOneIO();
		for (int i = 0; i < gAnswers.getChildCount(); i++) {
			Button v = (Button) gAnswers.getChildAt(i);
			v.setTypeface(comic_sans_bold);
			v.setOnClickListener(new AnswerClickListener(v));
		}
		animations.popButtonIn(1000, row1);
		animations.popButtonIn(1000, row2);
	}

	private int displayOneIO() {
		int input1 = randomInput(difficulty);
		int input2 = randomInput(difficulty);
		Operator sign = randomOperator(operators);
		int output = sign.calculate(input1, input2);
		if(output < 0)
		{
            Log.e("zgs","get output <0, redesign it!");
            return displayOneIO();
		}
        //get four answers with a correct answer and three wrong.
		int[] answers = randomMultipleChoice(output);
		String text =	"\n" + 
						input1 + "\t\t\t\t\n" + 
						sign.symbol() + "\t" + input2 + "\t\t\t\t\n" +
						"_______\t\t\t\t";
		gQuestion.setText(text);
		for (int i = 0; i < answers.length; i++)
			((Button) gAnswers.getChildAt(i)).setText("" + answers[i]);
		return output;
	}
	
	// Generates 4 semi-random values as multiple choice options
	// One random one out of the 4 is the correct answer
	private int[] randomMultipleChoice(int answer) {
		int[] answers = new int[4];
		int deviation = 5;
		if (answer > 5) {
			deviation = answer/2;
		}
		int i = 0;
		while(i < answers.length) {
			double plus_minus = Math.random();
			int alternative;
			if (plus_minus < 0.5)	
				alternative = (int)(answer + Math.random() * deviation);
			else
				alternative = (int)(answer - Math.random() * deviation);
			boolean add = true;
			if (alternative != answer) {
				for (int a: answers)
					if (a == alternative)
						add = false;
				if (add&&(alternative >= 0)) {
					answers[i++] = alternative;

				}
			}

				 
		}
		switch ((int)(Math.random()*12%4)) {
		case 0: answers[0] = answer; break;
		case 1: answers[1] = answer; break;
		case 2: answers[2] = answer; break;
		case 3: answers[3] = answer; break;
		}
		
		return answers;
	}
	// Returns a random input that depends on the difficulty settiing
	private int randomInput(int difficulty) {
		switch (difficulty) {
		case 0:	return (int)(Math.random()*10+1);
		case 1:	return (int)(Math.random()*100+1);
		case 2:	return (int)(Math.random()*1000+1);
		}
		return 0;
	}
	// Returns a random Operator enum instance
	private Operator randomOperator(ArrayList<Operator> operators) {		
		int result = (int) (Math.random()*120%operators.size());
		switch (result) {
		case 0:	return operators.get(0);
		case 1:	return operators.get(1);
		case 2:	return operators.get(2);
		case 3:	return operators.get(3);
		}
		return null;
	}

	/* Listener methods/classes */
	
	// Listener for each answer bubble
	// If picks the correct one, generates a new problem
	// If picks wrong one, or reaches total # of problems, quits to display game summary
	private class AnswerClickListener implements View.OnClickListener {

		private Button view;
		
		AnswerClickListener(Button view) {
			this.view = view;
		}
		
		@Override
		public void onClick(View v) {
			if (answer == Integer.parseInt((String) view.getText())) {
				animations.popInOut(gSuccess);
				correct++;
			} else {
				wrong++;
				animations.popInOut(gFailure);
			}
			if (++count < total_count) {
				answer = displayOneIO();
			} else {
				int sec_duration = (int) ((System.nanoTime()-game_start)/1e9);
				Intent summary = new Intent(GameActivity.this, SummaryActivity.class);
				String[] order = {DIFFICULTY, CORRECT, WRONG, SKIPPED, DURATION};
				summary
					.putExtra(DIFFICULTY, difficulty)
					.putExtra(CORRECT, correct)
					.putExtra(WRONG, wrong)
					.putExtra(SKIPPED, skipped)
					.putExtra(DURATION, sec_duration)
					.putExtra(ORDER, order);
				startActivity(summary);
			}
		}
	};
	
	@Override
	public void onBackPressed() {}
	
	@Override
	public void onRestart() {
		super.onRestart();
		// If resuming from game window, redirects to Menu
		Intent intent = new Intent(GameActivity.this, MenuActivity.class);
		startActivity(intent);
	}
	
}
