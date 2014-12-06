package com.roger.mathbee;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.TimeInterpolator;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnticipateOvershootInterpolator;
import android.view.animation.OvershootInterpolator;

public class Animations {
	
	// Directions for slideButton
	public static final int LEFT = 0;
	public static final int RIGHT = 1;
	
	private static Animations animations;
	
	public static Animations getInstance() {
		if (animations == null)
			animations = new Animations();
		return animations;
	}
	protected Animations() {
		
	}
	
	/* Animation methods/classes */
	
	// Scale buttons in (with overshoot)
	public void popButtonIn(int delay, View view) {
		TimeInterpolator overshoot = new OvershootInterpolator();
		PropertyValuesHolder pvhBtnX = PropertyValuesHolder.ofFloat(View.SCALE_X, 1);
		PropertyValuesHolder pvhBtnY = PropertyValuesHolder.ofFloat(View.SCALE_Y, 1);
		view.setScaleX(0);
		view.setScaleY(0);
		ObjectAnimator anim = ObjectAnimator.ofPropertyValuesHolder(view, pvhBtnX, pvhBtnY);
		anim.setStartDelay(delay);
		anim.setDuration(300);
		anim.setInterpolator(overshoot);
		anim.start();
	}
	
	// Slide buttons out of window, either left or right
	public void slideButton(ViewGroup container, int direction, final Runnable endAction, View view) {
		TimeInterpolator antiOvershoot = new AnticipateOvershootInterpolator();
		int dist;
		if (direction == LEFT)
			dist = -container.getWidth();
		else
			dist = container.getWidth();
		PropertyValuesHolder pvhTranslationX = PropertyValuesHolder.ofFloat(
				View.TRANSLATION_X, dist);
		PropertyValuesHolder pvhShrinkX = PropertyValuesHolder.ofFloat(View.SCALE_X, .7f);
		PropertyValuesHolder pvhShrinkY = PropertyValuesHolder.ofFloat(View.SCALE_Y, .7f);
		ObjectAnimator anim = ObjectAnimator.ofPropertyValuesHolder(
				view, pvhTranslationX, pvhShrinkX, pvhShrinkY);
		anim.setDuration(500);
		anim.setInterpolator(antiOvershoot);
		if (endAction != null) {
			anim.addListener(new AnimatorListenerAdapter(){
				
				@Override
				public void onAnimationEnd(Animator animation) {
                    endAction.run();
                }
			});
		}
		anim.start();
	}
	// Flash view in, then out quickly
	public void popInOut(View view) {
		// Assume view is already scaled to (0,0)
		view.animate()
			.alpha(1)
			.setDuration(150).setListener(new popInOutAnimatorEndAction(view))
			.start();
		// Remove listener to prevent infinite loop of onAnimationEnd() calls
		view.animate().setListener(null);
	}
	// Implements the 'out' part of popInOut
	// Starts the flashing out after flashing in is complete and is displayed
	// For a period of time.
	class popInOutAnimatorEndAction implements Animator.AnimatorListener {
		View view;
		popInOutAnimatorEndAction(View v) {
			this.view = v;		
		}
		@Override
		public void onAnimationEnd(Animator animation) {
            view.animate()
            .setStartDelay(400)
            .alpha(0)
            .setDuration(150)
            .start();
        }
		@Override
		public void onAnimationCancel(Animator animation) {}
		@Override
		public void onAnimationRepeat(Animator animation) {}
		@Override
		public void onAnimationStart(Animator animation) {}
	}
	

	public void slideButtons(ViewGroup container, int direction, final Runnable endAction, View... views) {
		TimeInterpolator antiOvershoot = new AnticipateOvershootInterpolator();
		ObjectAnimator[] animate = new ObjectAnimator[views.length];
		for (int i = 0; i < views.length; i++) {
			View v = views[i];
			int dist;
			if (direction == LEFT)
				dist = -container.getWidth();
			else
				dist = container.getWidth();
			PropertyValuesHolder pvhTranslationX = PropertyValuesHolder.ofFloat(
					View.TRANSLATION_X, dist);
			PropertyValuesHolder pvhShrinkX = PropertyValuesHolder.ofFloat(View.SCALE_X, .7f);
			PropertyValuesHolder pvhShrinkY = PropertyValuesHolder.ofFloat(View.SCALE_Y, .7f);
			ObjectAnimator anim = ObjectAnimator.ofPropertyValuesHolder(
					v, pvhTranslationX, pvhShrinkX, pvhShrinkY);
			anim.setDuration(500);
			anim.setInterpolator(antiOvershoot);
			animate[i] = anim;
		}
		AnimatorSet set = new AnimatorSet();
		set.playTogether(animate);
		if (endAction != null) {
			set.addListener(new AnimatorListenerAdapter(){
				
				@Override
				public void onAnimationEnd(Animator animation) {
                    endAction.run();
                }
			});
		}
		set.start();
	}
	
	public void popButtonsIn(ViewGroup parent) {
		int count = parent.getChildCount();
		TimeInterpolator overshoot = new OvershootInterpolator();
		ObjectAnimator[] animate = new ObjectAnimator[count];
		PropertyValuesHolder pvhBtnX = PropertyValuesHolder.ofFloat(View.SCALE_X, 1);
		PropertyValuesHolder pvhBtnY = PropertyValuesHolder.ofFloat(View.SCALE_Y, 1);
		for (int i = 0; i < count; i++) {
			View btn = parent.getChildAt(i);
			btn.setScaleX(0);
			btn.setScaleY(0);
			ObjectAnimator anim = ObjectAnimator.ofPropertyValuesHolder(btn, pvhBtnX, pvhBtnY);
			if (i == 0)
				anim.setStartDelay(1500);
			anim.setDuration(300);
			anim.setInterpolator(overshoot);
			animate[i] = anim;
		}
		AnimatorSet set = new AnimatorSet();
		set.playSequentially(animate);
		set.start();
	}
	
}
