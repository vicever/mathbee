package com.roger.mathbee;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

public class MenuActivity extends Activity {
	
	public static final String BTNDIFF = "BTNDIFFICULTY";
	public static final String BTNSET = "BTNSET";
	public static final String BTNOP = "BTNOPERATIONS";
	
	private Typeface comic_sans_bold;
	private Animations animations;
	private ViewGroup btnDifficulty, btnSet, btnOperations;
	private ViewGroup row1, row2;
	private TextView mDifficulty, mSet, mOperations;
	private Button gPlay;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_menu);
		overridePendingTransition(0,0);
		animations = Animations.getInstance();
		comic_sans_bold = Typeface.createFromAsset(getAssets(), "fonts/comicbd.ttf");
		init();
	}
	
	private void init() {
		// Retrieve UI Views/ViewGroups
		row1 = (ViewGroup) findViewById(R.id.row1);
		row2 = (ViewGroup) findViewById(R.id.row2);
		btnDifficulty = (ViewGroup) findViewById(R.id.difficultyButtons);
		btnSet = (ViewGroup) findViewById(R.id.setButtons);
		btnOperations = (ViewGroup) findViewById(R.id.operationButtons);
		gPlay = (Button) findViewById(R.id.start_button);
		gPlay.setTypeface(comic_sans_bold);
		mDifficulty = (TextView) findViewById(R.id.difficulty);
		// Set all label text to comic-sans bold
		mDifficulty.setTypeface(comic_sans_bold);
		mSet = (TextView) findViewById(R.id.set);
		mSet.setTypeface(comic_sans_bold);
		mOperations = (TextView) findViewById(R.id.operations);
		mOperations.setTypeface(comic_sans_bold);
		// Set listeners and text to all buttons
		gPlay.setOnClickListener(validateSettingsListener);
		for (int i = 1; i < btnDifficulty.getChildCount(); i++) {
			ToggleButton toggle = (ToggleButton) btnDifficulty.getChildAt(i);
			toggle.setTypeface(comic_sans_bold);
			toggle.setOnCheckedChangeListener(oneChangeListener);
		}
		for (int i = 1; i < btnSet.getChildCount(); i++) {
			ToggleButton toggle = (ToggleButton) btnSet.getChildAt(i);
			toggle.setTypeface(comic_sans_bold);
			toggle.setOnCheckedChangeListener(oneChangeListener);
		}
		/*for (int i = 0; i < btnOperations.getChildCount(); i++) {
			ToggleButton toggle = (ToggleButton) btnOperations.getChildAt(i);
			toggle.setTypeface(comic_sans_bold);
		}*/
		// Pop all views into window
		animations.popButtonIn(1500, row1);
		animations.popButtonIn(1500, row2);
	}
	
	// If each set of buttons have at least one checked, then 'Play' button 
	// sends intent to GameActivity. Otherwise, Toasts a message
	private View.OnClickListener validateSettingsListener = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			boolean diff_ = false;
			boolean set_ = false;
			boolean op_ = false;
			int diff_checked = 0;
			int set_checked = 0;
			int[] op_checked = new int[btnOperations.getChildCount()];
			for (int i = 1; i < btnDifficulty.getChildCount(); i++)
				if (((CompoundButton) btnDifficulty.getChildAt(i)).isChecked()) {
					diff_checked = i-1;
					diff_ = true;
					break;
				}
			for (int i = 1; i < btnSet.getChildCount(); i++)
				if (((CompoundButton) btnSet.getChildAt(i)).isChecked()) {
					set_checked = i-1;
					set_ = true;
					break;
				}
			for (int i = 0; i < btnOperations.getChildCount(); i++) {
				if (((CompoundButton) btnOperations.getChildAt(i)).isChecked()) {
					op_checked[i] = 1;
					op_ = true;
				}
			}
			switch(set_checked) {
			case 0: 
				set_checked = 10;
				break;
			case 1:
				set_checked = 20;
				break;
			case 2:
				set_checked = 40;
				break;
			default: 
				set_checked = 10;
				break;
			}
			if (diff_ && set_ && op_) {
				Intent intent = new Intent(MenuActivity.this, GameActivity.class);
				intent
					.putExtra(BTNDIFF, diff_checked)
					.putExtra(BTNSET, set_checked)
					.putExtra(BTNOP, op_checked);
				startActivity(intent);
			} else {

				 Toast.makeText(MenuActivity.this, "请选择难度&&设置&&运算！！ "
				 		, Toast.LENGTH_LONG).show();
			}	
		}
	};
	
	// Makes sure only one (or 0) buttons are pressed for each set of buttons
	private OnCheckedChangeListener oneChangeListener = new OnCheckedChangeListener() {

		@Override
		public void onCheckedChanged(CompoundButton btn, boolean boo) {
			ViewGroup parent = (ViewGroup) btn.getParent();
			for (int i = 1; i < parent.getChildCount(); i++) {
				CompoundButton btnChild = (CompoundButton) parent.getChildAt(i);
				if (!btnChild.getContentDescription().equals(btn.getContentDescription()))
					btnChild.setChecked(false);
			}
			btn.setChecked(boo);
		}
	};
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu, menu);
		return true;
	}
	// Disable back pressing to MainActivity
	@Override
	public void onBackPressed()	{}
}
