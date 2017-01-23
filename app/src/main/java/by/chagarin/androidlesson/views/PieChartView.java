package by.chagarin.androidlesson.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import java.util.Random;

public class PieChartView extends View {
    private Paint slicePaint;
    private float[] dataPoints;

    public PieChartView(Context context, AttributeSet attrs) {
        super(context, attrs);
        slicePaint = new Paint();
        slicePaint.setAntiAlias(true);
        slicePaint.setDither(true);
        //обязательно
        slicePaint.setStyle(Paint.Style.FILL);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (this.dataPoints != null) {
            Random r;
            int startLeft = 0;
            int startTop = 0;
            int endBottom = getWidth();
            int endRight = endBottom;
            RectF rectF = new RectF(startLeft, startTop, endRight, endBottom);
            float[] scaledValues = scale();
            float sliceStartPoints = 0;
            for (float scaledValue : scaledValues) {
                r = new Random();
                int color = Color.argb(100, r.nextInt(256), r.nextInt(256), r.nextInt(256));
                slicePaint.setColor(color);
                //рисуем дугу
                canvas.drawArc(rectF, sliceStartPoints, scaledValue, true, slicePaint);
                sliceStartPoints += scaledValue;
            }

        }
    }

    public void setDataPoints(float[] dataPoints) {
        this.dataPoints = dataPoints;
        //запуск перерисовки
        invalidate();
    }

    private float[] scale() {
        float[] scaledValues = new float[this.dataPoints.length];
        float total = getTotal();
        for (int i = 0; i < this.dataPoints.length; i++) {
            scaledValues[i] = (this.dataPoints[i] / total) * 360;
        }
        return scaledValues;
    }

    private float getTotal() {
        float total = 0;
        for (float val : this.dataPoints) {
            total += val;
        }
        return total;
    }
}
