package forecastbuster;

import forecastbuster.incoming.FetchTask;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by Kristjan on 3.01.14.
 */
public class StarterServlet extends HttpServlet {
    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        FetchTask fetchTask = new FetchTask();
        fetchTask.start();
        resp.setContentType("text/plain");
        resp.getWriter().println("Hello, world" );
    }
}
