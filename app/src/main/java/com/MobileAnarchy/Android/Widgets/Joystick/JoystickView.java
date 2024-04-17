package com.MobileAnarchy.Android.Widgets.Joystick;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
//import org.lien.btjoystick.BluetoothJoystickActivity;

/* loaded from: classes.dex */
public class JoystickView extends View {
    public static final int CONSTRAIN_BOX = 0;
    public static final int CONSTRAIN_CIRCLE = 1;
    public static final int COORDINATE_CARTESIAN = 0;
    public static final int COORDINATE_DIFFERENTIAL = 1;
    public static final int INVALID_POINTER_ID = -1;
    private double angle;
    private boolean autoReturnToCenter;
    private Paint basePaint;
    private Paint bgPaint;
    private int bgRadius;
    private int cX;
    private int cY;
    private int cartX;
    private int cartY;
    private JoystickClickedListener clickListener;
    private float clickThreshold;
    private boolean clicked;
    private int dimX;
    private int handleInnerBoundaries;
    private Paint handlePaint;
    private int handleRadius;
    private float handleX;
    private float handleY;
    private int innerPadding;
    private JoystickMovedListener moveListener;
    private float moveResolution;
    private int movementConstraint;
    private int movementRadius;
    private float movementRange;
    private int offsetX;
    private int offsetY;
    private double radial;
    private float reportX;
    private float reportY;
    private Paint stickPaint;
    private float touchPressure;
    private float touchX;
    private float touchY;
    private int userCoordinateSystem;
    private int userX;
    private int userY;
    private boolean yAxisInverted;
    private final boolean D = false;
    String TAG = "JoystickView";
    private int pointerId = -1;

    public JoystickView(Context context) {
        super(context);
        initJoystickView();
    }

    public JoystickView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initJoystickView();
    }

    public JoystickView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initJoystickView();
    }

    private void initJoystickView() {
        setFocusable(true);
        this.bgPaint = new Paint(1);
        this.bgPaint.setColor(-7829368);
        this.bgPaint.setStrokeWidth(1.0f);
        this.bgPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        this.handlePaint = new Paint(1);
        this.handlePaint.setColor(-12303292);
        this.handlePaint.setStrokeWidth(1.0f);
        this.handlePaint.setStyle(Paint.Style.FILL_AND_STROKE);
        this.stickPaint = new Paint(1);
        this.stickPaint.setColor(Color.rgb(48, 48, 64));
        this.stickPaint.setStrokeWidth(15.0f);
        this.stickPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        this.basePaint = new Paint(1);
        this.basePaint.setColor(Color.rgb(64, 64, 32));
        this.basePaint.setStrokeWidth(1.0f);
        this.basePaint.setStyle(Paint.Style.FILL_AND_STROKE);
        this.innerPadding = 10;
        setMovementRange(10.0f);
        setMoveResolution(1.0f);
        setClickThreshold(0.4f);
        setYAxisInverted(true);
        setUserCoordinateSystem(0);
        setAutoReturnToCenter(true);
    }

    public void setHandleColor(int color) {
        this.handlePaint.setColor(color);
    }

    public void setAutoReturnToCenter(boolean autoReturnToCenter) {
        this.autoReturnToCenter = autoReturnToCenter;
    }

    public boolean isAutoReturnToCenter() {
        return this.autoReturnToCenter;
    }

    public void setUserCoordinateSystem(int userCoordinateSystem) {
        if (userCoordinateSystem < 0 || this.movementConstraint > 1) {
            Log.e(this.TAG, "invalid value for userCoordinateSystem");
        } else {
            this.userCoordinateSystem = userCoordinateSystem;
        }
    }

    public int getUserCoordinateSystem() {
        return this.userCoordinateSystem;
    }

    public void setMovementConstraint(int movementConstraint) {
        if (movementConstraint < 0 || movementConstraint > 1) {
            Log.e(this.TAG, "invalid value for movementConstraint");
        } else {
            this.movementConstraint = movementConstraint;
        }
    }

    public int getMovementConstraint() {
        return this.movementConstraint;
    }

    public boolean isYAxisInverted() {
        return this.yAxisInverted;
    }

    public void setYAxisInverted(boolean yAxisInverted) {
        this.yAxisInverted = yAxisInverted;
    }

    public void setClickThreshold(float clickThreshold) {
        if (clickThreshold < 0.0f || clickThreshold > 1.0f) {
            Log.e(this.TAG, "clickThreshold must range from 0...1.0f inclusive");
        } else {
            this.clickThreshold = clickThreshold;
        }
    }

    public float getClickThreshold() {
        return this.clickThreshold;
    }

    public void setMovementRange(float movementRange) {
        this.movementRange = movementRange;
    }

    public float getMovementRange() {
        return this.movementRange;
    }

    public void setMoveResolution(float moveResolution) {
        this.moveResolution = moveResolution;
    }

    public float getMoveResolution() {
        return this.moveResolution;
    }

    public void setOnJostickMovedListener(JoystickMovedListener listener) {
        this.moveListener = listener;
    }

    public void setOnJostickClickedListener(JoystickClickedListener listener) {
        this.clickListener = listener;
    }

    @Override // android.view.View
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(measure(widthMeasureSpec), measure(heightMeasureSpec));
    }

    @Override // android.view.View
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        int d = Math.min(getMeasuredWidth(), getMeasuredHeight());
        this.dimX = d;
        this.cX = d / 2;
        this.cY = d / 2;
        this.bgRadius = (this.dimX / 2) - this.innerPadding;
        this.handleRadius = (int) (((double) d) * 0.22d);
        this.handleInnerBoundaries = this.handleRadius;
        this.movementRadius = Math.min(this.cX, this.cY) - this.handleInnerBoundaries;
    }

    private int measure(int measureSpec) {
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
        if (specMode == 0) {
            return 200;
        }
        return specSize;
    }

    @Override // android.view.View
    protected void onDraw(Canvas canvas) {
        canvas.save();
        canvas.drawCircle((float) this.cX, (float) this.cY, (float) this.bgRadius, this.bgPaint);
        this.handleX = this.touchX + ((float) this.cX);
        this.handleY = this.touchY + ((float) this.cY);
        canvas.drawCircle((float) this.cX, (float) this.cY, (float) (this.handleRadius >> 1), this.basePaint);
        canvas.drawLine((float) this.cX, (float) this.cY, this.handleX, this.handleY, this.stickPaint);
        canvas.drawCircle(this.handleX, this.handleY, (float) this.handleRadius, this.handlePaint);
        canvas.restore();
    }

    private void constrainBox() {
        this.touchX = Math.max(Math.min(this.touchX, (float) this.movementRadius), (float) (-this.movementRadius));
        this.touchY = Math.max(Math.min(this.touchY, (float) this.movementRadius), (float) (-this.movementRadius));
    }

    private void constrainCircle() {
        float diffX = this.touchX;
        float diffY = this.touchY;
        double radial = Math.sqrt((double) ((diffX * diffX) + (diffY * diffY)));
        if (radial > ((double) this.movementRadius)) {
            this.touchX = (float) ((int) ((((double) diffX) / radial) * ((double) this.movementRadius)));
            this.touchY = (float) ((int) ((((double) diffY) / radial) * ((double) this.movementRadius)));
        }
    }

    public void setPointerId(int id) {
        this.pointerId = id;
    }

    public int getPointerId() {
        return this.pointerId;
    }

    @Override // android.view.View
    public boolean onTouchEvent(MotionEvent ev) {
        int x;
        int action = ev.getAction();
        switch (action & 255) {
            case 0:
                if (this.pointerId == -1 && (x = (int) ev.getX()) >= this.offsetX && x < this.offsetX + this.dimX) {
                    setPointerId(ev.getPointerId(0));
                    return true;
                }
                break;
            case 1:
            case 3:
                if (this.pointerId != -1) {
                    returnHandleToCenter();
                    setPointerId(-1);
                    break;
                }
                break;
            case 2:
                return processMoveEvent(ev);
            case 5:
                if (this.pointerId == -1) {
                    int pointerId = ev.getPointerId((action & 65280) >> 8);
                    int x2 = (int) ev.getX(pointerId);
                    if (x2 >= this.offsetX && x2 < this.offsetX + this.dimX) {
                        setPointerId(pointerId);
                        return true;
                    }
                }
                break;
            case 6:
                if (this.pointerId != -1 && ev.getPointerId((action & 65280) >> 8) == this.pointerId) {
                    returnHandleToCenter();
                    setPointerId(-1);
                    return true;
                }
                break;
        }
        return false;
    }

    private boolean processMoveEvent(MotionEvent ev) {
        if (this.pointerId == -1) {
            return false;
        }
        int pointerIndex = ev.findPointerIndex(this.pointerId);
        this.touchX = (ev.getX(pointerIndex) - ((float) this.cX)) - ((float) this.offsetX);
        this.touchY = (ev.getY(pointerIndex) - ((float) this.cY)) - ((float) this.offsetY);
        reportOnMoved();
        invalidate();
        this.touchPressure = ev.getPressure(pointerIndex);
        reportOnPressure();
        return true;
    }

    /* access modifiers changed from: private */
    public void reportOnMoved() {
        boolean rx;
        boolean ry;
        if (this.movementConstraint == 1) {
            constrainCircle();
        } else {
            constrainBox();
        }
        calcUserCoordinates();
        if (this.moveListener != null) {
            if (Math.abs(this.touchX - this.reportX) >= this.moveResolution) {
                rx = true;
            } else {
                rx = false;
            }
            if (Math.abs(this.touchY - this.reportY) >= this.moveResolution) {
                ry = true;
            } else {
                ry = false;
            }
            if (rx || ry) {
                this.reportX = this.touchX;
                this.reportY = this.touchY;
                this.moveListener.OnMoved(this.cartX, this.cartY);
            }
        }
    }

    private void calcUserCoordinates() {
        this.cartX = (int) ((this.touchX / ((float) this.movementRadius)) * this.movementRange);
        this.cartY = (int) ((this.touchY / ((float) this.movementRadius)) * this.movementRange);
        this.radial = Math.sqrt((double) ((this.cartX * this.cartX) + (this.cartY * this.cartY)));
        this.angle = Math.atan2((double) this.cartY, (double) this.cartX);
        if (!this.yAxisInverted) {
            this.cartY *= -1;
        }
        if (this.userCoordinateSystem == 0) {
            this.userX = this.cartX;
            this.userY = this.cartY;
        } else if (this.userCoordinateSystem == 1) {
            this.userX = this.cartY + (this.cartX / 4);
            this.userY = this.cartY - (this.cartX / 4);
            if (((float) this.userX) < (-this.movementRange)) {
                this.userX = (int) (-this.movementRange);
            }
            if (((float) this.userX) > this.movementRange) {
                this.userX = (int) this.movementRange;
            }
            if (((float) this.userY) < (-this.movementRange)) {
                this.userY = (int) (-this.movementRange);
            }
            if (((float) this.userY) > this.movementRange) {
                this.userY = (int) this.movementRange;
            }
        }
    }

    private void reportOnPressure() {
        if (this.clickListener == null) {
            return;
        }
        if (this.clicked && this.touchPressure < this.clickThreshold) {
            this.clickListener.OnReleased();
            this.clicked = false;
            invalidate();
        } else if (!this.clicked && this.touchPressure >= this.clickThreshold) {
            this.clicked = true;
            this.clickListener.OnClicked();
            invalidate();
            performHapticFeedback(1);
        }
    }

    private void returnHandleToCenter() {
        if (this.autoReturnToCenter) {
            final double intervalsX = (double) ((0.0f - this.touchX) / 5.0f);
            final double intervalsY = (double) ((0.0f - this.touchY) / 5.0f);
            for (int i = 0; i < 5; i++) {
                int finalI = i;
                postDelayed(new Runnable() { // from class: com.MobileAnarchy.Android.Widgets.Joystick.JoystickView.1
                    @Override // java.lang.Runnable
                    public void run() {
                        JoystickView.this.touchX = (float) (((double) JoystickView.this.touchX) + intervalsX);
                        JoystickView.this.touchY = (float) (((double) JoystickView.this.touchY) + intervalsY);
                        JoystickView.this.reportOnMoved();
                        JoystickView.this.invalidate();
                        if (JoystickView.this.moveListener != null && finalI == 4) {
                            JoystickView.this.moveListener.OnReturnedToCenter();
                        }
                    }
                }, (long) (i * 40));
            }
            if (this.moveListener != null) {
                this.moveListener.OnReleased();
            }
        }
    }

    public void setTouchOffset(int x, int y) {
        this.offsetX = x;
        this.offsetY = y;
    }
}
