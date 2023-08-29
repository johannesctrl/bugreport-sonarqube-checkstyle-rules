package de.johannes.sonarqube.checkstyle.rules.plugin;

import org.sonar.api.Plugin;

/**
 * Entry point of your plugin containing your custom rules. See also <a href=
 * "https://github.com/SonarSource/sonar-java/tree/master/docs/java-custom-rules-example">Example
 * Project</a>
 */
public class JavaRulesPlugin implements Plugin {

	@Override
	public void define(final Context pContext) {
		// server extensions -> objects are instantiated during server startup
		pContext.addExtension(JavaRulesDefinition.class);

		// batch extensions -> objects are instantiated during code analysis
		pContext.addExtension(JavaFileCheckRegistrar.class);
	}

}
