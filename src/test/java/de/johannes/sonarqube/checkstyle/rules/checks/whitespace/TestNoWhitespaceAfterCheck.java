package de.johannes.sonarqube.checkstyle.rules.checks.whitespace;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.sonar.java.checks.verifier.CheckVerifier;

import de.johannes.sonarqube.checkstyle.rules.checks.NoWhitespaceAfterCheck;

/**
 * Tests {@link NoWhitespaceAfterCheck}.
 */
public class TestNoWhitespaceAfterCheck {

	@ParameterizedTest(name = "allowLineBreaks: {arguments}")
	@ValueSource(booleans = { true, false })
	void testNoWhitespaceAfterCheck(boolean pAllowLineBreaks) {
		final NoWhitespaceAfterCheck noWhitespaceAfterCheck = new NoWhitespaceAfterCheck();
		noWhitespaceAfterCheck.setAllowLineBreaks(pAllowLineBreaks);

		final String testFilePath = pAllowLineBreaks
				? "src/test/java/de/johannes/sonarqube/checkstyle/rules/checks/whitespace/testfiles/NoWhitespaceAfterCheck_AllowLineBreaksIsTrue_TestFile.java"
				: "src/test/java/de/johannes/sonarqube/checkstyle/rules/checks/whitespace/testfiles/NoWhitespaceAfterCheck_AllowLineBreaksIsFalse_TestFile.java";

		CheckVerifier.newVerifier().onFile(testFilePath).withCheck(noWhitespaceAfterCheck).verifyIssues();
	}
}
