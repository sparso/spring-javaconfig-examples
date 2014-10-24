package uk.co.parso.barebones;

import ch.qos.logback.classic.LoggerContext;
import javax.annotation.PreDestroy;
import org.slf4j.ILoggerFactory;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
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
// Enabled the @Scheduled annotation
@EnableScheduling
// Scan for annotated classes
@ComponentScan(basePackages = {"uk.co.parso"})
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

    @Bean
    public TaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setCorePoolSize(4);
        taskExecutor.setMaxPoolSize(4);
        taskExecutor.afterPropertiesSet();
        taskExecutor.setQueueCapacity(100);
        return taskExecutor;
    }
}
