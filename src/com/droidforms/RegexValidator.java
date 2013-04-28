package com.droidforms;

import java.util.regex.Pattern;

public class RegexValidator extends Validator {
	private Pattern pattern;

	public RegexValidator(String regex, String description) {
		super(description);
		pattern = Pattern.compile(regex);
	}

	@Override
	public boolean isValid(String text) {
		return pattern.matcher(text).matches();
	}
}
