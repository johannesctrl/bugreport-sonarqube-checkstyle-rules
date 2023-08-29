package de.johannes.sonarqube.checkstyle.rules.plugin;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.sonar.plugins.java.api.CheckRegistrar;

/**
 * 
 * Test for {@link JavaFileCheckRegistrar}.
 *
 */
class TestFileCheckRegistrar {

	@Test
	void checkNumberRules() {
		final CheckRegistrar.RegistrarContext context = new CheckRegistrar.RegistrarContext();

		final JavaFileCheckRegistrar registrar = new JavaFileCheckRegistrar();
		registrar.register(context);

		assertThat(context.checkClasses()).hasSize(2);
		assertThat(context.testCheckClasses()).hasSize(0);
	}

}