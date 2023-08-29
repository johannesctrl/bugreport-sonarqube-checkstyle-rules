package de.johannes.sonarqube.checkstyle.rules.checks;

import static java.util.Map.entry;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.sonar.check.Rule;
import org.sonar.check.RuleProperty;
import org.sonar.plugins.java.api.tree.AnnotationTree;
import org.sonar.plugins.java.api.tree.ArrayAccessExpressionTree;
import org.sonar.plugins.java.api.tree.ArrayTypeTree;
import org.sonar.plugins.java.api.tree.MemberSelectExpressionTree;
import org.sonar.plugins.java.api.tree.MethodReferenceTree;
import org.sonar.plugins.java.api.tree.NewArrayTree;
import org.sonar.plugins.java.api.tree.SynchronizedStatementTree;
import org.sonar.plugins.java.api.tree.SyntaxToken;
import org.sonar.plugins.java.api.tree.Tree;
import org.sonar.plugins.java.api.tree.Tree.Kind;
import org.sonar.plugins.java.api.tree.TypeCastTree;
import org.sonar.plugins.java.api.tree.UnaryExpressionTree;

/**
 * This SonarQube rule implements Checkstyle's <a href=
 * "https://checkstyle.sourceforge.io/checks/whitespace/nowhitespaceafter.html">NoWhitespaceAfter</a>
 * check.
 */
@Rule(key = "NoWhitespaceAfterCheck", description = "Checks that there is no whitespace after a token. More specifically, it checks that it is not followed by whitespace, or (if line breaks are allowed) all characters on the line after are whitespace. To forbid lineBreaks after a token, set property allowLineBreaks to false.")
public class NoWhitespaceAfterCheck extends NoWhitespaceCheck {

	@RuleProperty(key = "annotation", description = "Annotation '@'", defaultValue = "true")
	private boolean annotationDefault = true;
	@RuleProperty(key = "arrayAccessExpression", description = "Array Access Expression'[i]'", defaultValue = "true")
	private boolean arrayAccessExpressionDefault = true;
	@RuleProperty(key = "arrayType", description = "Array Type '[]'", defaultValue = "true")
	private boolean arrayTypeDefault = true;
	@RuleProperty(key = "bitwiseComplement", description = "Bitwise Complement '~'", defaultValue = "true")
	private boolean bitwiseComplementDefault = true;
	@RuleProperty(key = "logicalComplement", description = "Logical Complement '!'", defaultValue = "true")
	private boolean logicalComplementDefault = true;
	@RuleProperty(key = "memberSelect", description = "Member Select '.'", defaultValue = "true")
	private boolean memberSelectDefault = true;
	@RuleProperty(key = "methodReference", description = "Java 8 method reference '::'", defaultValue = "false")
	private boolean methodReferenceDefault = true;
	@RuleProperty(key = "newArray", description = "New Array '[]'", defaultValue = "true")
	private boolean newArrayDefault = true;
	@RuleProperty(key = "prefixDecrement", description = "Prefix Decrement '--i'", defaultValue = "true")
	private boolean prefixDecrementDefault = true;
	@RuleProperty(key = "prefixIncrement", description = "Prefix Increment '++i'", defaultValue = "true")
	private boolean prefixIncrementDefault = true;
	@RuleProperty(key = "synchronizedStatement", description = "Keyword 'synchronized'", defaultValue = "false")
	private boolean synchronizedStatementDefault = true;
	@RuleProperty(key = "typeCast", description = "Type Cast", defaultValue = "false")
	private boolean typeCastDefault = true;
	@RuleProperty(key = "unaryMinus", description = "Unary Minus '-i'", defaultValue = "true")
	private boolean unaryMinusDefault = true;
	@RuleProperty(key = "unaryPlus", description = "Unary Plus '+i'", defaultValue = "true")
	private boolean unaryPlusDefault = true;

	private final List<Kind> afterKindGroup = Arrays.asList(Kind.ANNOTATION,
			Kind.BITWISE_COMPLEMENT,
			Kind.LOGICAL_COMPLEMENT,
			Kind.MEMBER_SELECT,
			Kind.METHOD_REFERENCE,
			Kind.NEW_ARRAY,
			Kind.PREFIX_DECREMENT,
			Kind.PREFIX_INCREMENT,
			Kind.SYNCHRONIZED_STATEMENT,
			Kind.TYPE_CAST,
			Kind.UNARY_MINUS,
			Kind.UNARY_PLUS);
	private final List<Kind> beforeKindGroup = Arrays.asList(Kind.ARRAY_ACCESS_EXPRESSION, Kind.ARRAY_TYPE);
	private Map<Kind, KindAttributes> kindMap;

	@Override
	public List<Kind> nodesToVisit() {
		final List<Kind> kindsToVisit = new ArrayList<>();
		kindsToVisit.addAll(afterKindGroup);
		kindsToVisit.addAll(beforeKindGroup);

		return kindsToVisit;
	}

	@Override
	public void visitNode(Tree pTree) {
		super.visitNode(pTree);

		kindMap = createKindMap();
		final SyntaxToken tokenUnderTest = getTokenUnderTest(pTree);

		if (tokenUnderTest != null) {
			final boolean hasWhitespace = isInBeforeKindGroup(pTree) ? hasWhitespaceBefore(tokenUnderTest)
					: hasWhitespaceAfter(pTree, tokenUnderTest);
			final boolean considerToken = kindMap.get(pTree.kind()).getConsider();

			if (hasWhitespace && considerToken) {
				reportIssue(pTree,
						String.format("'%s' is followed by whitespace.", kindMap.get(pTree.kind()).getMsgToken()));
			}
		}
	}

	/**
	 * Get the token to check for neighboring whitespace, if e.g. '++'.
	 */
	private SyntaxToken getTokenUnderTest(Tree pTree) {
		SyntaxToken tokenUnderTest = null;

		if (pTree.is(Kind.TYPE_CAST)) {
			tokenUnderTest = ((TypeCastTree) pTree).closeParenToken();

		} else if (pTree.is(Kind.PREFIX_INCREMENT,
				Kind.PREFIX_DECREMENT,
				Kind.POSTFIX_INCREMENT,
				Kind.UNARY_PLUS,
				Kind.UNARY_MINUS,
				Kind.BITWISE_COMPLEMENT,
				Kind.LOGICAL_COMPLEMENT)) {
			tokenUnderTest = ((UnaryExpressionTree) pTree).operatorToken();

		} else if (pTree.is(Kind.ANNOTATION)) {
			tokenUnderTest = ((AnnotationTree) pTree).atToken();

		} else if (pTree.is(Kind.METHOD_REFERENCE)) {
			tokenUnderTest = ((MethodReferenceTree) pTree).doubleColon();

		} else if (pTree.is(Kind.MEMBER_SELECT)) {
			tokenUnderTest = ((MemberSelectExpressionTree) pTree).operatorToken();

		} else if (pTree.is(Kind.NEW_ARRAY)) {
			tokenUnderTest = ((NewArrayTree) pTree).openBraceToken();

		} else if (pTree.is(Kind.SYNCHRONIZED_STATEMENT)) {
			tokenUnderTest = ((SynchronizedStatementTree) pTree).firstToken();

		} else if (pTree.is(Kind.ARRAY_ACCESS_EXPRESSION)) {
			tokenUnderTest = ((ArrayAccessExpressionTree) pTree).dimension().openBracketToken();

		} else if (pTree.is(Kind.ARRAY_TYPE)) {
			final SyntaxToken openBracketToken = ((ArrayTypeTree) pTree).openBracketToken();
			if (!isRegExMatchingLeftSidedCharsOfToken(openBracketToken, REGEX_PRECEDING_ANNOTATION)) {
				tokenUnderTest = openBracketToken;
			}

		} else {
			throw new IllegalStateException("Unexpected tree kind " + pTree.kind());
		}

		return tokenUnderTest;
	}

	private boolean hasWhitespaceAfter(Tree pTree, SyntaxToken pSyntaxToken) {
		boolean result = false;

		final boolean lineBreakAfterToken = isRegExMatchingRightSidedCharsOfToken(pSyntaxToken, "\\R");
		if (!lineBreakAfterToken && !isLineBreakBetweenTypeCastParenthesis(pTree)) {
			result = isRegExMatchingRightSidedCharsOfToken(pSyntaxToken, "\\s");

		} else {
			result = !getAllowLineBreaks();
		}

		return result;
	}

	private boolean isLineBreakBetweenTypeCastParenthesis(Tree pTree) {
		boolean lineBreakBetweenTypeCastParentheses = false;
		if (pTree.is(Kind.TYPE_CAST)) {
			final TypeCastTree typeCastTree = (TypeCastTree) pTree;
			final int openParenLine = typeCastTree.openParenToken().range().start().line();
			final int closeParenLine = typeCastTree.closeParenToken().range().start().line();
			lineBreakBetweenTypeCastParentheses = openParenLine != closeParenLine;
		}
		return lineBreakBetweenTypeCastParentheses;
	}

	/**
	 * Method to create KindMap at runtime to make user's SonarQube
	 * customizations of public values take effect.
	 */
	private Map<Kind, KindAttributes> createKindMap() {
		return Map.ofEntries(
		//@formatter:off
				entry(Kind.ANNOTATION, new KindAttributes("@", annotationDefault)),
				entry(Kind.ARRAY_ACCESS_EXPRESSION, new KindAttributes(Kind.ARRAY_ACCESS_EXPRESSION.name(), arrayAccessExpressionDefault)),
				entry(Kind.ARRAY_TYPE, new KindAttributes(Kind.ARRAY_TYPE.name(), arrayTypeDefault)),
				entry(Kind.BITWISE_COMPLEMENT, new KindAttributes("~", bitwiseComplementDefault)),
				entry(Kind.LOGICAL_COMPLEMENT, new KindAttributes("!", logicalComplementDefault)),
				entry(Kind.MEMBER_SELECT, new KindAttributes(".", memberSelectDefault)),
				entry(Kind.METHOD_REFERENCE, new KindAttributes("::", methodReferenceDefault)),
				entry(Kind.NEW_ARRAY, new KindAttributes("{", newArrayDefault)),
				entry(Kind.PREFIX_DECREMENT, new KindAttributes("--", prefixDecrementDefault)),
				entry(Kind.PREFIX_INCREMENT, new KindAttributes("++", prefixIncrementDefault)),
				entry(Kind.SYNCHRONIZED_STATEMENT, new KindAttributes("synchronized", synchronizedStatementDefault)),
				entry(Kind.TYPE_CAST, new KindAttributes(")", typeCastDefault)),
				entry(Kind.UNARY_MINUS, new KindAttributes("-", unaryMinusDefault)),
				entry(Kind.UNARY_PLUS, new KindAttributes("+", unaryPlusDefault)));
		//@formatter:on
	}

	private boolean isInBeforeKindGroup(Tree pTree) {
		return pTree.is(beforeKindGroup.toArray(new Kind[0]));
	}

	/**
	 * Attributes for Kind.
	 */
	class KindAttributes {
		private final String msgToken;
		private final boolean consider;

		public KindAttributes(String pMsgToken, boolean pConsider) {
			msgToken = pMsgToken;
			consider = pConsider;
		}

		public String getMsgToken() {
			return msgToken;
		}

		public boolean getConsider() {
			return consider;
		}

	}

}
