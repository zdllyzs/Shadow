package com.zdlly.shadow.MyView;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Xfermode;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.Toast;

/**
 * Created by zdlly on 2017/3/5.
 */
public class MyShapeView extends android.support.v7.widget.AppCompatImageView {




    private int isReload=0;
    private int mSides = 3;
    private Paint mPaint;
    private Xfermode mXfermode;
    private Bitmap mMask;
    private Bitmap currBitmap;
    private Path path;


    private int color= Color.GREEN;

    final public static int DRAG = 1;
    final public static int ZOOM = 2;
    final public static int COLOR = 1;
    final public static int PICTURE = 2;



    public int mode = 0;
    public int mode1=PICTURE;

    private Matrix matrix = new Matrix();
    private Matrix matrix1 = new Matrix();
    private Matrix saveMatrix = new Matrix();

    private float x_down = 0;
    private float y_down = 0;


    private PointF mid = new PointF();
    private float initDis = 1f;

    private Canvas canvas;

    public void setColor(int color) {
        this.color = color;
    }

    public void setMode1(int mode1) {
        this.mode1 = mode1;
    }
    public void setIsReload(int isReload) {
        this.isReload = isReload;
    }
    public void setmSides(int mSides) {
        this.mSides = mSides;
    }

    public MyShapeView(Context context) {
        this(context, null);
    }

    public MyShapeView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MyShapeView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        path = new Path();
        matrix = new Matrix();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        mPaint.setColor(color);
        if(mode1==COLOR){
            mXfermode = new PorterDuffXfermode(PorterDuff.Mode.SRC);
        }else {
            mXfermode = new PorterDuffXfermode(PorterDuff.Mode.DST_IN);
        }
        int id = canvas.saveLayer(0, 0, canvas.getWidth(), canvas.getHeight(), null, Canvas.ALL_SAVE_FLAG);
        createMask();
        super.onDraw(canvas);
        mPaint.setXfermode(mXfermode);
        canvas.save();
        if(isReload==1) {
            currBitmap=null;
            isReload=0;
        }
        if (currBitmap == null) {
            canvas.drawBitmap(mMask, 0, 0, mPaint);

        } else {
            canvas.save();
            Paint paint = new Paint();
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
            canvas.drawPaint(paint);
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC));
            canvas.restore();
            canvas.drawBitmap(currBitmap, matrix, null);
        }
        canvas.restore();
        mPaint.setXfermode(null);

        canvas.restoreToCount(id);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (currBitmap == null) {
            setDrawingCacheEnabled(true);
            currBitmap = Bitmap.createBitmap(this.getDrawingCache());
            setDrawingCacheEnabled(false);
        }

        int action = event.getAction();
        switch (action & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                saveMatrix.set(matrix);
                x_down = event.getX();
                y_down = event.getY();
                mode = DRAG;
                break;

            case MotionEvent.ACTION_POINTER_DOWN:
                saveMatrix.set(matrix);
                initDis = ToolScaleView.spacing(event);
                mode = ZOOM;
                ToolScaleView.midPoint(mid, event);
                break;

            case MotionEvent.ACTION_MOVE:
                if (mode == DRAG) {
                    matrix1.set(saveMatrix);
                    matrix1.postTranslate(event.getX() - x_down, event.getY() - y_down);
                    matrix.set(matrix1);
                    invalidate();
                } else if (mode == ZOOM) {
                    matrix1.set(saveMatrix);
                    float newDis = ToolScaleView.spacing(event);
                    float scale = newDis / initDis;
                    matrix1.postScale(scale, scale, mid.x, mid.y);
                    matrix.set(matrix1);
                    invalidate();
                }
                break;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
                mode = 0;
                break;
            case MotionEvent.ACTION_OUTSIDE:
                matrix.set(saveMatrix);
        }

        Bitmap dealBitmap = Bitmap.createBitmap(currBitmap, 0, 0, currBitmap.getWidth(), currBitmap.getHeight(), matrix, true);
        this.setImageBitmap(dealBitmap);
        return true;

    }


    private void createMask() {
        int maskWidth = getMeasuredWidth();
        int maskHeight = getMeasuredHeight();
        mMask = Bitmap.createBitmap(maskWidth, maskHeight, Bitmap.Config.ARGB_8888);
        canvas = new Canvas(mMask);
        int rectSize = maskWidth / 2;
        drawMultShape(canvas, mSides, rectSize);
    }

    public void drawMultShape(Canvas canvas, int count, float radius) {

        canvas.translate(radius, radius);
        if (count < 3) {
            Toast.makeText(getContext(), "无法绘制边数少于3的图形", Toast.LENGTH_SHORT).show();
        } else if (count < 30) {
            for (int i = 0; i < count; i++) {
                if (i == 0) {
                    path.moveTo(radius * cos(360 / count * i), radius * sin(360 / count * i));
                } else {
                    path.lineTo(radius * cos(360 / count * i), radius * sin(360 / count * i));
                }
            }
            path.close();
            canvas.drawPath(path, mPaint);
            path.reset();
        } else {
            canvas.drawCircle(0, 0, radius, mPaint);
        }
    }


    float sin(int num) {
        return (float) Math.sin(num * Math.PI / 180);
    }

    float cos(int num) {
        return (float) Math.cos(num * Math.PI / 180);
    }
}