package de.johannes.sonarqube.checkstyle.rules.checks;

import static java.util.Objects.isNull;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.sonar.check.RuleProperty;
import org.sonar.plugins.java.api.IssuableSubscriptionVisitor;
import org.sonar.plugins.java.api.tree.SyntaxToken;
import org.sonar.plugins.java.api.tree.Tree;

/**
 * Abstract class for {@link NoWhitespaceAfterCheck} and
 * {@link NoWhitespaceBeforeCheck}.
 */
public abstract class NoWhitespaceCheck extends IssuableSubscriptionVisitor {

	/** RegEx to check preceding line breaks. */
	protected static final String REGEX_PRECEDING_LINE_BREAK = "\\R\\s*";
	/** RegEx to check if there is a preceding annotation. */
	protected static final String REGEX_PRECEDING_ANNOTATION = ".*@\\w*(\\s*)?";
	/** RegEx to check preceding whitespace */
	protected static final String REGEX_PRECEDING_WHITESPACE = "\\s";

	@RuleProperty(key = "allowLineBreaks", description = "Allow line breaks", defaultValue = "true")
	private boolean allowLineBreaks = true;
	
	private final String lineSeperator = System.lineSeparator();
	private String codeText;

	@Override
	public void visitNode(Tree pTree) {
		initCodeText();
		super.visitNode(pTree);
	}

	@Override
	public void visitToken(SyntaxToken pSyntaxToken) {
		initCodeText();
		super.visitToken(pSyntaxToken);
	}

	private void initCodeText() {
		if (isNull(codeText)) {
			codeText = context.getFileLines().stream().collect(Collectors.joining(lineSeperator));
		}
	}

	/**
	 * Checks if a preceding whitespace exists before a token.
	 *
	 * @param pSyntaxToken
	 *            the token to check if it has a preceding whitespace.
	 * @return <code>true</code>, if a preceding whitespace exists before the
	 *         given token.
	 */
	protected boolean hasWhitespaceBefore(SyntaxToken pSyntaxToken) {
		boolean result = false;

		final boolean lineBreak = isRegExMatchingLeftSidedCharsOfToken(pSyntaxToken, REGEX_PRECEDING_LINE_BREAK);
		if (!lineBreak) {
			final boolean whitespace = isRegExMatchingLeftSidedCharsOfToken(pSyntaxToken, REGEX_PRECEDING_WHITESPACE);
			result = whitespace;

		} else {
			result = !allowLineBreaks;
		}

		return result;
	}

	/**
	 * Checks if a given RegEx matches the left sided char set of a given token.
	 * The RegEx will be applied to a String that starts at the beginning of the
	 * code until the left neighbor of the given token.
	 *
	 * @param pSyntaxToken
	 *            the token whose left sided characters are to check.
	 * @param pRegEx
	 *            the RegEx to apply.
	 * @return <code>true</code> if the RegEx matches.
	 */
	protected boolean isRegExMatchingLeftSidedCharsOfToken(SyntaxToken pSyntaxToken, String pRegEx) {
		final int tokenColumnIndex = pSyntaxToken.range().start().column() - 1;
		final int tokenLineIndex = pSyntaxToken.range().start().line() - 1;

		final int charIndex = findCharIndex(codeText, tokenLineIndex, tokenColumnIndex);
		final int charIndexLeftNeighbor = charIndex - 1;
		final String subString = codeText.substring(0, charIndexLeftNeighbor + 1);

		final String regex = pRegEx + "\\z";
		final Pattern pattern = Pattern.compile(regex, Pattern.DOTALL);
		final Matcher matcher = pattern.matcher(subString);

		return matcher.find();
	}

	/**
	 * Checks if a given RegEx matches the right sided char set of a given
	 * token. The RegEx will be applied to a String that starts at the beginning
	 * of the token until the end of the code.
	 *
	 * @param pSyntaxToken
	 *            the token whose right sided characters are to check.
	 * @param pRegEx
	 *            the RegEx to apply.
	 * @return <code>true</code> if the RegEx matches.
	 */
	protected boolean isRegExMatchingRightSidedCharsOfToken(SyntaxToken pSyntaxToken, String pRegEx) {
		final int tokenColumnIndex = pSyntaxToken.range().end().column() - 1;
		final int tokenLineIndex = pSyntaxToken.range().end().line() - 1;

		final int charIndex = findCharIndex(codeText, tokenLineIndex, tokenColumnIndex);
		final String subString = codeText.substring(charIndex, codeText.length());

		final String regex = "\\A" + pRegEx;
		final Pattern pattern = Pattern.compile(regex, Pattern.DOTALL);
		final Matcher matcher = pattern.matcher(subString);

		return matcher.find();
	}

	/**
	 * Finds the index (0-based) of a given token in a code text.
	 * 
	 * @param pCodeText
	 *            the code text to find the token in.
	 * @param pLine
	 *            the line of the token (0-based).
	 * @pColumn the column of the token (0-based).
	 * @return the index of a character in a text.
	 */
	private int findCharIndex(String pCodeText, int pLine, int pColumn) {
		final String[] lines = pCodeText.split(lineSeperator);

		int codePointIndex = 0;
		for (int i = 0; i < pLine; i++) {
			codePointIndex += lines[i].length() + lineSeperator.length();
		}
		codePointIndex += pColumn;

		return codePointIndex;
	}

	/**
	 * Setter to control whether whitespace is allowed if the token is at a line
	 * break.
	 *
	 * @param pAllowLineBreaks
	 *            whether whitespace should be flagged at line breaks.
	 */
	public void setAllowLineBreaks(boolean pAllowLineBreaks) {
		allowLineBreaks = pAllowLineBreaks;
	}

	protected boolean getAllowLineBreaks() {
		return allowLineBreaks;
	}

	protected String getCodeText() {
		return codeText;
	}
}
