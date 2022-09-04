package myserver.results;

import javax.servlet.ServletContext;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Servlet to show the results of a specific thread.
 */
@WebServlet(name = "ShowResults", value = "/ShowResults")
public class ShowResults extends HttpServlet {
    /**
     * doGet method to display the results.
     *
     * @param request  The servlet http request.
     * @param response The servlet http response.
     * @throws IOException Error while sending redirect to the error page.
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        ServletContext context = getServletContext();
        ConcurrentHashMap<Integer, Integer[]> counters;
        try {
            // Get the map from the global scope
            counters = (ConcurrentHashMap<Integer, Integer[]>) context.getAttribute("imageCounters");
            // get the session, but don't create one if there it's null.
            HttpSession session = request.getSession(false);
            int id = (Integer) session.getAttribute("id");
            String url = (String) session.getAttribute("url");
            int count = counters.get(id)[1]; // The image counter.
            int finished = counters.get(id)[0]; // The flag.

            String message;
            if (finished == 1) {
                message = "Crawling is finished";
            } else
                message = "still crawling...<a href=\"/ShowResults\">reload</a> this page for final results!";

            response.setContentType("text/html");
            PrintWriter toClient = response.getWriter();
            toClient.print("<h2>Crawling: " + url + "</h2><br>");
            toClient.print("<h3>Found " + count + " images!</h3><br>");
            toClient.print("<h3>" + message + "</h3><br>");
            toClient.print("<p><a href=\"/\">back to main page</a></p>");
            toClient.close();
        } catch (NullPointerException | IllegalStateException e) {
            response.sendRedirect("/html/submiterror.html");
        }

    }

}
