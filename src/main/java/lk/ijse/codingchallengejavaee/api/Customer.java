/*
Project: Coding Challenge - 1
Author: Nimna Kaveesha Sekara
License: MIT (See the LICENSE file for details)
*/

package lk.ijse.codingchallengejavaee.api;

import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebInitParam;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lk.ijse.codingchallengejavaee.db.CustomerDBProcess;
import lk.ijse.codingchallengejavaee.dto.CustomerDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

@WebServlet(name = "customer", urlPatterns = "/customer")
public class Customer extends HttpServlet {

    private static final Logger logger = LoggerFactory.getLogger(Customer.class);

    Connection connection;

    @Override
    public void init() throws ServletException {
        try {
            var ctx = new InitialContext();
            DataSource pool = (DataSource) ctx.lookup("java:comp/env/jdbc/shopPoss");
            this.connection = pool.getConnection();
            logger.info("Initialized with connection pool: {}", pool);
        } catch (NamingException | SQLException e) {
            logger.error("Error during initialization", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        logger.debug("Handling GET request");

        var action = req.getParameter("action");

        if("getAllCustomers".equals(action)){
            getAllCustomers(req,resp);
        } else if ("getCustomerId".equals(action)) {
            getCustomerId(req, resp);
        } else if ("getAllCustomerIds".equals(action)) {
            getAllCustomerIds(req, resp);
        } else if ("getCustomer".equals(action)) {
            var studentId = req.getParameter("customerId");
            getCustomer(req,resp,studentId);
        } else {
            logger.warn("Invalid action parameter: {}", action);
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid action parameter");
        }
    }

    private void getAllCustomerIds(HttpServletRequest req, HttpServletResponse resp) {
        logger.debug("Handling getAllCustomerIds");
        CustomerDBProcess customerDBProcess = new CustomerDBProcess();
        ArrayList<String> allCustomerIds = customerDBProcess.getAllCustomerIds(connection);

        Jsonb jsonb = JsonbBuilder.create();

        try {
            var json = jsonb.toJson(allCustomerIds);
            resp.setContentType("application/json");
            resp.getWriter().write(json);
            logger.debug("Returned all customer IDs successfully");
        } catch (IOException e) {
            logger.error("Error writing JSON response", e);
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            throw new RuntimeException(e);
        }
    }

    private void getCustomer(HttpServletRequest req, HttpServletResponse resp, String custId) {
        logger.debug("Handling getCustomer");

        CustomerDBProcess customerDBProcess = new CustomerDBProcess();
        CustomerDTO customerDTO = customerDBProcess.getCustomer(connection, custId);
        Jsonb jsonb = JsonbBuilder.create();

        try {
            var json = jsonb.toJson(customerDTO);
            resp.setContentType("application/json");
            resp.getWriter().write(json);
            logger.debug("Returned customer successfully: {}", custId);
        } catch (IOException e) {
            logger.error("Error writing JSON response", e);
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            throw new RuntimeException(e);
        }
    }

    private void getCustomerId(HttpServletRequest req, HttpServletResponse resp) {
        logger.debug("Handling getCustomerId");

        CustomerDBProcess customerDBProcess = new CustomerDBProcess();
        var last_cust_id = customerDBProcess.getCustomerId(connection);
        Jsonb jsonb = JsonbBuilder.create();

        try {
            String json = jsonb.toJson(last_cust_id);
            resp.setContentType("application/json");
            resp.getWriter().write(json);
            logger.debug("Returned last customer ID successfully: {}", last_cust_id);
        } catch (IOException e) {
            logger.error("Error writing JSON response", e);
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            throw new RuntimeException(e);
        }
    }

    private void getAllCustomers(HttpServletRequest req, HttpServletResponse resp) {
        logger.debug("Handling getAllCustomers");

        CustomerDBProcess customerDBProcess = new CustomerDBProcess();
        ArrayList<CustomerDTO> allCustomers = customerDBProcess.getAllCustomers(connection);

        Jsonb jsonb = JsonbBuilder.create();

        try {
            var json = jsonb.toJson(allCustomers);
            resp.setContentType("application/json");
            resp.getWriter().write(json);
            logger.debug("Returned all customers successfully");
        } catch (IOException e) {
            logger.error("Error writing JSON response", e);
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        logger.debug("Handling POST request");

        if (req.getContentType() != null && req.getContentType().toLowerCase().startsWith("application/json")) {
            Jsonb jsonb = JsonbBuilder.create();
            CustomerDTO customerDTO = jsonb.fromJson(req.getReader(), CustomerDTO.class);
            CustomerDBProcess customerDBProcess = new CustomerDBProcess();
            boolean result = customerDBProcess.saveCustomer(customerDTO, connection);
            if (result) {
                resp.setStatus(HttpServletResponse.SC_OK);
                resp.getWriter().write("Customer information saved successfully.");
                logger.info("Customer information saved successfully.");
            } else {
                resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Failed to save customer information.");
                logger.error("Failed to save customer information.");
            }
        } else {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            logger.warn("Invalid request format for POST");
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        logger.debug("Handling PUT request");

        if (req.getContentType() != null && req.getContentType().toLowerCase().startsWith("application/json")) {
            Jsonb jsonb = JsonbBuilder.create();
            CustomerDTO customerDTO = jsonb.fromJson(req.getReader(), CustomerDTO.class);
            CustomerDBProcess customerDBProcess = new CustomerDBProcess();
            boolean result = customerDBProcess.updateCustomer(customerDTO, connection);
            if (result) {
                resp.setStatus(HttpServletResponse.SC_OK);
                resp.getWriter().write("Customer information updated successfully.");
                logger.info("Customer information updated successfully.");
            } else {
                resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Failed to update customer information.");
                logger.error("Failed to update customer information.");
            }
        } else {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            logger.warn("Invalid request format for PUT");
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        logger.debug("Handling DELETE request");

        var customerId = req.getParameter("customerId");
        CustomerDBProcess customerDBProcess = new CustomerDBProcess();
        boolean result = customerDBProcess.deleteCustomer(customerId, connection);
        if (result) {
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().write("Customer information deleted successfully.");
            logger.info("Customer information deleted successfully: {}", customerId);
        } else {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Failed to delete customer information.");
            logger.error("Failed to delete customer information: {}", customerId);
        }
    }
}
