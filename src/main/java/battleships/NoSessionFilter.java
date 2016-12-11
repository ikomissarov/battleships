package battleships;

import battleships.model.Response;
import battleships.model.User;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * @author Igor
 */
public class NoSessionFilter implements Filter {
    private final ObjectMapper jsonMapper = new ObjectMapper();

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {
        User user = (User) ((HttpServletRequest) servletRequest).getSession().getAttribute("user");
        if(user == null) {
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
