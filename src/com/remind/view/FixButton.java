package com.remind.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;

/**
 * @author ChenLong
 * 
 *         不会因为点击父类view而触发selector
 */
public class FixButton extends Button {

    public FixButton(Context context) {
        super(context);
    }

    public FixButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FixButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void setPressed(boolean pressed) {
        if (pressed && getParent() instanceof View && ((View) getParent()).isPressed()) {
            return;
        }
        super.setPressed(pressed);
    }
}
