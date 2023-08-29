package de.johannes.sonarqube.checkstyle.rules.checks;

import static java.util.Map.entry;
import static java.util.Objects.isNull;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.sonar.check.Rule;
import org.sonar.check.RuleProperty;
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
public class NoWhitespaceBeforeCheck extends NoWhitespaceCheck {

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
		super.visitToken(pSyntaxToken);

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
		super.visitNode(pTree);

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
}
