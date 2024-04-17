package com.MobileAnarchy.Android.Widgets.Joystick;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

/* loaded from: classes.dex */
public class DualJoystickView extends LinearLayout {
    private static final String TAG = DualJoystickView.class.getSimpleName();
    private final boolean D = false;
    private Paint dbgPaint1;
    private View pad;
    private JoystickView stickL;
    private JoystickView stickR;

    public DualJoystickView(Context context) {
        super(context);
        this.stickL = new JoystickView(context);
        this.stickR = new JoystickView(context);
        initDualJoystickView();
    }

    public DualJoystickView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.stickL = new JoystickView(context, attrs);
        this.stickR = new JoystickView(context, attrs);
        initDualJoystickView();
    }

    private void initDualJoystickView() {
        setOrientation(0);
        this.stickL.setHandleColor(Color.rgb(112, 32, 32));
        this.stickR.setHandleColor(Color.rgb(32, 80, 48));
        this.pad = new View(getContext());
    }

    @Override // android.widget.LinearLayout, android.view.View
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        removeView(this.stickL);
        removeView(this.stickR);
        float padW = (float) (getMeasuredWidth() - (getMeasuredHeight() * 2));
        LayoutParams joyLParams = new LayoutParams((int) ((((float) getMeasuredWidth()) - padW) / 2.0f), getMeasuredHeight());
        this.stickL.setLayoutParams(joyLParams);
        this.stickR.setLayoutParams(joyLParams);
        this.stickL.TAG = "L";
        this.stickR.TAG = "R";
        this.stickL.setPointerId(-1);
        this.stickR.setPointerId(-1);
        addView(this.stickL);
        ViewGroup.LayoutParams padLParams = new ViewGroup.LayoutParams((int) padW, getMeasuredHeight());
        removeView(this.pad);
        this.pad.setLayoutParams(padLParams);
        addView(this.pad);
        addView(this.stickR);
    }

    @Override // android.widget.LinearLayout, android.view.View, android.view.ViewGroup
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        this.stickR.setTouchOffset(this.stickR.getLeft(), this.stickR.getTop());
    }

    public void setAutoReturnToCenter(boolean left, boolean right) {
        this.stickL.setAutoReturnToCenter(left);
        this.stickR.setAutoReturnToCenter(right);
    }

    public void setOnJostickMovedListener(JoystickMovedListener left, JoystickMovedListener right) {
        this.stickL.setOnJostickMovedListener(left);
        this.stickR.setOnJostickMovedListener(right);
    }

    public void setOnJostickClickedListener(JoystickClickedListener left, JoystickClickedListener right) {
        this.stickL.setOnJostickClickedListener(left);
        this.stickR.setOnJostickClickedListener(right);
    }

    public void setYAxisInverted(boolean leftYAxisInverted, boolean rightYAxisInverted) {
        this.stickL.setYAxisInverted(leftYAxisInverted);
        this.stickR.setYAxisInverted(rightYAxisInverted);
    }

    public void setMovementConstraint(int movementConstraint) {
        this.stickL.setMovementConstraint(movementConstraint);
        this.stickR.setMovementConstraint(movementConstraint);
    }

    public void setMovementRange(float movementRangeLeft, float movementRangeRight) {
        this.stickL.setMovementRange(movementRangeLeft);
        this.stickR.setMovementRange(movementRangeRight);
    }

    public void setMoveResolution(float leftMoveResolution, float rightMoveResolution) {
        this.stickL.setMoveResolution(leftMoveResolution);
        this.stickR.setMoveResolution(rightMoveResolution);
    }

    public void setUserCoordinateSystem(int leftCoordinateSystem, int rightCoordinateSystem) {
        this.stickL.setUserCoordinateSystem(leftCoordinateSystem);
        this.stickR.setUserCoordinateSystem(rightCoordinateSystem);
    }

    @Override // android.view.View, android.view.ViewGroup
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
    }

    @Override // android.view.View, android.view.ViewGroup
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return this.stickL.dispatchTouchEvent(ev) || this.stickR.dispatchTouchEvent(ev);
    }

    @Override // android.view.View
    public boolean onTouchEvent(MotionEvent ev) {
        return this.stickL.onTouchEvent(ev) || this.stickR.onTouchEvent(ev);
    }
}
