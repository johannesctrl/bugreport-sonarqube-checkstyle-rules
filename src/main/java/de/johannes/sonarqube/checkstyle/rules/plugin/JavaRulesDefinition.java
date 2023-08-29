package de.johannes.sonarqube.checkstyle.rules.plugin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;

import org.sonar.api.SonarRuntime;
import org.sonar.api.server.rule.RulesDefinition;
import org.sonarsource.analyzer.commons.RuleMetadataLoader;

/**
 * Declare rule metadata in server repository of rules. That allows to list the
 * rules in the page "Rules".
 */
public class JavaRulesDefinition implements RulesDefinition {
	/**
	 * The repository key.
	 */
	public static final String REPOSITORY_KEY = "sonarqube-checkstyle-rules";

	/**
	 * The repository name.
	 */
	public static final String REPOSITORY_NAME = "Johannes Sonarqube Checkstyle Rules";

	// don't change that because the path is hard coded in CheckVerifier
	private static final String RESOURCE_BASE_PATH = "/org/sonar/l10n/java/rules/java";

	// Add the rule keys of the rules which need to be considered as
	// template-rules
	private static final Set<String> RULE_TEMPLATES_KEY = Collections.emptySet();

	private final SonarRuntime runtime;

	public JavaRulesDefinition(SonarRuntime pRuntime) {
		this.runtime = pRuntime;
	}

	@Override
	public void define(Context pContext) {
		final NewRepository repository = pContext.createRepository(REPOSITORY_KEY, "java").setName(REPOSITORY_NAME);
		final RuleMetadataLoader ruleMetadataLoader = new RuleMetadataLoader(RESOURCE_BASE_PATH, runtime);
		ruleMetadataLoader.addRulesByAnnotatedClass(repository, new ArrayList<>(RulesList.getChecks()));
		setTemplates(repository);
		repository.done();
	}

	private static void setTemplates(NewRepository pRepository) {
		RULE_TEMPLATES_KEY.stream()
				.map(pRepository::rule)
				.filter(Objects::nonNull)
				.forEach(rule -> rule.setTemplate(true));
	}

}
