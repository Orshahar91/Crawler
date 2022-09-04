package myserver.thread;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashSet;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Image crawler to count the number of images inside a given URL
 * Taking thr wanted depth into account.
 *
 */
public class Crawler extends Thread {
    /**
     * The max depth for the recursion.
     */
    public static int MAX_DEPTH;
    /**
     * To save the visited links.
     */
    private final HashSet<String> links;
    /**
     * The url address to crawl.
     */
    private final String url;
    /**
     * The unique client id to save/update/display the results.
     */
    private final int clientId;
    /**
     * Reference to the global results map.
     */
    private final ConcurrentHashMap<Integer, Integer[]> counters;
    /**
     * Stores the image counter and the unique id.
     */
    private final Integer []currCount;

    /**
     * Instantiates a new Crawler.
     *
     * @param URL            the url seed
     * @param servletContext the servlet context
     * @param request        the http request
     */
    public Crawler(String URL, ServletContext servletContext, HttpServletRequest request) {
        HttpSession session = request.getSession(); // creates session for the client(current request)
        session.setAttribute("url", URL);
        currCount = new Integer[]{0,0}; // initialization
        links = new HashSet<>();
        url = URL;
        // synchronized block to deal with race conditions
        synchronized (this) {
            // incrementing the id and updating it in the global scope and in the session.
            clientId = (int) servletContext.getAttribute("id") + 1;
            servletContext.setAttribute("id", clientId);
            session.setAttribute("id", clientId);
            // assigning the initialized array to the id key
            counters = (ConcurrentHashMap<Integer, Integer[]>) servletContext.getAttribute("imageCounters");
            counters.put(clientId, currCount);
        }
    }

    /**
     * Override method of Thread
     */
    public void run() {
        System.out.println("Starting Thread for url: " + url);
        getPageLinks(url, 0);
        // setting the "finished" flag to '1'
        currCount[0] = 1;
        counters.put(clientId, currCount);
        System.out.println("End of thread for url: " + url);
    }

    /**
     * Recursive method to count images inside the url
     *
     * @param URL   the url
     * @param depth the depth
     */
    public void getPageLinks(String URL, int depth) {
        //  Check if you have already crawled the URLs
        if ((!links.contains(URL) && (depth < MAX_DEPTH))) {
            System.out.println("Begin crawling: " + URL + " at depth: " + depth);
            try {
                // If not add it to the index
                links.add(URL);
                // Fetch the HTML code
                Document document = Jsoup.connect(URL).ignoreContentType(true).get();
                // Get all the images in current page
                Elements imageElements = document.select("img");
                // Update the counter
                currCount[1] += imageElements.size();
                counters.put(clientId, currCount);
                System.out.println("Number of images found for: " + URL + " is " + imageElements.size());
                // Parse the HTML to extract links to other URLs
                Elements linksOnPage = document.select("a[href]");
                System.out.println("End crawling: " + URL + " at depth: " + depth);
                depth++;
                // For each extracted URL... go back to Step 4.
                for (Element page : linksOnPage) {
                    getPageLinks(page.attr("abs:href"), depth);
                }
            } catch (IOException e) {
                System.err.println("For '" + URL + "': " + e.getMessage());
            }
        }
    }
}