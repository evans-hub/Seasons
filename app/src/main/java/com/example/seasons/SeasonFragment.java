package com.example.seasons;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.constraintlayout.motion.widget.Key;
import androidx.fragment.app.Fragment;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class SeasonFragment extends Fragment {
    private int[] animationDurations = {15000, 15000, 15000, 15000};
    private int[] backgroundColors = {Color.parseColor("#FF4500"), Color.parseColor("#8FBC8F"), Color.parseColor("#FFFF00"), Color.parseColor("#FFFFFF")};
    private ImageView backgroundImageView;
    private ImageView birdsImageView;
    private ImageView cloudImageView;
    private int currentStep = 0;
    private TextView dateTimeTextView;
    private Handler handler = new Handler(Looper.getMainLooper());
    private boolean isAnimating = false;
    private MediaPlayer mediaPlayer;
    private int[] musicResources = {R.raw.spring_song, R.raw.summer_song, R.raw.autumn_song, R.raw.winter_song};
    private LinearLayout parentLayout;
    private Handler seasonHandler = new Handler(Looper.getMainLooper());
    private int[] seasonImages = {R.drawable.spring, R.drawable.summer, R.drawable.autumn, R.drawable.winter};
    private Runnable seasonRunnable = new Runnable() {
        public void run() {
            nextSeason();
            seasonHandler.postDelayed(this, 15000);
        }
    };
    private Button startButton;
    private Button stopButton;
    private ImageView sunImageView;
    private RelativeLayout topLayout;
    private Runnable updateDateTimeRunnable = new Runnable() {
        public void run() {
            updateDateTime();
            handler.postDelayed(this, 1000);
        }
    };
    private ImageView wheelImageView;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        startBackgroundChange();
        return inflater.inflate(R.layout.fragment_season, container, false);
    }

    private void startBackgroundChange() {
        handler.postDelayed(new Runnable() {
            public void run() {
                changeBackgroundWithAnimation();
                handler.postDelayed(this, 5000);
            }
        }, 5000);
    }

    private void changeBackgroundWithAnimation() {
        AlphaAnimation fadeOutAnimation = new AlphaAnimation(1.0f, 0.0f);
        fadeOutAnimation.setDuration(1000);
        fadeOutAnimation.setFillAfter(true);
        fadeOutAnimation.setAnimationListener(new Animation.AnimationListener() {
            public void onAnimationStart(Animation animation) {
            }

            public void onAnimationEnd(Animation animation) {
                topLayout.setBackgroundColor(getResources().getColor(17170443));
                AlphaAnimation fadeInAnimation = new AlphaAnimation(0.0f, 1.0f);
                fadeInAnimation.setDuration(1000);
                fadeInAnimation.setFillAfter(true);
                topLayout.startAnimation(fadeInAnimation);
            }

            public void onAnimationRepeat(Animation animation) {
            }
        });
        topLayout.startAnimation(fadeOutAnimation);
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        parentLayout = (LinearLayout) view.findViewById(R.id.wheelCoverLayout);
        topLayout = (RelativeLayout) view.findViewById(R.id.topLayout);
        backgroundImageView = (ImageView) view.findViewById(R.id.backgroundImageView);
        cloudImageView = (ImageView) view.findViewById(R.id.cloudImageView);
        sunImageView = (ImageView) view.findViewById(R.id.sunImageView);
        birdsImageView = (ImageView) view.findViewById(R.id.birdsImageView);
        wheelImageView = (ImageView) view.findViewById(R.id.wheelImageView);
        dateTimeTextView = (TextView) view.findViewById(R.id.dateTimeTextView);
        startButton = (Button) view.findViewById(R.id.startButton);
        stopButton = (Button) view.findViewById(R.id.stopButton);
        startButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                stopSeasonalChange();
                currentStep = 0;
                parentLayout.setBackgroundColor(Color.parseColor("#FF4500"));
                startSeasonalChange();
            }
        });
        stopButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                stopSeasonalChange();
                isAnimating = false;
            }
        });
        handler.post(updateDateTimeRunnable);
        seasonHandler.post(seasonRunnable);
        startSeasonalChange();
    }

    public void onDestroyView() {
        super.onDestroyView();
        handler.removeCallbacks(updateDateTimeRunnable);
        stopSeasonalChange();
        handler.removeCallbacksAndMessages(null);
    }

    private void startSeasonalChange() {
        isAnimating = true;
        currentStep = 0;
        startAnimationsAndMusic(0);
    }

    private void stopSeasonalChange() {
        isAnimating = false;
        MediaPlayer mediaPlayer2 = mediaPlayer;
        if (mediaPlayer2 != null) {
            mediaPlayer2.release();
            mediaPlayer = null;
        }
    }

    private void nextSeason() {
        int length = (currentStep + 1) % backgroundColors.length;
        currentStep = length;
        startAnimationsAndMusic(length);
    }

    private void startAnimationsAndMusic(int step) {
        animateCloud();
        animateSun();
        animateBirds();
        animateWheel();
        animateBackgroundColor(step);
        playMusic(getContext(), musicResources[step]);
    }

    private void playMusic(Context context, int resourceId) {
        stopSeasonalChange();
        MediaPlayer create = MediaPlayer.create(context, resourceId);
        mediaPlayer = create;
        create.setLooping(true);
        mediaPlayer.start();
    }

    private void animateBackgroundColor(int step) {
        int currentColor = ((ColorDrawable) parentLayout.getBackground()).getColor();
        int currentTopColor = ((ColorDrawable) topLayout.getBackground()).getColor();
        int nextColor = backgroundColors[step];
        ArgbEvaluator argbEvaluator = new ArgbEvaluator();
        Object[] objArr = new Object[2];
        objArr[0] = Integer.valueOf(currentTopColor);
        objArr[1] = Integer.valueOf(Color.parseColor(currentStep % 2 == 0 ? "#01AEEE" : "#87CEEB"));
        ValueAnimator topColorAnimation = ValueAnimator.ofObject(argbEvaluator, objArr);
        topColorAnimation.setDuration(15000);
        topColorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator animator) {
                topLayout.setBackgroundColor(((Integer) animator.getAnimatedValue()).intValue());
            }
        });
        topColorAnimation.start();
        ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), new Object[]{Integer.valueOf(currentColor), Integer.valueOf(nextColor)});
        colorAnimation.setDuration((long) animationDurations[step]);
        colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator animator) {
                parentLayout.setBackgroundColor(((Integer) animator.getAnimatedValue()).intValue());
            }
        });
        colorAnimation.start();
        backgroundImageView.setImageResource(seasonImages[step]);
    }

    private void animateCloud() {
        cloudImageView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            public void onGlobalLayout() {
                cloudImageView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                int parentWidth = topLayout.getWidth();
                ObjectAnimator cloudAnimator = ObjectAnimator.ofFloat(cloudImageView, "translationX", new float[]{(float) (-cloudImageView.getWidth()), (float) parentWidth});
                cloudAnimator.setDuration((long) ((animationDurations[currentStep] / 1) / 5));
                cloudAnimator.setInterpolator(new LinearInterpolator());
                cloudAnimator.setRepeatMode(2);
                cloudAnimator.setRepeatCount(-1);
                cloudAnimator.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        if (isAnimating) {
                            animateCloud();
                        }
                    }
                });
                cloudAnimator.start();
            }
        });
    }

    private void animateSun() {
        sunImageView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            public void onGlobalLayout() {
                sunImageView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                int parentWidth = topLayout.getWidth();
                ObjectAnimator sunAnimator = ObjectAnimator.ofFloat(sunImageView, "translationX", new float[]{(float) (-sunImageView.getWidth()), (float) parentWidth});
                sunAnimator.setDuration((long) ((animationDurations[currentStep] / 1) / 5));
                sunAnimator.setInterpolator(new LinearInterpolator());
                sunAnimator.setRepeatMode(2);
                sunAnimator.setRepeatCount(-1);
                sunAnimator.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        if (isAnimating) {
                            animateSun();
                        }
                    }
                });
                sunAnimator.start();
            }
        });
    }

    private void animateBirds() {
        birdsImageView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            public void onGlobalLayout() {
                birdsImageView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                int parentWidth = topLayout.getWidth();
                final float startX = (float) (-birdsImageView.getWidth());
                ObjectAnimator birdsAnimator = ObjectAnimator.ofFloat(birdsImageView, "translationX", new float[]{startX, (float) parentWidth});
                birdsAnimator.setDuration((long) ((animationDurations[currentStep] / 1) / 7));
                birdsAnimator.setInterpolator(new LinearInterpolator());
                birdsAnimator.setRepeatMode(1);
                birdsAnimator.setRepeatCount(-1);
                birdsAnimator.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        birdsImageView.setTranslationX(startX);
                        if (isAnimating) {
                            animateBirds();
                        }
                    }
                });
                birdsAnimator.start();
            }
        });
    }

    public void animateWheel() {
        ObjectAnimator wheelAnimator = ObjectAnimator.ofFloat(wheelImageView, Key.ROTATION, new float[]{0.0f, 360.0f});
        wheelAnimator.setDuration(3000);
        wheelAnimator.setInterpolator(new LinearInterpolator());
        wheelAnimator.setRepeatMode(1);
        wheelAnimator.setRepeatCount(-1);
        wheelAnimator.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                if (isAnimating) {
                    animateWheel();
                }
            }
        });
        wheelAnimator.start();
    }

    private void updateDateTime() {
        dateTimeTextView.setText(new SimpleDateFormat("dd MMM yyyy HH:mm:ss", Locale.getDefault()).format(new Date()));
    }
}
