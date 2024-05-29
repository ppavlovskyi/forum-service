package telran.java52.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.expression.WebExpressionAuthorizationManager;

import lombok.RequiredArgsConstructor;
import telran.java52.accounting.model.Role;
import telran.java52.post.dao.PostRepository;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {
	
	final PostRepository postRepository;
	
	@Bean
	SecurityFilterChain securityFilterChain (HttpSecurity http) throws Exception {
		http.httpBasic(Customizer.withDefaults());
		http.csrf(csrf->csrf.disable());
		http.authorizeHttpRequests(authorize -> authorize
				.requestMatchers("/account/register", "/forum/posts/**")
					.permitAll()
				.requestMatchers("/account/user/{user}/role/{role}")
					.hasRole(Role.ADMINISTRATOR.name())
				.requestMatchers(HttpMethod.PUT, "/account/user/{login}")
					.access(new WebExpressionAuthorizationManager("#login == authentication.name"))
				.requestMatchers(HttpMethod.DELETE, "/account/user/{login}")
					.access(new WebExpressionAuthorizationManager("#login == authentication.name or hasRole('ADMINISTRATOR')"))
				.requestMatchers(HttpMethod.POST, "/forum/post/{author}")
					.access(new WebExpressionAuthorizationManager("#author == authentication.name"))
				.requestMatchers(HttpMethod.PUT, "/forum/post/{postId}/comment/{author}")
					.access(new WebExpressionAuthorizationManager("#author == authentication.name"))
//				.requestMatchers(HttpMethod.PUT, "/forum/post/{postId}")
//					.access(new WebExpressionAuthorizationManager("@postRepository.findById(#postId).orElse(null)?.author == authentication.name"))
				
				.anyRequest().authenticated()
				);
		return http.build();
	}
	


}
