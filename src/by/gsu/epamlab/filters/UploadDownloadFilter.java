package by.gsu.epamlab.filters;

import java.io.IOException;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

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
import by.gsu.epamlab.exceptions.DaoException;
import by.gsu.epamlab.ifaces.TaskDao;
import by.gsu.epamlab.model.beans.Task;
import by.gsu.epamlab.model.beans.User;
import by.gsu.epamlab.model.factories.TaskFactory;
import by.gsu.epamlab.utilities.Utilities;

@WebFilter({ "/upload", "/download" })
public class UploadDownloadFilter implements Filter {

	private static final Logger LOGGER = Logger.getLogger(UploadDownloadFilter.class.getName());

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		HttpServletResponse httpResponse = (HttpServletResponse) response;
		HttpSession session = httpRequest.getSession();
		User user = (User) session.getAttribute(ConstantsJSP.USER_NAME);
		// Restrict access to application for guests
		if (user == null) {
			httpResponse.sendRedirect(httpRequest.getContextPath() + ConstantsJSP.INDEX_PAGE_URL);
			chain.doFilter(request, response);
			return;
		}
		String idTaskStr = httpRequest.getParameter(ConstantsJSP.ID_TASK_NAME);
		String redirectPath = Utilities.getRedirectPath(httpRequest) + "&" + ConstantsJSP.ERROR_NAME + "=";
		int idTask = 0;
		try {
			idTask = Integer.parseInt(idTaskStr);
			TaskDao taskDao = TaskFactory.getClassFromFactory();
			Optional<Task> task = taskDao.getTaskById(idTask);
			// if there is no task with such id, the user is redirected to the main page
			if (!task.isPresent()) {
				httpResponse.sendRedirect(redirectPath + ConstantsJSP.WRONG_ID_TASK + idTaskStr);
				return;
			} else {
				// if the task with such id belongs to another user, the user is redirected to
				// the main page
				if (task.get().getUserId() != user.getId()) {
					httpResponse.sendRedirect(redirectPath + ConstantsJSP.WRONG_ID_TASK + idTaskStr);
					return;
				}
			}
		} catch (IllegalArgumentException e) {
			LOGGER.log(Level.SEVERE, e.toString(), e);
			httpResponse.sendRedirect(redirectPath + ConstantsJSP.WRONG_ID_TASK + idTaskStr);
			return;
		} catch (DaoException e) {
			LOGGER.log(Level.SEVERE, e.toString(), e);
			httpResponse.sendRedirect(redirectPath + ConstantsJSP.CONNECTION_ERROR_MESSAGE);
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