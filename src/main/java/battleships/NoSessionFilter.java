package battleships;

import battleships.model.Response;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * @author Igor
 */
public class NoSessionFilter implements Filter {
    private static final Logger logger = LoggerFactory.getLogger(NoSessionFilter.class);
    private final ObjectMapper jsonMapper = new ObjectMapper();

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {
        HttpSession session = ((HttpServletRequest) servletRequest).getSession(false);
        if(session == null || session.getAttribute("user") == null) {
            logger.info("Redirecting user with no session.");
            Response response = new Response(Response.Type.REDIRECT, "index.html");
            jsonMapper.writeValue(servletResponse.getOutputStream(), response);
        } else {
            filterChain.doFilter(servletRequest, servletResponse);
        }
    }

    @Override
    public void destroy() {

    }
}
