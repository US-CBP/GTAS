package gov.gtas.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PasswordValidation {
	private static final Logger logger = LoggerFactory.getLogger(PasswordValidation.class);
	private static Pattern pattern;
	private static Matcher matcher;

	private static final String PASSWORD_PATTERN = "((?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$!%]).{8,20})";

	public PasswordValidation() {
		pattern = Pattern.compile(PASSWORD_PATTERN);
	}

	/**
	 * Validate password with regular expression
	 * 
	 * @param password
	 *            password for validation
	 * @return true valid password, false invalid password
	 */
	public static boolean validate(final String password) {

		matcher = pattern.matcher(password);
		return matcher.matches();

	}

	public static void main(String[] args) {
		logger.info(" SS4rinivas1!" + new PasswordValidation().validate("S4rinivas!"));
	}

	private static int calculatePasswordStrength(String password) {

		// total score of password
		int iPasswordScore = 0;

		if (password.length() < 8)
			return 0;
		else if (password.length() >= 10)
			iPasswordScore += 2;
		else
			iPasswordScore += 1;

		// if it contains one digit, add 2 to total score
		if (password.matches("(?=.*[0-9]).*"))
			iPasswordScore += 2;

		// if it contains one lower case letter, add 2 to total score
		if (password.matches("(?=.*[a-z]).*"))
			iPasswordScore += 2;

		// if it contains one upper case letter, add 2 to total score
		if (password.matches("(?=.*[A-Z]).*"))
			iPasswordScore += 2;

		// if it contains one special character, add 2 to total score
		if (password.matches("(?=.*[~!@#$%^&*()_-]).*"))
			iPasswordScore += 2;

		return iPasswordScore;

	}

}
