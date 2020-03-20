package by.gsu.epamlab.controllers;

import java.io.IOException;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import by.gsu.epamlab.constants.ConstantsJSP;
import by.gsu.epamlab.exceptions.DaoException;
import by.gsu.epamlab.ifaces.UserDao;
import by.gsu.epamlab.model.beans.User;
import by.gsu.epamlab.model.factories.UserFactory;
import by.gsu.epamlab.utilities.Utilities;

@WebServlet("/login")
public class LoginController extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = Logger.getLogger(LoginController.class.getName());

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String account = request.getParameter(ConstantsJSP.ACCOUNT_NAME);
		String password = request.getParameter(ConstantsJSP.PASSWORD_NAME);
		String passwordHash = Utilities.getPasswordHash(password);
		UserDao userDao = UserFactory.getClassFromFactory();
		String errorRedirectPath = request.getContextPath() + ConstantsJSP.LOGIN_PAGE_URL + "?" + ConstantsJSP.ERROR_NAME + "=";
		try {
			Optional<User> user = userDao.getUser(account, passwordHash);
			if (user.isPresent()) {
				HttpSession session = request.getSession();
				session.setAttribute(ConstantsJSP.USER_NAME, user.get());
				response.sendRedirect(Utilities.getRedirectPath(request));
			} else {
				response.sendRedirect(errorRedirectPath + ConstantsJSP.INCORRECT_PASSWORD_MESSAGE);
			}
		} catch (DaoException e) {
			LOGGER.log(Level.SEVERE, e.toString(), e);
			response.sendRedirect(errorRedirectPath + ConstantsJSP.CONNECTION_ERROR_MESSAGE);
		}
	}
}