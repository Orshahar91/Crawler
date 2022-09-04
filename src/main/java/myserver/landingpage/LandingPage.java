package myserver.landingpage;

import myserver.thread.Crawler;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * This is our landing page servlet.
 */
@WebServlet(name = "LandingPage", urlPatterns = {"", "/LandingPage"})
public class LandingPage extends HttpServlet {
    /**
     * Get method to redirect client to the html landing page.
     *
     * @param request  The servlet http request.
     * @param response The servlet http response.
     * @throws ServletException Error while including the html page.
     * @throws IOException      Error while including the html page.
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ServletContext context = getServletContext();
        context.getRequestDispatcher("/html/landingpage.html").include(request, response);
    }

    /**
     * Post method to retrieve url entered by the client,
     * and validate it before creating a thread to start crawling.
     *
     * @param request  The servlet http request.
     * @param response The servlet http response.
     * @throws ServletException request dispatcher include error.
     * @throws IOException      request dispatcher include, sendRedirect error.
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ServletContext context = getServletContext();

        String url = request.getParameter("url");
        HttpURLConnection connection;

        try {
            // Validation after trimming.
            URL urlConnection = new URL(url.trim());
            urlConnection.toURI();
            connection = (HttpURLConnection) urlConnection.openConnection();
            int responseCode = connection.getResponseCode();
            if (HttpURLConnection.HTTP_OK != responseCode) {
                response.sendRedirect("/html/error.html");
            }
        }
        catch (URISyntaxException | IOException e) {
            response.sendRedirect("/html/error.html");
            return;
        }

        // Creating new thread for the request.
        Crawler crawler = new Crawler(url, context, request);
        crawler.start(); // Activate the thread and go to the run method.
        context.getRequestDispatcher("/html/success.html").include(request, response);
    }
}





