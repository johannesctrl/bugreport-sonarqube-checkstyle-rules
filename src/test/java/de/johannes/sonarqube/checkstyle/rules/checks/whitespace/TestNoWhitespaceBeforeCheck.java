package de.johannes.sonarqube.checkstyle.rules.checks.whitespace;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.sonar.java.checks.verifier.CheckVerifier;

import de.johannes.sonarqube.checkstyle.rules.checks.NoWhitespaceBeforeCheck;

/**
 * Tests {@link NoWhitespaceBeforeCheck}.
 */
public class TestNoWhitespaceBeforeCheck {

	@ParameterizedTest(name = "allowLineBreaks: {arguments}")
	@ValueSource(booleans = { true, false })
	void testNoWhitespaceBeforeCheck(boolean pAllowLineBreaks) {
		final NoWhitespaceBeforeCheck noWhitespaceBeforeCheck = new NoWhitespaceBeforeCheck();
		noWhitespaceBeforeCheck.setAllowLineBreaks(pAllowLineBreaks);

		final String testFilePath = pAllowLineBreaks
				? "src/test/java/de/johannes/sonarqube/checkstyle/rules/checks/whitespace/testfiles/NoWhitespaceBeforeCheck_AllowLineBreaksTrue_TestFile.java"
				: "src/test/java/de/johannes/sonarqube/checkstyle/rules/checks/whitespace/testfiles/NoWhitespaceBeforeCheck_AllowLineBreaksFalse_TestFile.java";

		CheckVerifier.newVerifier().onFile(testFilePath).withCheck(noWhitespaceBeforeCheck).verifyIssues();
	}

}
