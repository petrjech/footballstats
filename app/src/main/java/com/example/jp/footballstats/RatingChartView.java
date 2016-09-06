package com.example.jp.footballstats;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;

import com.example.jp.footballstats.resources.Colors;

import java.util.ArrayList;

public class RatingChartView extends View {

    private final static float CHART_PADDING = 0.02f;
    private final static float CHART_STROKE_WIDTH = 0.005f;
    private final static float CHART_STROKE_WIDTH_BOLD = 0.025f;
    private final static float CHART_TEXT_SIZE = 20f;
    private final static float CHART_ASPECT_RATIO = 0.67f;

    private TextPaint mTextPaint;
    private Paint mPaint;

    private Path chartPath;
    private ArrayList<Integer> chartColumns;
    private ArrayList<Float> chartValues;
    private int ratingAverage;
    private float chartAveragePosition;
    private boolean isChartDataSet = false;

    private int contentWidth, contentHeight;
    private float chartOffset;

    public RatingChartView(Context context) {
        super(context);
        init();
    }

    public RatingChartView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        mTextPaint = new TextPaint();
        mTextPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setTextSize(CHART_TEXT_SIZE);
        mTextPaint.setColor(Colors.COLOR_CHART_TEXT);

        chartPath = new Path();

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
    }

    /**
     * Sets chart data
     *
     * @param columns chart X axis
     * @param values  chart Y axis
     */
    void setChartData(final ArrayList<Integer> columns, final ArrayList<Float> values, int ratingAverage) {
        chartColumns = columns;
        chartValues = values;
        this.ratingAverage = ratingAverage;
        if (columns.size() > 1 && ratingAverage > 0) isChartDataSet = true;
        if (isChartDataSet) {
            recalculateChartPath();
            invalidate();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        drawChartOutline(canvas);
        if (isChartDataSet) {
            drawChartData(canvas);
            drawChartText(canvas);
        }
    }

    private void drawChartOutline(Canvas canvas) {
        mPaint.setColor(Colors.COLOR_CHART_OUTLINE);
        mPaint.setStrokeWidth(contentWidth * CHART_STROKE_WIDTH);
        canvas.drawRect(0f, 0f, contentWidth, contentHeight, mPaint);
        mPaint.setColor(Colors.COLOR_CHART_OUTLINE_DIM);
        canvas.drawLine(chartOffset, contentHeight / 2f, contentWidth - chartOffset, contentHeight / 2f, mPaint);
        if (isChartDataSet) {
            chartAveragePosition = chartOffset + ((((float) ratingAverage - chartColumns.get(0)) / (chartColumns.get(chartColumns.size() - 1) - chartColumns.get(0))) * (contentWidth - (2 * chartOffset)));
            canvas.drawLine(chartAveragePosition, chartOffset, chartAveragePosition, contentHeight - chartOffset, mPaint);
        }
    }

    private void drawChartData(Canvas canvas) {
        mPaint.setColor(Colors.COLOR_CHART_DATA);
        mPaint.setStrokeWidth(contentWidth * CHART_STROKE_WIDTH_BOLD);
        canvas.drawPath(chartPath, mPaint);
    }

    private void drawChartText(Canvas canvas) {
        mTextPaint.setTextAlign(Paint.Align.LEFT);
        canvas.drawText("0", chartAveragePosition - chartOffset - (CHART_TEXT_SIZE / 2f), contentHeight - chartOffset, mTextPaint);
        canvas.drawText("100", chartAveragePosition + chartOffset, chartOffset + CHART_TEXT_SIZE, mTextPaint);
        canvas.drawText(chartColumns.get(0).toString(), chartOffset, (contentHeight / 2f) - chartOffset, mTextPaint);
        canvas.drawText(String.valueOf(ratingAverage), chartAveragePosition + chartOffset, (contentHeight / 2f) - chartOffset, mTextPaint);
        mTextPaint.setTextAlign(Paint.Align.RIGHT);
        canvas.drawText(chartColumns.get(chartColumns.size() - 1).toString(), contentWidth - chartOffset, (contentHeight / 2f) - chartOffset, mTextPaint);
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
    protected void onSizeChanged(int width, int height, int oldWidth, int oldHeight) {
        super.onSizeChanged(width, height, oldWidth, oldHeight);
        contentHeight = height - getPaddingLeft() - getPaddingRight();
        contentWidth = width - getPaddingTop() - getPaddingBottom();
        chartOffset = contentWidth * CHART_PADDING;
        if (contentHeight > contentWidth * CHART_ASPECT_RATIO)
            contentHeight = (int) (contentWidth * CHART_ASPECT_RATIO);
        if (isChartDataSet) recalculateChartPath();
    }

    private void recalculateChartPath() {
        chartPath.reset();
        int columnsCount = chartColumns.size();
        float x, y;
        float chartWidth = contentWidth - (2 * chartOffset);
        float chartHeight = contentHeight - (2 * chartOffset);
        for (int column = 0; column < columnsCount; column++) {
            x = chartOffset + ((chartWidth * column) / (columnsCount - 1));
            y = chartOffset + (chartHeight * (1 - chartValues.get(column)));
            if (column == 0) {
                chartPath.moveTo(x, y);
            } else {
                chartPath.lineTo(x, y);
            }
        }
    }
}