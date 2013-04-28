package com.droidforms;

public interface IFormItem {
	boolean isValid();
	int getKey();
	Object getValue();
}
