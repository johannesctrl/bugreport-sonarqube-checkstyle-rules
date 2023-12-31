package de.johannes.sonarqube.checkstyle.rules.plugin;

import org.sonar.plugins.java.api.CheckRegistrar;
import org.sonar.plugins.java.api.JavaCheck;
import org.sonarsource.api.sonarlint.SonarLintSide;

import java.util.List;

/**
 * Provide the "checks" (implementations of rules) classes that are going be executed during
 * source code analysis.
 * <p>
 * This class is a batch extension by implementing the {@link org.sonar.plugins.java.api.CheckRegistrar} interface.
 */
@SonarLintSide
public class JavaFileCheckRegistrar implements CheckRegistrar {

    /**
     * Register the classes that will be used to instantiate checks during analysis.
     */
    @Override
    public void register(final RegistrarContext pRegistrarContext) {
        // Call to registerClassesForRepository to associate the classes with the correct repository key
        pRegistrarContext.registerClassesForRepository(JavaRulesDefinition.REPOSITORY_KEY, checkClasses(), testCheckClasses());
    }

    /**
     * Lists all the main checks provided by the plugin
     */
    public static List<Class<? extends JavaCheck>> checkClasses() {
        return RulesList.getJavaChecks();
    }

    /**
     * Lists all the test checks provided by the plugin
     */
    public static List<Class<? extends JavaCheck>> testCheckClasses() {
        return RulesList.getJavaTestChecks();
    }
}
