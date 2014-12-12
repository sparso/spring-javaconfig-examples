package uk.co.parso.barebones.config;

import ch.qos.logback.classic.LoggerContext;
import javax.annotation.PreDestroy;
import org.slf4j.ILoggerFactory;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

/**
 * Root application context configuration
 * 
 * @author sam
 */
@Configuration
// Import Spring MVC configuration, which does things like enable 
// the use of @Controller
@EnableWebMvc
public class SpringConfig {
    @PreDestroy
    public void preDestroy() {
        // Shutdown Logback cleanly due to an SMTPAppender bug
        ILoggerFactory factory = LoggerFactory.getILoggerFactory();
        if (factory instanceof LoggerContext) {
            LoggerContext ctx = (LoggerContext) factory;
            ctx.stop();
        }
    }
}
