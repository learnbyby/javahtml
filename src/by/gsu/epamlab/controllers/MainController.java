package by.gsu.epamlab.controllers;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import by.gsu.epamlab.constants.ConstantsJSP;
import by.gsu.epamlab.exceptions.DaoException;
import by.gsu.epamlab.exceptions.InitException;
import by.gsu.epamlab.model.beans.Task;
import by.gsu.epamlab.model.beans.User;
import by.gsu.epamlab.model.enums.Operation;
import by.gsu.epamlab.model.enums.View;
import by.gsu.epamlab.model.factories.TaskFactory;
import by.gsu.epamlab.model.factories.UserFactory;
import by.gsu.epamlab.utilities.FileUtilities;
import by.gsu.epamlab.utilities.Utilities;

@WebServlet(urlPatterns = { "/main" }, loadOnStartup = 1)
public class MainController extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = Logger.getLogger(MainController.class.getName());

	public void init() throws ServletException {
		try {
			// Configuring data source
			ServletContext sc = getServletContext();
			UserFactory.init(sc);
			TaskFactory.init(sc);

			// Path to project directory for file operations
			String applicationPath = sc.getRealPath("");
			String uploadPath = applicationPath + File.separator + ConstantsJSP.UPLOAD_DIR;
			File fileUploadDirectory = new File(uploadPath);
			if (!fileUploadDirectory.exists()) {
				fileUploadDirectory.mkdirs();
			}
			FileUtilities.setUploadPath(uploadPath);				
		} catch (InitException e) {
			LOGGER.log(Level.SEVERE, e.toString(), e);
			throw new ServletException(e);
		}
	}

	
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String viewStr = request.getParameter(ConstantsJSP.VIEW_NAME);
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute(ConstantsJSP.USER_NAME);
		int userId = user.getId();
		View view = View.valueOf(viewStr.toUpperCase());
		String strResponse = "[1]"; // will be interpreted by client as DaoException
		try {
			List<Task> tasks = view.getTasks(userId);
			strResponse = Utilities.getJsonTasks(tasks);
		} catch (DaoException e) {
			LOGGER.log(Level.SEVERE, e.toString(), e);
		}
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().write(strResponse);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String operationStr = request.getParameter(ConstantsJSP.OPERATION_NAME);
		String[] idTasksStr = request.getParameterValues(ConstantsJSP.ID_TASK_NAME);
		List<Integer> idTasks = Arrays.stream(idTasksStr).map(Integer::parseInt).collect(Collectors.toList());
		Operation operation = Operation.valueOf(operationStr.toUpperCase());
		String redirectPath = Utilities.getRedirectPath(request);
		try {
			operation.doOperation(idTasks);
			response.sendRedirect(redirectPath);
		} catch (DaoException e) {
			LOGGER.log(Level.SEVERE, e.toString(), e);
			response.sendRedirect(
					redirectPath + "&" + ConstantsJSP.ERROR_NAME + "=" + ConstantsJSP.CONNECTION_ERROR_MESSAGE);
		}
	}
}