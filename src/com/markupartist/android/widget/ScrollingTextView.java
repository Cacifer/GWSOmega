package com.markupartist.android.widget;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.TextView;

// TODO: Auto-generated Javadoc
/**
 * The Class ScrollingTextView.
 *
 * @author marco
 * Workaround to be able to scroll text inside a TextView without it required
 * to be focused. For some strange reason there isn't an easy way to do this
 * natively.
 * 
 * Original code written by Evan Cummings:
 * http://androidbears.stellarpc.net/?p=185
 */
public class ScrollingTextView extends TextView {

	/**
	 * Instantiates a new scrolling text view.
	 *
	 * @param context the context
	 * @param attrs the attrs
	 * @param defStyle the def style
	 */
	public ScrollingTextView(Context context, AttributeSet attrs,
	        int defStyle) {
    	super(context, attrs, defStyle);
    }

    /**
     * Instantiates a new scrolling text view.
     *
     * @param context the context
     * @param attrs the attrs
     */
    public ScrollingTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * Instantiates a new scrolling text view.
     *
     * @param context the context
     */
    public ScrollingTextView(Context context) {
        super(context);
    }

    /**
     * On focus changed.
     *
     * @param focused the focused
     * @param direction the direction
     * @param previouslyFocusedRect the previously focused rect
     * @see android.widget.TextView#onFocusChanged(boolean, int, android.graphics.Rect)
     */
    @Override
    protected void onFocusChanged(boolean focused, int direction,
            Rect previouslyFocusedRect) {
        if (focused) {
            super.onFocusChanged(focused, direction, previouslyFocusedRect);
        }
    }

    /**
     * On window focus changed.
     *
     * @param focused the focused
     * @see android.widget.TextView#onWindowFocusChanged(boolean)
     */
    @Override
    public void onWindowFocusChanged(boolean focused) {
        if (focused) {
            super.onWindowFocusChanged(focused);
        }
    }

    /**
     * Checks if is focused.
     *
     * @return true, if is focused
     * @see android.view.View#isFocused()
     */
    @Override
    public boolean isFocused() {
        return true;
    }
}
