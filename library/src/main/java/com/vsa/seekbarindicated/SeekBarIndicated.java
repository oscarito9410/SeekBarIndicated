package com.vsa.seekbarindicated;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

/**
 * Created by albertovecinasanchez on 27/8/15.
 */
public class SeekBarIndicated extends FrameLayout implements SeekBar.OnSeekBarChangeListener {

    private ViewGroup mWrapperIndicator;
    private TextView mTextViewProgress;
    private SeekBar mSeekBar;
    private RelativeLayout mWrapperSeekBarMaxMinValues;
    private TextView mTextViewMinValue;
    private TextView mTextViewMaxValue;

    private int mSeekBarMarginLeft = 0;
    private int mSeekBarMarginTop = 0;
    private int mSeekBarMarginBottom = 0;
    private int mSeekBarMarginRight = 0;

    private String mIndicatorText;


    private SeekBar.OnSeekBarChangeListener mOnSeekBarChangeListener;
    private TextProvider mTextProviderIndicator;

    private int mMeasuredWidth;

    public SeekBarIndicated(Context context) {
        super(context);
        if(!isInEditMode())
            init(context);
    }

    public SeekBarIndicated(Context context, AttributeSet attrs) {
        super(context, attrs);
        if(!isInEditMode())
            init(context, attrs, 0);
    }

    public SeekBarIndicated(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if(!isInEditMode())
            init(context, attrs, defStyleAttr);
    }

    private void init(Context context) {
        init(context, null, 0);
    }

    private void init(Context context, AttributeSet attrs, int defStyle) {
        View view = LayoutInflater.from(context).inflate(R.layout.view_seekbar_indicated, this);
        bindViews(view);

        if(attrs != null)
            setAttributes(context, attrs, defStyle);
        mSeekBar.setOnSeekBarChangeListener(this);
        mTextViewProgress.setText(String.valueOf(mSeekBar.getProgress()));
        getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mMeasuredWidth = mSeekBar.getWidth()
                        - mSeekBar.getPaddingLeft()
                        - mSeekBar.getPaddingRight();
                mSeekBar.setPadding(mSeekBar.getPaddingLeft(),
                        mWrapperIndicator.getHeight(),
                        mSeekBar.getPaddingRight(),
                        mSeekBar.getPaddingBottom());
                setIndicator();
                getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });
        mTextViewMinValue.setText("0");
        mTextViewMaxValue.setText(String.valueOf(mSeekBar.getMax()));
    }

    private void bindViews(View view) {
        mWrapperIndicator = (ViewGroup) view.findViewById(R.id.wrapper_seekbar_indicator);
        mTextViewProgress = (TextView) view.findViewById(R.id.txt_seekbar_indicated_progress);
        mSeekBar = (SeekBar) view.findViewById(R.id.seekbar);
        mWrapperSeekBarMaxMinValues= (RelativeLayout) view.findViewById(R.id.wrapper_seekbar_max_min_values);
        mTextViewMinValue = (TextView) view.findViewById(R.id.txt_seekbar_min_value);
        mTextViewMaxValue = (TextView) view.findViewById(R.id.txt_seekbar_max_value);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        setIndicator();
        if(mOnSeekBarChangeListener != null)
            mOnSeekBarChangeListener.onProgressChanged(seekBar, progress, fromUser);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        if(mOnSeekBarChangeListener != null)
            mOnSeekBarChangeListener.onStartTrackingTouch(seekBar);
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        if(mOnSeekBarChangeListener != null)
            mOnSeekBarChangeListener.onStopTrackingTouch(seekBar);
    }

    private void setIndicator() {
        if(mTextProviderIndicator != null) {
            mTextViewProgress.setText(mTextProviderIndicator.provideText(mSeekBar.getProgress()));
        } else {
            if(mIndicatorText != null) {
                try {
                    mTextViewProgress.setText(
                            String.valueOf(String.format(mIndicatorText, mSeekBar.getProgress())));
                } catch (Exception e) {
                    mTextViewProgress.setText(String.valueOf(mSeekBar.getProgress()));
                }
            } else {
                mTextViewProgress.setText(String.valueOf(mSeekBar.getProgress()));
            }
        }
        Rect padding = new Rect();
        mSeekBar.getThumb().getPadding(padding);

        int thumbPos = mSeekBar.getPaddingLeft()
                + mMeasuredWidth
                * mSeekBar.getProgress()
                / mSeekBar.getMax();
        mWrapperIndicator.setX(thumbPos
        - (mWrapperIndicator.getWidth() / 2));
    }

    private void setAttributes(Context context, AttributeSet attrs, int defStyle) {
        //then obtain typed array
        TypedArray arr = context.obtainStyledAttributes(attrs, R.styleable.SeekBarIndicated, defStyle, 0);

        //and get values you need by indexes from your array attributes defined above
        mSeekBarMarginLeft = arr.getDimensionPixelSize(R.styleable.SeekBarIndicated_seekbar_marginLeft, 0);
        mSeekBarMarginTop = arr.getDimensionPixelSize(R.styleable.SeekBarIndicated_seekbar_marginTop, 0);
        mSeekBarMarginRight = arr.getDimensionPixelSize(R.styleable.SeekBarIndicated_seekbar_marginRight, 0);
        mSeekBarMarginBottom = arr.getDimensionPixelSize(R.styleable.SeekBarIndicated_seekbar_marginBottom, 0);
        mIndicatorText = arr.getString(R.styleable.SeekBarIndicated_seekbar_indicatorText);
        arr.recycle();
        mWrapperSeekBarMaxMinValues.setPadding(
                mSeekBarMarginLeft,
                0,
                mSeekBarMarginRight,
                0);


        mSeekBar.setPadding(mSeekBarMarginLeft,
                mSeekBar.getPaddingTop() + mSeekBarMarginTop,
                mSeekBarMarginRight,
                mSeekBar.getPaddingBottom() + mSeekBarMarginBottom);

    }

    public void setOnSeekBarChangeListener(SeekBar.OnSeekBarChangeListener onSeekBarChangeListener) {
        mOnSeekBarChangeListener = onSeekBarChangeListener;
    }

    public void setTextProviderIndicator(TextProvider textProviderIndicator) {
        mTextProviderIndicator = textProviderIndicator;
    }

    public int getProgress() {
        return mSeekBar.getProgress();
    }

    public interface TextProvider {
        String provideText(int progress);
    }

}
