package de.johannes.sonarqube.checkstyle.rules.checks;

import static java.util.Map.entry;
import static java.util.Objects.isNull;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.sonar.check.Rule;
import org.sonar.check.RuleProperty;
import org.sonar.plugins.java.api.IssuableSubscriptionVisitor;
import org.sonar.plugins.java.api.tree.ExpressionTree;
import org.sonar.plugins.java.api.tree.ForStatementTree;
import org.sonar.plugins.java.api.tree.LabeledStatementTree;
import org.sonar.plugins.java.api.tree.ListTree;
import org.sonar.plugins.java.api.tree.MethodReferenceTree;
import org.sonar.plugins.java.api.tree.StatementTree;
import org.sonar.plugins.java.api.tree.SyntaxToken;
import org.sonar.plugins.java.api.tree.Tree;
import org.sonar.plugins.java.api.tree.Tree.Kind;
import org.sonar.plugins.java.api.tree.UnaryExpressionTree;

/**
 * This SonarQube rule implements Checkstyle's <a href=
 * "https://checkstyle.sourceforge.io/checks/whitespace/nowhitespacebefore.html">NoWhitespaceBefore</a>
 * check.
 */
@Rule(key = "NoWhitespaceBeforeCheck", description = "Checks that there is no whitespace before a token. More specifically, it checks that it is not preceded with whitespace, or (if line breaks are allowed) all characters on the line before are whitespace.")
public class NoWhitespaceBeforeCheck extends IssuableSubscriptionVisitor {

	/** RegEx to check preceding line breaks. */
	protected static final String REGEX_PRECEDING_LINE_BREAK = "\\R\\s*";
	/** RegEx to check if there is a preceding annotation. */
	protected static final String REGEX_PRECEDING_ANNOTATION = ".*@\\w*(\\s*)?";
	/** RegEx to check preceding whitespace */
	protected static final String REGEX_PRECEDING_WHITESPACE = "\\s";

	@RuleProperty(key = "allowLineBreaks", description = "Allow line breaks", defaultValue = "true")
	private boolean allowLineBreaks = true;
	@RuleProperty(key = "comma", description = "Comma ','", defaultValue = "true")
	private boolean commaDefault = true;
	@RuleProperty(key = "semicolon", description = "Semicolon ';", defaultValue = "true")
	private boolean semicolonDefault = true;
	@RuleProperty(key = "dot", description = "Dot '.'", defaultValue = "false")
	private boolean dotDefault = true;
	@RuleProperty(key = "ellipsis", description = "Ellipsis '...'", defaultValue = "true")
	private boolean ellipsisDefault = true;
	@RuleProperty(key = "genericStart", description = "Generic Start '<'", defaultValue = "false")
	private boolean genericStartDefault = true;
	@RuleProperty(key = "genericEnd", description = "Generic End '>'", defaultValue = "false")
	private boolean genericEndDefault = true;
	@RuleProperty(key = "labeledStatement", description = "Labeled Statement ':'", defaultValue = "false")
	private boolean labeledStatementDefault = true;
	@RuleProperty(key = "methodReference", description = "Java 8 method reference '::'", defaultValue = "true")
	private boolean methodReferenceDefault = true;
	@RuleProperty(key = "postfixDecrement", description = "Postfix Decrement 'i--'", defaultValue = "true")
	private boolean postfixDecrementDefault = true;
	@RuleProperty(key = "postfixIncrement", description = "Postfix Increment 'i++'", defaultValue = "true")
	private boolean postfixIncrementDefault = true;

	private final String lineSeperator = System.lineSeparator();

	@Override
	public List<Kind> nodesToVisit() {
		return Arrays.asList(Kind.TOKEN,
				Kind.POSTFIX_DECREMENT,
				Kind.POSTFIX_INCREMENT,
				Kind.LABELED_STATEMENT,
				Kind.METHOD_REFERENCE);
	}

	/*
	 * Handling , ; ... < >
	 */
	@Override
	public void visitToken(SyntaxToken pSyntaxToken) {
		final SyntaxToken tokenUnderTest = getTokenUnderTest(pSyntaxToken);

		if (tokenUnderTest != null) {
			final Boolean considerToken = createTokenMap().get(tokenUnderTest.text());

			if (hasWhitespaceBefore(tokenUnderTest) && Boolean.TRUE.equals(considerToken)) {
				reportIssue(pSyntaxToken.parent(),
						String.format("'%s' is preceded by whitespace.", tokenUnderTest.text()));
			}
		}
	}

	/*
	 * Handling -- ++ : ::
	 */
	@Override
	public void visitNode(Tree pTree) {
		final SyntaxToken tokenUnderTest = getTokenUnderTest(pTree);

		if (tokenUnderTest != null) {
			final Boolean considerToken = createKindMap().get(pTree.kind());

			if (hasWhitespaceBefore(tokenUnderTest) && Boolean.TRUE.equals(considerToken)) {
				reportIssue(tokenUnderTest.parent(),
						String.format("'%s' is preceded by whitespace.", tokenUnderTest.text()));
			}
		}
	}

	/**
	 * Method to create Token Map at runtime to make user's SonarQube
	 * customizations of public values take effect.
	 * 
	 * @return token map.
	 */
	private Map<String, Boolean> createTokenMap() {
		return Map.ofEntries(entry(".", dotDefault),
				entry("...", ellipsisDefault),
				entry(",", commaDefault),
				entry(";", semicolonDefault),
				entry("<", genericStartDefault),
				entry(">", genericEndDefault));
	}

	/**
	 * Method to create KindMap at runtime to make user's SonarQube
	 * customizations of public values take effect.
	 * 
	 * @return kind map.
	 */
	private Map<Kind, Boolean> createKindMap() {
		return Map.ofEntries(entry(Kind.POSTFIX_DECREMENT, postfixDecrementDefault),
				entry(Kind.POSTFIX_INCREMENT, postfixIncrementDefault),
				entry(Kind.LABELED_STATEMENT, labeledStatementDefault),
				entry(Kind.METHOD_REFERENCE, methodReferenceDefault));
	}

	private SyntaxToken getTokenUnderTest(SyntaxToken pSyntaxToken) {
		SyntaxToken tokenUnderTest = null;
		if (hasTokenCharacters(pSyntaxToken, ".", "...", ",", ";") || isGeneric(pSyntaxToken)) {
			if (!isInEmptyForInitializerOrCondition(pSyntaxToken)) {
				tokenUnderTest = pSyntaxToken;
			}
		}
		return tokenUnderTest;
	}

	private boolean isGeneric(SyntaxToken pSyntaxToken) {
		final boolean hasGenericCharacter = hasTokenCharacters(pSyntaxToken, "<", ">");
		final Kind parentKind = pSyntaxToken.parent().kind();
		final boolean hasValidParent = (parentKind == Kind.TYPE_ARGUMENTS) || (parentKind == Kind.TYPE_PARAMETER)
				|| (parentKind == Kind.TYPE_PARAMETERS);

		return hasGenericCharacter && hasValidParent;
	}

	private boolean hasTokenCharacters(SyntaxToken pSyntaxToken, String... pTokenCharacters) {
		for (final String tokenCharacter : pTokenCharacters) {
			if (pSyntaxToken.text().equals(tokenCharacter)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Checks that semicolon token is in an empty For Loop initializer or an
	 * empty For Loop condition.
	 * 
	 * @param pSyntaxToken
	 *            semicolon SyntaxToken
	 * @return <code>true</code> if semicolon is an empty For Loop initializer
	 *         or an empty For Loop condition.
	 */
	private boolean isInEmptyForInitializerOrCondition(SyntaxToken pSyntaxToken) {
		boolean result = false;
		if (hasTokenCharacters(pSyntaxToken, ";")) {
			if (pSyntaxToken.parent().is(Kind.FOR_STATEMENT)) {
				final ForStatementTree forStatementTree = (ForStatementTree) pSyntaxToken.parent();
				final ListTree<StatementTree> initializer = forStatementTree.initializer();
				final ExpressionTree condition = forStatementTree.condition();
				final ListTree<StatementTree> update = forStatementTree.update();
				result = (initializer.size() == 0) && isNull(condition) && (update.size() == 0);
			}
		}

		return result;
	}

	/**
	 * Get the token to check for preceding whitespace.
	 * 
	 * @param pTree
	 *            the Tree in which the SyntaxToken is searched in.
	 * @return the searched SyntaxToken, <code>null</code> if the SyntaxToken is
	 *         not found.
	 */
	private SyntaxToken getTokenUnderTest(Tree pTree) {
		SyntaxToken tokenUnderTest = null;

		if (pTree.is(Kind.METHOD_REFERENCE)) {
			tokenUnderTest = ((MethodReferenceTree) pTree).doubleColon();

		} else if (pTree.is(Kind.LABELED_STATEMENT)) {
			tokenUnderTest = ((LabeledStatementTree) pTree).colonToken();

		} else if (pTree.is(Kind.POSTFIX_DECREMENT, Kind.POSTFIX_INCREMENT)) {
			tokenUnderTest = ((UnaryExpressionTree) pTree).operatorToken();

		}

		return tokenUnderTest;
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

		String codeText = context.getFileLines().stream().collect(Collectors.joining(lineSeperator));

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

		String codeText = context.getFileLines().stream().collect(Collectors.joining(lineSeperator));

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
}
