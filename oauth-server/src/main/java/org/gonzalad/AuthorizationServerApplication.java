package org.gonzalad;

import org.gonzalad.config.SpringConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

/***
 * Sample OAuth2 server
 * 
 * see https://spring.io/blog/2015/02/03/sso-with-oauth2-angular-js-and-spring-
 * security-part-v http://www.bubblecode.net/fr/2016/01/22/comprendre-oauth2/
 * https://spring.io/guides/tutorials/spring-boot-oauth2/
 * http://projects.spring.io/spring-security-oauth/docs/oauth2.html
 * 
 * @author gonzalad
 */
// @SpringBootApplication
// @ComponentScan(basePackages = "org.gonzalad")
@SpringBootApplication//(scanBasePackageClasses = SpringConfig.class)
@EnableAuthorizationServer
//@Configuration
//@EnableWebMvc
//@EnableWebSecurity
//@ComponentScan("org.gonzalad")
public class AuthorizationServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(AuthorizationServerApplication.class, args);
	}
}
