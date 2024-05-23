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
import telran.java52.accounting.model.Role;
import telran.java52.accounting.model.UserAccount;

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
				if (!isPresentRoleAdministrator(login)) {
					throw new RuntimeException();
				}
			} catch (RuntimeException e) {
				response.sendError(403, "Not authorized");
				return;
			}

		}
		if (checkEndpointOwnerOrAdministrator(request.getMethod(), request.getServletPath())) {
			try {
				String login = request.getUserPrincipal().getName();
				String path = request.getServletPath();
				String[] segment = path.split("/");
				if(!(segment[3].equals(login)|| isPresentRoleAdministrator(login))){
					throw new RuntimeException();
				}

			} catch (RuntimeException e) {
				response.sendError(403, "You are not allowed to access this resource");
				return;
			}
		}

		chain.doFilter(request, response);
	}

	private boolean isPresentRoleAdministrator(String login) {
		UserAccount userAccount = userRepository.findById(login).orElseThrow(RuntimeException::new);
		return userAccount.getRoles().contains(Role.ADMINISTRATOR);
	}

	private boolean checkEndpointOwnerOrAdministrator(String method, String path) {
		return HttpMethod.DELETE.matches(method) && path.matches("/account/user/.*");
	}

	private boolean checkEndpointAdministrator(String method, String path) {

		return ((HttpMethod.PUT.matches(method) || HttpMethod.DELETE.matches(method))
				&& path.matches("/account/.*/role/.*"));
	}

}
