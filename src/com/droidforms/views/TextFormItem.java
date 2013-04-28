package com.droidforms.views;

import gws.grottworkshop.gwsomega.R;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;



public class TextFormItem extends AbsTextFormItem {

	private int validationErrorIconResId;

	public TextFormItem(Context context, AttributeSet attrs) {
		this(context, attrs, R.attr.textFormItemStyle);
	}

	public TextFormItem(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}
	
	@Override
	protected void parseAttrsImpl(AttributeSet attrs, int defStyle) {
		TypedArray ta = getContext().obtainStyledAttributes(attrs, R.styleable.TextFormItem, defStyle, 0);
		validationErrorIconResId = ta.getResourceId(R.styleable.TextFormItem_validationErrorIcon, 0);
	}

	@Override
	protected void updateValueViewAppeareanceImpl() {
		int drawable = isValid() ? 0 : validationErrorIconResId;
		getValueView().setCompoundDrawablesWithIntrinsicBounds(0, 0, drawable, 0);
	}
}
