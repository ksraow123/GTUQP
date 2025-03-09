package in.coempt.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.sql.Connection;

import java.sql.DriverManager;
@Component
@WebListener
public class ConnectionUtil implements ServletContextListener {

    static ServletContext context;
@Value("${spring.datasource.url}")
private String dbUrl;
    @Value("${spring.datasource.username}")
    private String dbUser;
    @Value("${spring.datasource.password}")
    private String dbPassword;
    @Override
    public void contextInitialized(ServletContextEvent event) {
        context = event.getServletContext();

        // Example of setting attributes (you should set these in your actual initialization code)
        context.setAttribute("db_url", dbUrl);
        context.setAttribute("db_user", dbUser);
        context.setAttribute("db_password", dbPassword);
    }

    public static Connection getConnection() {
        Connection con = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            String dbUrl = (String) context.getAttribute("db_url");
            String dbUser = (String) context.getAttribute("db_user");
            String dbPassword = (String) context.getAttribute("db_password");

            if (dbUrl == null || dbUser == null || dbPassword == null) {
                throw new IllegalStateException("Database connection attributes are not set in the ServletContext.");
            }

            con = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
            return con;
        } catch (Exception e) {
            e.printStackTrace();

        }
        return con;
    }

    @Override
    public void contextDestroyed(ServletContextEvent event) {
        // Clean up any resources here
    }
}
