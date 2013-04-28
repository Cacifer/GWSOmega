package com.droidforms.views;

import gws.grottworkshop.gwsomega.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.OnHierarchyChangeListener;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.droidforms.IFormItem;
import com.droidforms.RegexValidator;
import com.droidforms.Validator;


public abstract class AbsTextFormItem extends LinearLayout implements IFormItem, TextWatcher, OnHierarchyChangeListener, OnGlobalLayoutListener {

	private TextView labelView;
	private EditText valueView;
	private String labelText;
	private String valueText = "";
	private Boolean mandatory; 
	private Boolean isValid;
	private List<Validator> validators = new ArrayList<Validator>();
	private int minChars;
	private int maxChars;
	private boolean caseSensitive;
	private String errorDescription;
	private int siblingLabelId;

	public AbsTextFormItem(Context context, AttributeSet attrs) {
		this(context, attrs, R.attr.absTextFormItemStyle);
	}

	public AbsTextFormItem(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs);
		setOnHierarchyChangeListener(this);
		parseAttrs(attrs, defStyle);
	}

	private void parseAttrs(AttributeSet attrs, int defStyle) {
		TypedArray ta = getContext().obtainStyledAttributes(attrs, R.styleable.AbsTextFormItem, defStyle, 0);
		
		labelText = ta.getString(R.styleable.AbsTextFormItem_labelText);
		mandatory = ta.getBoolean(R.styleable.AbsTextFormItem_mandatory, false);
		minChars = ta.getInteger(R.styleable.AbsTextFormItem_minChars, 0);
		maxChars = ta.getInteger(R.styleable.AbsTextFormItem_maxChars, Integer.MAX_VALUE);
		caseSensitive = ta.getBoolean(R.styleable.AbsTextFormItem_caseSensitive, true);
		
		String regex = ta.getString(R.styleable.AbsTextFormItem_regex);
		if (regex != null) {
			String description = ta.getString(R.styleable.AbsTextFormItem_regexDescription);
			validators.add(new RegexValidator(regex, description));
		}

		int layout = ta.getResourceId(R.styleable.AbsTextFormItem_dfinternalLayout, 0);
		if (layout != 0) {
			LayoutInflater.from(getContext()).inflate(layout, this);
		}
		siblingLabelId = ta.getResourceId(R.styleable.AbsTextFormItem_useSiblingLabel, 0);
		if (siblingLabelId != 0) {
			getViewTreeObserver().addOnGlobalLayoutListener(this);
		}
		
		ta.recycle();
		
		parseAttrsImpl(attrs, defStyle);
		updateValueViewAppeareance();
	}
	
	protected void parseAttrsImpl(AttributeSet attrs, int defStyle) {
	}

	public void addValidator(Validator validator) {
		validators.add(validator);
		validate();
	}

	private void validate() {
		if (mandatory && TextUtils.isEmpty(valueText)) {
			setIsValid(false, R.string.field_is_mandatory);
			return;
		}
		
		int numChars = valueText.length();
		if (numChars < minChars) {
			setIsValid(false, R.string.field_is_too_short, minChars);
			return;
		}
		if (numChars > maxChars) {
			setIsValid(false, R.string.field_is_too_long, maxChars);
			return;
		}

		String adaptedText = caseSensitive ? valueText : valueText.toLowerCase(Locale.getDefault());
		for (Validator validator : validators) {
			if (!validator.isValid(adaptedText)) {
				setIsValid(false, validator.getDescription());
				return;
			}
		}
		setIsValid(true, null);
	}

	private void setIsValid(boolean newValue, int errorDescriptionResId, Object ... args) {
		String fmt = getResources().getString(errorDescriptionResId);
		setIsValid(newValue, String.format(fmt, args));
	}

	private void setIsValid(boolean newValue, String errorDescription, Object ...args) {
		if (isValid != null && isValid == newValue) {
			return;
		}
		
		isValid = newValue;
		this.errorDescription = generateErrorDescription(errorDescription);

		updateLabelViewAppearance();
		updateValueViewAppeareance();
	}

	private void updateLabelViewAppearance() {
		if (labelView != null && TextUtils.isEmpty(valueText)) {
			labelView.setText(String.format(isValid() ? "%s" : "%s *", labelText));
		}
	}

	private void updateValueViewAppeareance() {
		if (valueView == null) {
			return;
		}
		updateValueViewAppeareanceImpl();
	}

	protected abstract void updateValueViewAppeareanceImpl();

	private String generateErrorDescription(String errorDescription) {
		if (isValid()) {
			return null;
		}
		String firstPart = getResources().getString(R.string.error_message_first_part);
		return String.format("%s \"%s\" %s", firstPart, labelText, errorDescription);
	}
	
	@Override
	public boolean isValid() {
		return isValid == null ? true : isValid;
	}

	@Override
	public int getKey() {
		return getId();
	}

	@Override
	public String getValue() {
		return valueText;
	}

	@Override
	public void afterTextChanged(Editable s) {
		valueText = valueView.getText().toString().trim();
		validate();
	}
	
	public String getErrorDescription() {
		return errorDescription;
	}
	
	protected EditText getValueView() {
		return valueView;
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
	}
	
	@Override
	public void beforeTextChanged(CharSequence s, int start, int count, int after) {
	}
	
	@Override
	public void onChildViewAdded(View parent, View child) {
		if (labelView == null && child.getId() == R.id.label) {
			setLabelView(child);
			return;
		}
		if (valueView == null && child.getId() == R.id.value) {
			valueView = (EditText) child;
			valueView.addTextChangedListener(this);
			validate();
		}
	}

	public void setLabelView(View child) {
		labelView = (TextView) child;
		if (labelText == null) {
			labelText = labelView.getText().toString();
		}
		else {
			labelView.setText(labelText);
		}
		updateLabelViewAppearance();
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public void onGlobalLayout() {
		if (getParent() instanceof ViewGroup) {
			ViewGroup parent = (ViewGroup) getParent();
			TextView labelView = (TextView) parent.findViewById(siblingLabelId);
			setLabelView(labelView);
			getViewTreeObserver().removeGlobalOnLayoutListener(this);
		}
	}

	@Override
	public void onChildViewRemoved(View parent, View child) {
	}
}
