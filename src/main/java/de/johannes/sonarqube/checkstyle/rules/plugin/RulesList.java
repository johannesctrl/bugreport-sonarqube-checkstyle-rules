package de.johannes.sonarqube.checkstyle.rules.plugin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.sonar.plugins.java.api.JavaCheck;

import de.johannes.sonarqube.checkstyle.rules.checks.NoWhitespaceAfterCheck;
import de.johannes.sonarqube.checkstyle.rules.checks.NoWhitespaceBeforeCheck;

/**
 * List of registered Sonarqube Checkstyle rules.
 *
 */
public final class RulesList {

	private RulesList() {
	}

	/**
	 * Gets the checks to be registered.
	 */
	public static List<Class<? extends JavaCheck>> getChecks() {
		final List<Class<? extends JavaCheck>> checks = new ArrayList<>();
		checks.addAll(getJavaChecks());
		checks.addAll(getJavaTestChecks());
		return Collections.unmodifiableList(checks);
	}

	public static List<Class<? extends JavaCheck>> getJavaChecks() {
		return List.of(NoWhitespaceAfterCheck.class, NoWhitespaceBeforeCheck.class);
	}

	public static List<Class<? extends JavaCheck>> getJavaTestChecks() {
		return Collections.emptyList();
	}
}
