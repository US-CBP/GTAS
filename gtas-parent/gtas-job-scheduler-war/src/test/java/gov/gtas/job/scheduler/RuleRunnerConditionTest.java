package gov.gtas.job.scheduler;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotatedTypeMetadata;

@RunWith(MockitoJUnitRunner.class)
public class RuleRunnerConditionTest {

	@Mock
	ConditionContext context;

	@Mock
	Environment environment;

	@Mock
	AnnotatedTypeMetadata metadata;

	@Test
	public void testEnabled() {
		Mockito.when(environment.getProperty("enable.rule.runner")).thenReturn("true");
		Mockito.when(context.getEnvironment()).thenReturn(environment);
		RuleRunnerCondition rrc = new RuleRunnerCondition();
		assertTrue(rrc.matches(context, metadata));

	}

	@Test
	public void testDisabled() {
		Mockito.when(environment.getProperty("enable.rule.runner")).thenReturn("false");
		Mockito.when(context.getEnvironment()).thenReturn(environment);
		RuleRunnerCondition rrc = new RuleRunnerCondition();
		assertFalse(rrc.matches(context, metadata));

	}

}
