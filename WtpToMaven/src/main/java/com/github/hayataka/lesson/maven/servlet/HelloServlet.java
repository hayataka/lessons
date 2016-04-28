package com.github.hayataka.lesson.maven.servlet;

import java.io.IOException;
import java.io.Serializable;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(name="HelloServlet",urlPatterns={"/hello/*"})
public class HelloServlet extends HttpServlet implements Serializable {

	   private static final long serialVersionUID = 1L;

	    @Override
	    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
	            throws ServletException, IOException {
	        resp.getWriter().print("it is Servlet 3.0. ");
	    }
}
