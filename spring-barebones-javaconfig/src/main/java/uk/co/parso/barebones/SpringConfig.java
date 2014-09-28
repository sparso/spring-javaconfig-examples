package uk.co.parso.barebones;

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
// Scan for annotated classes
@ComponentScan(basePackages = {"uk.co.parso"})
public class SpringConfig {
    
}
