package de.johannes.sonarqube.checkstyle.rules.checks.whitespace.testfiles;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.function.IntFunction;

import de.johannes.sonarqube.checkstyle.rules.checks.NoWhitespaceAfterCheck;

/**
 * Test file for {@link NoWhitespaceAfterCheck}.
 */
//@formatter:off
public class NoWhitespaceBeforeCheck_AllowLineBreaksFalse_TestFile {
	
	/**
	 * GENERIC_START
	 * '<'
	 */
	// Compliant@+1
	class GenericStartTypeParameter1<A, B> {}

	// Noncompliant@+1 {{'<' is preceded by whitespace.}}
	class GenericStartTypeParameter2 <A, B> {}
	
	// Noncompliant@+2 {{'<' is preceded by whitespace.}}
	class GenericStartTypeParameter3
		<A, B> {}

	public void genericStartTypeArguments() {
		// Compliant@+1
		Map<String, Integer> map1 = new HashMap<>();
		
		// Noncompliant@+1 {{'<' is preceded by whitespace.}}
		Map <String, Integer> map2 = new HashMap<>();
	}
	
	/**
	 * GENERIC_END
	 * '>'
	 */
	// Compliant@+1
	class GenericEndTypeParameter1<A, B> {}

	// Noncompliant@+1 {{'>' is preceded by whitespace.}}
	class GenericEndTypeParameter2<A, B > {}
	
	// Noncompliant@+1 {{'>' is preceded by whitespace.}}
	class GenericEndTypeParameter3<A, B
		> {}

	public void genericEndTypeArguments() {
		// Compliant@+1
		Map<String, Integer> map1 = new HashMap<>();
		
		// Noncompliant@+1 {{'>' is preceded by whitespace.}}
		Map<String, Integer > map2 = new HashMap<>();
	}
	
	/**
	 * ELLIPSIS
	 * Kind.METHOD
	 */
	// Compliant@+1
	public void ellipsis(int... a) {
	}
	
	// Noncompliant@+1 {{'...' is preceded by whitespace.}}
	public void ellipsis2(int ... a) {
	}
	
	// Noncompliant@+1 {{'...' is preceded by whitespace.}}
	public void ellipsis3(int
			... a) {
	}

	/**
	 * DOT
	 * '.'
	 */
	private void dot(String s) {
		// Compliant@+1
		Integer.parseInt(s);

		// Noncompliant@+1 {{'.' is preceded by whitespace.}}
		Integer .parseInt(s);

		// Noncompliant@+1 {{'.' is preceded by whitespace.}}
		Integer
			.parseInt(s);
	}
	
	/**
	 * SEMI
	 * ';'
	 */
	private void semicolon() {
		// Compliant@+1
		int a;
		
		// Noncompliant@+1 {{';' is preceded by whitespace.}}
		int b ;
	}
	private void forCondition() {
		// Compliant@+1
		for (int i = 0; i < 10; i++) {
			
		}
	}
	private void emptyForCondition() {
		// Compliant@+1
		for ( ; 
				; ) {
		}
	}
	private void emptyForInit() {
		for (int i = 0; i < 10; i++) {
		}
	}
	// Noncompliant@+1 {{';' is preceded by whitespace.}}
	;
	
	/**
	 * METHOD_REF
	 * Kind.METHOD_REFERENCE
	 */
	private void methodReference() {
		// Compliant@+1
		Comparator<String> a = String::compareToIgnoreCase;

		// Noncompliant@+1 {{'::' is preceded by whitespace.}}}
		Comparator<String> b = String ::compareToIgnoreCase;

		// Noncompliant@+1 {{'::' is preceded by whitespace.}}}
		Comparator<String> c = String
				::compareToIgnoreCase;
		
		// Noncompliant@+1 {{'::' is preceded by whitespace.}}}
		IntFunction<int[]> f = int[] :: new;
		
		// Compliant@+1
		Comparator<String> d = String:: compareToIgnoreCase;

		// Compliant@+1
		Comparator<String> e = String::
			compareToIgnoreCase;
	}
	
	/**
	 * LABELED_STAT
	 * Kind.LABELED_STATEMENT
	 */
	private void labeledStat() {
		// Compliant@+1
		outer: while (true) {
			continue outer;
		}
	}
	private void labeledStat2() {
		// Noncompliant@+1 {{':' is preceded by whitespace.}}}
		outer : while (true) {
			continue outer;
		}
	}
	
	/**
	 * DEC
	 * Kind.POSTFIX_DECREMENT
	 */
	private void decrementPostfix(int a) {
		// Compliant@+1
		a = a--;
		
		// Noncompliant@+1 {{'--' is preceded by whitespace.}}}
		a = a --;
	}

	/**
	 * INC
	 * Kind.POSTFIX_INCREMENT
	 */
	private void incrementPostfix(int a) {
		// Compliant@+1
		int i = a++;
		
		// Noncompliant@+1 {{'++' is preceded by whitespace.}}}
		int j = a ++;

		// Noncompliant@+1 {{'++' is preceded by whitespace.}}}
		int k = a
				++;
	}
}
