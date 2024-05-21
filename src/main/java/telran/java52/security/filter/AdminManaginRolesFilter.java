package telran.java52.security.filter;

import java.io.IOException;

import org.apache.tomcat.util.bcel.Const;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import telran.java52.accounting.dao.UserRepository;

@Component
@RequiredArgsConstructor
@Order(20)
public class AdminManaginRolesFilter implements Filter {

	final UserRepository userRepository;

	@Override
	public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) resp;
		if (checkEndpointAdministrator(request.getMethod(), request.getServletPath())) {
			try {
				String login = request.getUserPrincipal().getName();
				if (!(login.equals("admin"))) {
					throw new RuntimeException();
				}
			} catch (RuntimeException e) {
				response.sendError(401);
				return;
			}

		}
		if (checkEndpointOwnerOrAdministrator(request.getMethod(), request.getServletPath())) {
			try {
				String login = request.getUserPrincipal().getName();
				String path = request.getServletPath();
				String[] segment = path.split("/");
				if(!(segment[3].equals(login)|| login.equals("admin"))){
					throw new RuntimeException();
				}

			} catch (RuntimeException e) {
				response.sendError(401);
				return;
			}
		}

		chain.doFilter(request, response);
	}

	private boolean checkEndpointOwnerOrAdministrator(String method, String path) {
		return HttpMethod.DELETE.matches(method) && path.matches("/account/user/.*");
	}

	private boolean checkEndpointAdministrator(String method, String path) {

		return ((HttpMethod.PUT.matches(method) || HttpMethod.DELETE.matches(method))
				&& path.matches("/account/.*/role/.*"));
	}

}
