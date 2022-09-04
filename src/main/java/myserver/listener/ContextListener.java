package myserver.listener;

import myserver.thread.Crawler;

import javax.servlet.*;
import javax.servlet.annotation.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This is a listener for the servlet context, triggered once
 * when we get to any servlet.
 */
@WebListener
public class ContextListener implements ServletContextListener {

    /**
     * This method is called when the servlet context is initialized(when the Web application is deployed).
     * Creates a concurrent hashmap to be used by different clients(Threads).
     * @param sce context initialize event
     */
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ServletContext ctx = sce.getServletContext();
        try {

            // Set the max depth inside Crawler class to the servlet init parameter defined in web.xml
            Crawler.MAX_DEPTH = Integer.parseInt(ctx.getInitParameter("maxdepth"));

            // This hashmap is Thread-Safe and can manage I/O without locking mechanism.
            // The value is comprised of integer array and will include the image counter
            // and a flag to distinguish whether the thread has finished the job.
            ConcurrentHashMap<Integer, Integer[]> map = new ConcurrentHashMap<Integer, Integer[]>();

            // This map will be available to all other servlets
            ctx.setAttribute("imageCounters", map);

            // Global counter to give unique id to each client(Thread).
            ctx.setAttribute("id", 0);

        } catch (IllegalStateException e) {
            System.out.println(e.getMessage());
        }
    }
}
