package com.droidforms;

import android.util.SparseArray;

public interface IFormHost {
	public void submitForm(SparseArray<Object> formData);
}
