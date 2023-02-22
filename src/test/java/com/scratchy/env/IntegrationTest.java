package com.scratchy.env;

import com.scratchy.env.EnvironmentManagerApplicationApp;
import com.scratchy.env.config.AsyncSyncConfiguration;
import com.scratchy.env.config.EmbeddedElasticsearch;
import com.scratchy.env.config.EmbeddedKafka;
import com.scratchy.env.config.EmbeddedSQL;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

/**
 * Base composite annotation for integration tests.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@SpringBootTest(classes = { EnvironmentManagerApplicationApp.class, AsyncSyncConfiguration.class })
@EmbeddedElasticsearch
@EmbeddedKafka
@EmbeddedSQL
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public @interface IntegrationTest {
}
