package com.example.weatherapp.Views.Widgets;

import android.content.Context;
import android.view.View;
import android.view.animation.*;
import android.widget.*;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import com.example.weatherapp.R;

public class ProgressButton {

    private CardView mCardView;
    private TextView mTextView;
    private ProgressBar mProgressBar;
    private ImageView mImageView;
    private ConstraintLayout mConstraintLayout;

    Animation fade_in;

    public ProgressButton(Context context, View view) {
        this.fade_in = AnimationUtils.loadAnimation(context, R.anim.fade_in);
        this.mCardView = view.findViewById(R.id.progress_btn_cardView);
        this.mConstraintLayout = view.findViewById(R.id.progress_btn_layout);
        this.mProgressBar = view.findViewById(R.id.progress_btn_bar);
        this.mTextView = view.findViewById(R.id.progress_btn_textView);
        this.mImageView = view.findViewById(R.id.progress_done_icon);
        this.mTextView.setText(context.getString(R.string.btn_Enter));
    }

    public ProgressButton(Context context, View view, String buttonText) {
        this.fade_in = AnimationUtils.loadAnimation(context, R.anim.fade_in);
        this.mCardView = view.findViewById(R.id.progress_btn_cardView);
        this.mConstraintLayout = view.findViewById(R.id.progress_btn_layout);
        this.mProgressBar = view.findViewById(R.id.progress_btn_bar);
        this.mImageView = view.findViewById(R.id.progress_done_icon);
        this.mTextView = view.findViewById(R.id.progress_btn_textView);
        this.mTextView.setText(buttonText);
    }

    public void buttonActivated() {
        this.mProgressBar.setAnimation(fade_in);
        this.mProgressBar.setVisibility(View.VISIBLE);
        this.mTextView.setVisibility(View.INVISIBLE);
        this.mImageView.setVisibility(View.INVISIBLE);
    }

    public void buttonFinished() {
        this.mProgressBar.setVisibility(View.INVISIBLE);
        this.mTextView.setVisibility(View.INVISIBLE);
        this.mImageView.setAnimation(fade_in);
        this.mImageView.setVisibility(View.VISIBLE);
    }

    public void buttonReset() {
        this.mProgressBar.setVisibility(View.INVISIBLE);
        this.mTextView.setVisibility(View.VISIBLE);
        this.mImageView.setVisibility(View.INVISIBLE);
    }

    public void setText(String buttonText) {
        this.mTextView.setText(buttonText);
    }
}
