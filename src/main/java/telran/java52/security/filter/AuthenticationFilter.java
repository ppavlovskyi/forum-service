package telran.java52.security.filter;

import java.io.IOException;
import java.security.Principal;
import java.util.Base64;

import org.mindrot.jbcrypt.BCrypt;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import telran.java52.accounting.dao.UserRepository;
import telran.java52.accounting.model.UserAccount;

@Component
@RequiredArgsConstructor
@Order(10)
public class AuthenticationFilter implements jakarta.servlet.Filter {

	final UserRepository userRepository;

	@Override
	public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) resp;
//		System.out.println(request.getServletPath());
//		System.out.println(request.getMethod());
//		System.out.println(request.getHeader("Authorization"));
		if (checkEndpoint(request.getMethod(), request.getServletPath())) {
			try {
				String[] credentials = getCredrntials(request.getHeader("Authorization"));

				UserAccount userAccount = userRepository.findById(credentials[0]).orElseThrow(RuntimeException::new);
				if (!BCrypt.checkpw(credentials[1], userAccount.getPassword())) {
					throw new RuntimeException();
				}
				request = new WrappedRequest(request, userAccount.getLogin());
			} catch (RuntimeException e) {
				response.sendError(401);
				return;
			}
		}
		// request.getUserPrincipal();
		chain.doFilter(request, response);
	}

	private boolean checkEndpoint(String method, String path) {
		return !(HttpMethod.POST.matches(method) && path.matches("/account/register")
				||  path.matches("/forum/posts/\\w+(/\\w+)?"));
//				|| HttpMethod.POST.matches(method) && path.matches("/forum/posts/.*"));
	}

	private String[] getCredrntials(String header) {
		String token = header.split(" ")[1];
		String decode = new String(Base64.getDecoder().decode(token));
		return decode.split(":");
	}

	private class WrappedRequest extends HttpServletRequestWrapper {
		private String login;

		public WrappedRequest(HttpServletRequest request, String login) {
			super(request);
			this.login = login;
		}

		@Override
		public Principal getUserPrincipal() {
			return () -> login;
		}

	}

}
