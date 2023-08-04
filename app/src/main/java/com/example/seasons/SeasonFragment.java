package com.example.seasons;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SeasonFragment extends Fragment {

    private ImageView seasonalImageView,cloudImageView,birdsImageView,sunImageView,wheelImageView;
    /*private TextView dateTimeTextView;*/
    private Button startButton;
    private Button stopButton;
    private MediaPlayer mediaPlayer;
    private Handler handler = new Handler();
    private int currentStep = 0;
    private int[] backgroundColors = {
            Color.parseColor("#FF4500"), // OrangeRed
            Color.parseColor("#8FBC8F"), // DarkSeaGreen
            Color.parseColor("#FFFF00"), // Yellow
            Color.parseColor("#FFFFFF")  // White
    };
    private int[] imageResources = {
            R.drawable.spring,
            R.drawable.summer,
            R.drawable.autumn,
            R.drawable.winter
    };
    private int[] musicResources = {
            R.raw.spring_song,
            R.raw.summer_song,
            R.raw.autumn_song,
            R.raw.winter_song
    };

    private Runnable updateDateTimeRunnable = new Runnable() {
        @Override
        public void run() {
            updateDateTime();
            handler.postDelayed(this, 1000); // Update every second
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_season, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        seasonalImageView = view.findViewById(R.id.seasonalImageView);
        cloudImageView = view.findViewById(R.id.cloudImageView);
        birdsImageView = view.findViewById(R.id.birdsImageView);
        sunImageView = view.findViewById(R.id.sunImageView);
        wheelImageView = view.findViewById(R.id.wheelImageView);
        seasonalImageView = view.findViewById(R.id.seasonalImageView);
        /*dateTimeTextView = view.findViewById(R.id.dateTimeTextView);*/
        startButton = view.findViewById(R.id.startButton);
        stopButton = view.findViewById(R.id.stopButton);

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startSeasonalChange();
            }
        });

        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopSeasonalChange();
            }
        });

        handler.post(updateDateTimeRunnable);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        handler.removeCallbacks(updateDateTimeRunnable);
    }

    private void updateDateTime() {
        Date currentDate = new Date();
        DateFormat dateFormat = SimpleDateFormat.getDateTimeInstance();
        String formattedDate = dateFormat.format(currentDate);
        /*dateTimeTextView.setText(formattedDate);*/
    }
    private void startSeasonalChange() {
        if (currentStep >= backgroundColors.length) {
            currentStep = 0;
        }
        int nextStep = currentStep + 1;

        ValueAnimator colorAnimator = ValueAnimator.ofObject(
                new ArgbEvaluator(),
                backgroundColors[currentStep],
                backgroundColors[nextStep]
        );
        colorAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                seasonalImageView.setBackgroundColor((int) animator.getAnimatedValue());
            }
        });
        colorAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                // Start music playback
                playMusic(getContext(), musicResources[currentStep]);

                // Start animations for cloud, sun, birds, and wheel
                animateCloud();
                animateSun();
                animateBirds();
                animateWheel();

                // Switch to next step
                currentStep = nextStep;
            }
        });

        colorAnimator.setDuration(15000); // 15 seconds
        colorAnimator.start();
    }

    private void animateCloud() {
        // Calculate the width of the parent view (RelativeLayout)
        int parentWidth = ((View) cloudImageView.getParent()).getWidth();

        ObjectAnimator cloudAnimator = ObjectAnimator.ofFloat(
                cloudImageView, "translationX", -cloudImageView.getWidth(), parentWidth
        );
        cloudAnimator.setDuration(8000); // 8 seconds
        cloudAnimator.setRepeatMode(ValueAnimator.REVERSE);
        cloudAnimator.setRepeatCount(ValueAnimator.INFINITE);
        cloudAnimator.start();
    }

    private void animateSun() {
        ObjectAnimator sunAnimator = ObjectAnimator.ofFloat(
                sunImageView, "rotation", 0f, 360f
        );
        sunAnimator.setDuration(10000); // 10 seconds
        sunAnimator.setInterpolator(new LinearInterpolator()); // Linear rotation
        sunAnimator.setRepeatMode(ValueAnimator.RESTART);
        sunAnimator.setRepeatCount(ValueAnimator.INFINITE);
        sunAnimator.start();
    }

    private void animateBirds() {
        birdsImageView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                birdsImageView.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                // Calculate the width of the parent view (RelativeLayout)
                int parentWidth = ((View) birdsImageView.getParent()).getWidth();

                ObjectAnimator birdsAnimator = ObjectAnimator.ofFloat(
                        birdsImageView, "translationX", parentWidth, -birdsImageView.getWidth()
                );
                birdsAnimator.setDuration(5000); // 5 seconds
                birdsAnimator.setRepeatMode(ValueAnimator.REVERSE);
                birdsAnimator.setRepeatCount(ValueAnimator.INFINITE);
                birdsAnimator.start();
            }
        });
    }

    private void animateWheel() {
        ObjectAnimator wheelAnimator = ObjectAnimator.ofFloat(
                wheelImageView, "rotation", 0f, 360f
        );
        wheelAnimator.setDuration(3000); // 3 seconds
        wheelAnimator.setInterpolator(new LinearInterpolator()); // Linear rotation
        wheelAnimator.setRepeatMode(ValueAnimator.RESTART);
        wheelAnimator.setRepeatCount(ValueAnimator.INFINITE);
        wheelAnimator.start();
    }


    private void stopSeasonalChange() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    private void playMusic(Context context, int resourceId) {
        stopSeasonalChange();

        mediaPlayer = MediaPlayer.create(context, resourceId);
        mediaPlayer.setLooping(true); // Loop the music
        mediaPlayer.start();
    }

}
