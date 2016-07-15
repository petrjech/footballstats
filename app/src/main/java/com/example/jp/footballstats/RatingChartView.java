package com.example.jp.footballstats;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;

/**
 * TODO: document your custom view class.
 */
public class RatingChartView extends View {
    private String mExampleString = "Hello World!";
    private int mExampleColor = Color.RED;
    private float mExampleDimension = 20;
    private Drawable mExampleDrawable;

    private TextPaint mTextPaint;
    private Paint mPaint;
    private float mTextWidth;
    private float mTextHeight;
    private boolean vlastnost;

    private Path chartPath;
    private ArrayList<Integer> chartColumns;
    private ArrayList<Integer> chartValues;

    private int contentWidth, contentHeight;

    public RatingChartView(Context context) {
        super(context);
        init();
    }

    public RatingChartView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public void init() {
        // Set up a default TextPaint object
        mTextPaint = new TextPaint();
        mTextPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setTextAlign(Paint.Align.LEFT);

        // Update TextPaint and text measurements from attributes
        invalidateTextPaintAndMeasurements();

        chartPath = new Path();

        //mResultCanvas = new Canvas(mResultBitmap);

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setFilterBitmap(true);
    }

    /**
     * Sets chart data
     *
     * @param columns chart X axis
     * @param values chart Y axis
     */
    protected void setChartData(final ArrayList<Integer> columns, final ArrayList<Integer> values) {
        chartColumns = columns;
        chartValues = values;
        recalculateChartPath();
        invalidate();
    }

    private void invalidateTextPaintAndMeasurements() {
        mTextPaint.setTextSize(mExampleDimension);
        mTextPaint.setColor(mExampleColor);
        mTextWidth = mTextPaint.measureText(mExampleString);

        Paint.FontMetrics fontMetrics = mTextPaint.getFontMetrics();
        mTextHeight = fontMetrics.bottom;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Draw the text.
        canvas.drawText(mExampleString, (contentWidth - mTextWidth) / 2,  (contentHeight + mTextHeight) / 2, mTextPaint);
        canvas.drawLine(0, 0, contentWidth, contentHeight, mPaint);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        //Get size requested and size mode
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int width, height;

        //Determine Width
        switch (widthMode) {
            case MeasureSpec.EXACTLY:
                width = widthSize;
                break;
            case MeasureSpec.AT_MOST:
                width = widthSize;
                break;
            case MeasureSpec.UNSPECIFIED:
            default:
                width = widthSize;
                break;
        }

        //Determine Height
        switch (heightMode) {
            case MeasureSpec.EXACTLY:
                height = heightSize;
                break;
            case MeasureSpec.AT_MOST:
                height = heightSize;
                break;
            case MeasureSpec.UNSPECIFIED:
            default:
                height = heightSize;
                break;
        }

        setMeasuredDimension(width, height);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        contentHeight = h - getPaddingLeft() - getPaddingRight();
        contentWidth = w - getPaddingTop() - getPaddingBottom();
        recalculateChartPath();
    }

    private void recalculateChartPath() {
        chartPath.reset();
        if (chartColumns == null || chartColumns.size() < 2) return;
        int columnsCount = chartColumns.size();
        float x, y;
        for (int column = 0; column < columnsCount; column++) {
            x = ((contentWidth * column) / (columnsCount - 1));
            y = (contentHeight * (100 - chartValues.get(column)));
            if (column == 0) {
                chartPath.moveTo(x, y);
            } else {
                chartPath.lineTo(x, y);
            }
        }
    }
}