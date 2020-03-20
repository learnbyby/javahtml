package by.gsu.epamlab.filters;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import by.gsu.epamlab.constants.ConstantsJSP;
import by.gsu.epamlab.model.beans.User;
import by.gsu.epamlab.utilities.Utilities;

@WebFilter({ "/index.html", "/login.html", "/register.html", "/start", "/login", "/register" })
public class UserFilter implements Filter {

	// Restrict access to guest pages for users
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		HttpServletResponse httpResponse = (HttpServletResponse) response;
		HttpSession session = httpRequest.getSession();
		User user = (User) session.getAttribute(ConstantsJSP.USER_NAME);
		if (user != null) {
			httpResponse.sendRedirect(Utilities.getRedirectPath(httpRequest));
			return;
		}
		chain.doFilter(request, response);
	}

	@Override
	public void destroy() {
	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
	}
}