package de.johannes.sonarqube.checkstyle.rules.checks.whitespace.testfiles;

import java.util.Comparator;
import java.util.function.IntFunction;

import org.checkerframework.checker.nullness.qual.NonNull;

import de.johannes.sonarqube.checkstyle.rules.checks.NoWhitespaceAfterCheck;

/**
 * Test file for {@link NoWhitespaceAfterCheck} when allowLineBreak is set to <code>false</code>.
 */
//@formatter:off
@SuppressWarnings("unused")
public class NoWhitespaceAfterCheck_AllowLineBreaksIsFalse_TestFile {
	
	/**
	 * AT
	 * Kind.ANNOTATION
	 */
	private void annotation() {
		// Compliant@+1
		@Deprecated
		int x;
		
		// Noncompliant@+1 {{'@' is followed by whitespace.}}
		@ Deprecated
		int y;
		
		// Noncompliant@+1 {{'@' is followed by whitespace.}}
		@
		Deprecated
		int z;
	}
	
	/**
	 * INDEX_OP
	 * Kind.ARRAY_ACCESS_EXPRESSION
	 */
	private void indexOperator() {
		int[] array = {1};

		// Compliant@+1
		int b = array[0];
		
		// Noncompliant@+1 {{'ARRAY_ACCESS_EXPRESSION' is followed by whitespace.}}
		int a = array [0];
	}
	private void indexOperatorMulti() {
		int[][] array = {{1}};
		
		// Compliant@+1
		int a = array[0][0];

		// Noncompliant@+1 {{'ARRAY_ACCESS_EXPRESSION' is followed by whitespace.}}
		int b = array [0][0];

		// Noncompliant@+1 {{'ARRAY_ACCESS_EXPRESSION' is followed by whitespace.}}
		int c = array[0] [0];
		
		// Noncompliant@+1 {{'ARRAY_ACCESS_EXPRESSION' is followed by whitespace.}}
		int d = array[0]
				[0];

		// Compliant@+1
		int e = array[0][
		                       0];
	}
	private void indexOperatorSpecial() {
		// Compliant@+1
		int b = new int[][][] {{{0}}}[0][0][0];
		
		// Noncompliant@+1 {{'ARRAY_ACCESS_EXPRESSION' is followed by whitespace.}}
		int a = new int[][][] {{{0}}} [0][0][0];
	}

	/**
	 * ARRAY_DECLERATOR
	 * Kind.ARRAY_TYPE
	 */
	private void arrayType() {
		// Compliant@+1
		int[]a;
		
		// Noncompliant@+1 {{'ARRAY_TYPE' is followed by whitespace.}}
		int []b;

		// Noncompliant@+1 {{'ARRAY_TYPE' is followed by whitespace.}}
		int[] []c;
		
		// Noncompliant@+1 {{'ARRAY_TYPE' is followed by whitespace.}}
		int[]
				[]d;
		// Noncompliant@+1 {{'ARRAY_TYPE' is followed by whitespace.}}
		int[]
[]e;
		// Compliant@+1
		int[
		    ]f;

		// Compliant@+1
		int g[][];
		
		// Noncompliant@+1 {{'ARRAY_TYPE' is followed by whitespace.}}
		int h[] [];
		
		// Noncompliant@+1 {{'ARRAY_TYPE' is followed by whitespace.}}
		int i[]
				[];

		// Compliant@+1
		int[] j[];

		// Noncompliant@+1 {{'ARRAY_TYPE' is followed by whitespace.}}
		int[] k [];

		// Noncompliant@+1 {{'ARRAY_TYPE' is followed by whitespace.}}
		int[][] l[] [];

		// Noncompliant@+1 {{'ARRAY_TYPE' is followed by whitespace.}}
		int[]
				[] m[][];
		
		// Noncompliant@+1 {{'ARRAY_TYPE' is followed by whitespace.}}
		int[][] n[]
				[];

		// Noncompliant@+2 {{'ARRAY_TYPE' is followed by whitespace.}}
		// Noncompliant@+1 {{'ARRAY_TYPE' is followed by whitespace.}}
		int[] [] o[]
				[];

		// Noncompliant@+2 {{'ARRAY_TYPE' is followed by whitespace.}}
		// Noncompliant@+1 {{'ARRAY_TYPE' is followed by whitespace.}}
		int[]
				[] p[]
						[];
		
	}
    private void arrayTypeAnnotation() {
		// Compliant@+1
        String @NonNull [] a;

		// Compliant@+1
        String @NonNull []
        		b;

		// Compliant@+1
        String @NonNull
        	[] c;
	}

    /**
	 * BNOT
	 * Kind.BITWISE_COMPLEMENT
	 */
	private void bitwiseNot(int a) {
		// Compliant@+1
		a = ~a;
		
		// Noncompliant@+1 {{'~' is followed by whitespace.}}
		a = ~ a;
	}
	
	/**
	 * LNOT
	 * Kind.Kind.LOGICAL_COMPLEMENT
	 */
	private void logicalComplement(boolean a) {
		// Compliant@+1
		a = !a;
		
		// Noncompliant@+1 {{'!' is followed by whitespace.}}
		a = ! a;
	}
	
	/**
	 * DOT
	 * Kind.MEMBER_SELECT
	 */
	private void dot(String s) {
		// Compliant@+1
		Integer.parseInt(s);
		
		// Compliant@+1
		Integer .parseInt(s);
		
		// Compliant@+1
		Integer
			.parseInt(s);
		
		// Noncompliant@+1 {{'.' is followed by whitespace.}}
		Integer. parseInt(s);

		// Noncompliant@+1 {{'.' is followed by whitespace.}}
		Integer.
			parseInt(s);
	}
	
	/**
	 * METHOD_REF
	 * Kind.METHOD_REFERENCE
	 * (disabled by default)
	 */
	private void methodReference() {
		// Compliant@+1
		Comparator<String> a = String::compareToIgnoreCase;

		// Compliant@+1
		Comparator<String> b = String ::compareToIgnoreCase;

		// Compliant@+1
		Comparator<String> c = String
				::compareToIgnoreCase;
		
		// Noncompliant@+1 {{'::' is followed by whitespace.}}
		Comparator<String> d = String:: compareToIgnoreCase;
		
		// Noncompliant@+1 {{'::' is followed by whitespace.}}
		Comparator<String> e = String::
			compareToIgnoreCase;
		
		// Noncompliant@+1 {{'::' is followed by whitespace.}}
		IntFunction<int[]> f = int[]:: new;

		// Compliant@+1
		IntFunction<int[]> g = int[]::new;

		// Compliant@+1
		IntFunction<int[]> h = int[
		                         
		                           ]::new;
	}
	
	/**
	 * ARRAY_INIT
	 * Kind.NEW_ARRAY
	 */
	private void arrayInit() {
		// Compliant@+1
		int[] a = {1, 2, 3};
		
		// Compliant@+1
		int[] b = {1 , 2, 3};
		
		// Noncompliant@+1 {{'{' is followed by whitespace.}}
		int[] c = { 1};
		
		// Noncompliant@+1 {{'{' is followed by whitespace.}}
		int[] d = {
				1, 2, 3
		};
	}
	
	/**
	 * DEC
	 * Kind.PREFIX_DECREMENT
	 */
	private void decrementPrefix(int a) {
		// Compliant@+1
		a = --a;
		
		// Noncompliant@+1 {{'--' is followed by whitespace.}}
		a = -- a;
		
		// Noncompliant@+1 {{'--' is followed by whitespace.}}
		a = --
				a;
	}

	/**
	 * INC
	 * Kind.PREFIX_INCREMENT
	 */
	private void incrementPrefix(int a) {
		// Compliant@+1
		a = ++a;
		
		// Noncompliant@+1 {{'++' is followed by whitespace.}}
		a = ++ a;
		
		// Noncompliant@+1 {{'++' is followed by whitespace.}}
		a = ++
				a;
	}
	
	/**
	 * LITERAL_SYNCHRONIZED
	 * Kind.SYNCHRONIZED_STATEMENT
	 * (disabled by default)
	 */
	private void synchronizedLiteral() {
		// Compliant@+1
		synchronized(this) {};
		
		// Noncompliant@+1 {{'synchronized' is followed by whitespace.}
		synchronized (this) {};

		// Noncompliant@+1 {{'synchronized' is followed by whitespace.}
		synchronized
			(this) {};

		// Compliant@+1
		synchronized(
				this
				)
			{
			};
	}
	
	/**
	 * TYPECAST
	 * Kind.TYPE_CAST
	 * (disabled by default)
	 */
	private void typecast(int a) {
		// Compliant@+1
		float f1 = 1+(float)a;

		// Compliant@+1
		float f2 = 1+( float )a;

		// Noncompliant@+1 {{')' is followed by whitespace.}}
		float f3 = 1+(float) a;
		
		// Noncompliant@+1 {{')' is followed by whitespace.}}
		float f4 = 1+(
				float)a;

		// Noncompliant@+1 {{')' is followed by whitespace.}}
		float f5 = 1+(float
				)a;

		// Noncompliant@+1 {{')' is followed by whitespace.}}
		float f6 = 1+(float)
				a;
	}
	
	/**
	 * UNARY_PLUS
	 * Kind.UNARY_PLUS
	 */
	private void plus(int a) {
		// Compliant@+1
		a = +a;

		// Noncompliant@+1 {{'+' is followed by whitespace.}}
		a = + a;
		
		// Noncompliant@+1 {{'+' is followed by whitespace.}}
		a = +
				a;
	}
	
	/**
	 * UNARY_MINUS
	 * Kind.UNARY_MINUS
	 */
	private void minus(int a) {
		// Compliant@+1
		a = -a;
		
		// Noncompliant@+1 {{'-' is followed by whitespace.}}
		a = - a;
	}
}
