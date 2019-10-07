package gov.gtas.validator;

import java.util.ArrayList;
import java.util.List;

public class PasswordValidator {

	private static final String DIGIT_PATTERN = "(?=.*[0-9]).*";
	private static final String LOWER_CHAR_PATTERN = "(?=.*[a-z]).*";
	private static final String UPPER_CHAR_PATTERN = "(?=.*[A-Z]).*";
	private static final String SPECIAL_CHAR_PATTERN = "(?=.*[~!@#$%^&*()_-]).*";

	public static String[] validate(final String password) {

		List<String> messages = new ArrayList<String>();

		if (password.length() == 0) {
			messages.add("Password can not be null");
		}
		if (password.length() < 8 || password.length() > 20) {
			messages.add("Password must be between 8 to 20 characters");
		}
		if (!password.matches(DIGIT_PATTERN)) {
			messages.add("Password must contain atleast one digit");
		}
		if (!password.matches(LOWER_CHAR_PATTERN)) {
			messages.add("Password must contain atleast one lower case letter");
		}
		if (!password.matches(UPPER_CHAR_PATTERN)) {
			messages.add("Password must contain atleast one upper case letter");
		}

		if (!password.matches(SPECIAL_CHAR_PATTERN)) {
			messages.add("Password must contain atleast one special letter");
		}

		return messages.toArray(new String[messages.size()]);
	}

}
