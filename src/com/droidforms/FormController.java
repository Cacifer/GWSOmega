package com.droidforms;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.util.SparseArray;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

public class FormController implements OnClickListener {
	
	private List<IFormItem> formItems = new ArrayList<IFormItem>();
	private IFormHost host;
	
	public FormController(IFormHost host, int submitButtonResId) {
		this.host = host;
		Activity activity = (Activity) host;
		Button submitButton = (Button) activity.findViewById(submitButtonResId);
		submitButton.setOnClickListener(this);
		
		View root = activity.findViewById(android.R.id.content);
		findViewsByType(root, IFormItem.class, formItems);
	}

	private static void findViewsByType(View root, Class<IFormItem> class_, List<IFormItem> dest) {
		if (root instanceof IFormItem) {
			dest.add((IFormItem) root);
		}
		if (!(root instanceof ViewGroup)) {
			return;
		}
		ViewGroup viewGroup = (ViewGroup)root;
		for (int i = 0; i < viewGroup.getChildCount(); ++i) {
			View child = viewGroup.getChildAt(i);
			findViewsByType(child, class_, dest);
		}
	}

	@Override
	public void onClick(View v) {
		boolean allOk = true;
		for (IFormItem formItem : formItems) {
			if (!isVisible(formItem)) {
				continue;
			}
			if (!formItem.isValid()) {
				allOk = false;
				break;
			}
		}
		if (allOk) {
			SparseArray<Object> formData = collectFormData();
			host.submitForm(formData);
			return;
		}
		Toast.makeText((Activity)host, "The form contains errors", Toast.LENGTH_LONG).show();
	}

	private boolean isVisible(IFormItem formItem) {
		View view = (View)formItem;
		return view.getVisibility() == View.VISIBLE;
	}

	private SparseArray<Object> collectFormData() {
		SparseArray<Object> result = new SparseArray<Object>();
		for (IFormItem formItem : formItems) {
			if (!isVisible(formItem)) {
				continue;
			}
			result.put(formItem.getKey(), formItem.getValue());
		}
		return result;
	}
}
