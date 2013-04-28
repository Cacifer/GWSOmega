package com.droidforms.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.CheckBox;

import com.droidforms.IFormItem;

public class CheckBoxFormItem extends CheckBox implements IFormItem {

	public CheckBoxFormItem(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public CheckBoxFormItem(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs);
	}

	@Override
	public boolean isValid() {
		return true;
	}

	@Override
	public int getKey() {
		return getId();
	}

	@Override
	public Boolean getValue() {
		return isChecked();
	}
}
