package anim.example.com.switchbutton;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by zengjun on 2016/9/1.
 */
public class SwitchButton extends View {

    private float iosWidth;
    private float iosHeight;
    private float outStrokeWidth;
    private int outStrokeColor;
    private int toggleColor;
    private int onColor;
    private int offColor;
    private boolean isOn;

    private final float defaultIosWidth = dp2px(120);
    private final float defaultIosHeight = dp2px(50);
    private final float defaultOutStrokeWidth = dp2px(2);

    private final int defaultOutStrokeColor = Color.parseColor("#e6e6e6");
    private final int defaultToggleColor = Color.WHITE;
    private final int defaultOnColor = Color.BLUE;
    private final int defaultOffColor = Color.parseColor("#DCD8D8");
    private final boolean defaultIsOn = false;

    private float radiusOutSide;
    private float radiusInside;
    private final float bezierConstant = 0.551915024494f;
    private float bezierFactorOutSide;
    private float bezierFactorInside;
    private float distance;

    private Paint mStrokePaint;
    private Paint mTogglePaint;
    private Paint mOnPaint;
    private Paint mOffPaint;

    private float paddingLeft;
    private float paddingRight;
    private float paddingTop;
    private float paddingBottom;
    private float paddingLeftInside;
    private final float balanceFactor = 2.8f;

    public SwitchButton(Context context) {
        this(context, null);
    }

    public SwitchButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SwitchButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        final TypedArray ta = context.getTheme().obtainStyledAttributes(attrs, R.styleable.ios_switch_button, defStyleAttr, 0);
        iosWidth = ta.getDimension(R.styleable.ios_switch_button_ios_width, defaultIosWidth);
        iosHeight = ta.getDimension(R.styleable.ios_switch_button_ios_height, defaultIosHeight);
        outStrokeWidth = ta.getDimension(R.styleable.ios_switch_button_out_stroke_width, defaultOutStrokeWidth);
        radiusOutSide = iosHeight/2;
        bezierFactorOutSide = radiusOutSide*bezierConstant;
        radiusInside = (iosHeight - 2*outStrokeWidth)/2 + balanceFactor;
        bezierFactorInside = radiusInside*bezierConstant;
        outStrokeColor = ta.getColor(R.styleable.ios_switch_button_out_stroke_color, defaultOutStrokeColor);
        toggleColor = ta.getColor(R.styleable.ios_switch_button_toggle_color, defaultToggleColor);
        onColor = ta.getColor(R.styleable.ios_switch_button_on_background_color, defaultOnColor);
        offColor = ta.getColor(R.styleable.ios_switch_button_off_background_color, defaultOffColor);
        isOn = ta.getBoolean(R.styleable.ios_switch_button_turn_on, defaultIsOn);
        ta.recycle();
        paddingLeft = getPaddingLeft()+dp2px(2);
        paddingTop = getPaddingTop()+dp2px(2);
        paddingRight = getPaddingRight()+dp2px(2);
        paddingBottom = getPaddingBottom()+dp2px(2);
        paddingLeftInside = paddingLeft - balanceFactor;
        distance = 200;
        initPaint();
    }


    private void initPaint(){
        mStrokePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mStrokePaint.setStyle(Paint.Style.STROKE);
        mStrokePaint.setStrokeWidth(outStrokeWidth);
        mStrokePaint.setColor(outStrokeColor);

        mTogglePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTogglePaint.setStyle(Paint.Style.FILL);
        mTogglePaint.setStrokeWidth(radiusInside);
        mTogglePaint.setColor(toggleColor);

        mOnPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mOnPaint.setStyle(Paint.Style.FILL);
        mOnPaint.setColor(onColor);

        mOffPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mOffPaint.setStyle(Paint.Style.FILL);
        mOffPaint.setColor(offColor);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(measure(widthMeasureSpec, true), measure(heightMeasureSpec, false));
    }

    private int measure(int mSpec, boolean isWidth){
        int mode = MeasureSpec.getMode(mSpec);
        int size = MeasureSpec.getSize(mSpec);
        if (mode == MeasureSpec.EXACTLY){
            return size;
        }else{
            return  (int) (isWidth ? iosWidth + paddingLeft + paddingRight : iosHeight + paddingTop + paddingBottom);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawPath(strokePath(), mStrokePaint);
        canvas.drawPath(leftPath(), mOnPaint);
        canvas.drawPath(rightPath(), mOffPaint);
        invalidate();
    }

    private Path strokePath(){
        Path path = new Path();
        path.reset();
        CubicHPoint h1 = new CubicHPoint(paddingLeft + iosWidth - radiusOutSide, paddingTop, bezierFactorOutSide);
        CubicVPoint v2 = new CubicVPoint(paddingLeft +iosWidth, paddingTop + radiusOutSide, bezierFactorOutSide);
        CubicHPoint h3 = new CubicHPoint(paddingLeft + iosWidth - radiusOutSide, paddingTop + iosHeight, bezierFactorOutSide);
        CubicHPoint h4 = new CubicHPoint(paddingLeft + radiusOutSide, paddingTop + iosHeight, bezierFactorOutSide);
        CubicVPoint v5 = new CubicVPoint(paddingLeft, paddingTop + radiusOutSide, bezierFactorOutSide);
        CubicHPoint h6 = new CubicHPoint(paddingLeft + radiusOutSide, paddingTop, bezierFactorOutSide);
        path.moveTo(h6.x, h6.y);
        path.lineTo(paddingLeft + iosWidth - radiusOutSide, paddingTop);
        path.cubicTo(h1.right.x, h1.right.y, v2.top.x, v2.top.y, v2.x, v2.y);
        path.cubicTo(v2.bottom.x, v2.bottom.y, h3.right.x, h3.right.y, h3.x, h3.y);
        path.lineTo(h4.x, h4.y);
        path.cubicTo(h4.left.x, h4.left.y, v5.bottom.x, v5.bottom.y, v5.x, v5.y);
        path.cubicTo(v5.top.x, v5.top.y, h6.left.x, h6.right.y, h6.x, h6.y);
        return path;
    }


    private Path leftPath(){
        Path path = new Path();
        path.reset();
        CubicHPoint h1 = new CubicHPoint(paddingLeftInside+radiusOutSide, paddingTop+outStrokeWidth-balanceFactor, bezierFactorInside);
        CubicHPoint h2 = new CubicHPoint(paddingLeftInside+radiusOutSide+distance, paddingTop+outStrokeWidth-balanceFactor, bezierFactorInside);
        CubicVPoint v3 = new CubicVPoint(paddingLeftInside+radiusOutSide+distance-radiusInside,paddingTop+radiusOutSide, bezierFactorInside);
        CubicHPoint h4 = new CubicHPoint(paddingLeftInside+radiusOutSide+distance, paddingTop+iosHeight-outStrokeWidth+balanceFactor, bezierFactorInside);
        CubicHPoint h5 = new CubicHPoint(paddingLeftInside+radiusOutSide, paddingTop+iosHeight-outStrokeWidth+balanceFactor, bezierFactorInside);
        CubicVPoint v6 = new CubicVPoint(paddingLeftInside+outStrokeWidth,paddingTop+radiusOutSide, bezierFactorInside);
        path.moveTo(h1.x, h1.y);
        path.lineTo(h2.x, h2.y);
        path.cubicTo(h2.left.x, h2.left.y, v3.top.x, v3.top.y, v3.x, v3.y);
        path.cubicTo(v3.bottom.x, v3.bottom.y, h4.left.x, h4.left.y, h4.x, h4.y);
        path.lineTo(h5.x, h5.y);
        path.cubicTo(h5.left.x, h5.left.y, v6.bottom.x, v6.bottom.y, v6.x, v6.y);
        path.cubicTo(v6.top.x, v6.top.y, h1.left.x, h1.left.y, h1.x, h1.y);
        return path;
    }

    private Path rightPath(){
        Path path = new Path();
        path.reset();
        CubicHPoint h1 = new CubicHPoint(paddingLeftInside+radiusOutSide+distance, paddingTop+outStrokeWidth-balanceFactor, bezierFactorInside);
        CubicHPoint h2 = new CubicHPoint(paddingLeftInside+iosWidth-radiusOutSide, paddingTop+outStrokeWidth-balanceFactor, bezierFactorInside);
        CubicVPoint v3 = new CubicVPoint(paddingLeftInside+iosWidth-outStrokeWidth+balanceFactor,paddingTop+radiusOutSide, bezierFactorInside);
        CubicHPoint h4 = new CubicHPoint(paddingLeftInside+iosWidth-radiusOutSide, paddingTop+iosHeight-outStrokeWidth+balanceFactor, bezierFactorInside);
        CubicHPoint h5 = new CubicHPoint(paddingLeftInside+radiusOutSide+distance, paddingTop+iosHeight-outStrokeWidth+balanceFactor, bezierFactorInside);
        CubicVPoint v6 = new CubicVPoint(paddingLeftInside+radiusOutSide+distance+radiusInside,paddingTop+radiusOutSide, bezierFactorInside);
        path.moveTo(h1.x, h1.y);
        path.lineTo(h2.x, h2.y);
        path.cubicTo(h2.right.x, h2.right.y, v3.top.x, v3.top.y, v3.x, v3.y);
        path.cubicTo(v3.bottom.x, v3.bottom.y, h4.right.x, h4.right.y, h4.x, h4.y);
        path.lineTo(h5.x, h5.y);
        path.cubicTo(h5.right.x, h5.right.y, v6.bottom.x, v6.bottom.y, v6.x, v6.y);
        path.cubicTo(v6.top.x, v6.top.y, h1.right.x, h1.right.y, h1.x, h1.y);
        return path;
    }

    class CubicVPoint {
        public float x;
        public float y;
        public PointF top;
        public PointF bottom;

        public CubicVPoint(float x, float y, float bezierFactor) {
            this.x = x;
            this.y = y;
            top = new PointF(x, y-bezierFactor);
            bottom = new PointF(x, y+bezierFactor);
        }

        public void adjustX(float offX){
            this.x += offX;
            top.x += offX;
            bottom.x += offX;
        }
    }

    class CubicHPoint {
        public float x;
        public float y;
        public PointF left;
        public PointF right;

        public CubicHPoint(float x, float y, float bezierFactor) {
            this.x = x;
            this.y = y;
            left = new PointF(x -bezierFactor, y);
            right = new PointF(x +bezierFactor, y);
        }

        public void adjuestX(float offX){
            x += offX;
            left.x += offX;
            right.x += offX;
        }
    }

    public float dp2px(float dp) {
        final float scale = getResources().getDisplayMetrics().density;
        return dp * scale + 0.5f;
    }

}
