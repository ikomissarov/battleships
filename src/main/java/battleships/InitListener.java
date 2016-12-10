package battleships;

import battleships.model.WaitingRoom;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * @author Igor
 */
public class InitListener implements ServletContextListener {
    private static final Logger logger = LoggerFactory.getLogger(InitListener.class);

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        servletContextEvent.getServletContext().setAttribute("waitingRoom", new WaitingRoom());
        logger.info("Context Initialized.");
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        logger.info("Context Destroyed.");
    }
}
