package lk.ijse.codingchallengejavaee.api;

import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lk.ijse.codingchallengejavaee.db.OrderDBProcess;
import lk.ijse.codingchallengejavaee.db.OrderDetailsDBProcess;
import lk.ijse.codingchallengejavaee.dto.CombinedOrderDTO;
import lk.ijse.codingchallengejavaee.dto.OrderDTO;
import lk.ijse.codingchallengejavaee.dto.OrderDetailsDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@WebServlet(name = "order", urlPatterns = "/order")
public class Order extends HttpServlet {

    final static Logger logger = LoggerFactory.getLogger(Order.class);
    Connection connection;

    @Override
    public void init() throws ServletException {
        try {
            var ctx = new InitialContext();
            DataSource pool = (DataSource) ctx.lookup("java:comp/env/jdbc/shopPoss");
            this.connection = pool.getConnection();
        } catch (NamingException | SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        if (req.getContentType() != null && req.getContentType().toLowerCase().startsWith("application/json")) {
            Jsonb jsonb = JsonbBuilder.create();
            CombinedOrderDTO combinedOrderDTO = jsonb.fromJson(req.getReader(), CombinedOrderDTO.class);
            OrderDBProcess orderDBProcess = new OrderDBProcess();
            boolean result = orderDBProcess.saveOrder(combinedOrderDTO, connection);
            if (result) {
                resp.setStatus(HttpServletResponse.SC_OK);
                resp.getWriter().write("Order information saved successfully.");
            } else {
                resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Failed to save Order information.");
            }
        } else {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
        }

    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String orderId = req.getParameter("orderId");
        if (orderId != null) {
            OrderDBProcess orderDBProcess = new OrderDBProcess();
            boolean result = orderDBProcess.delete(orderId, connection);
            if (result) {
                resp.setStatus(HttpServletResponse.SC_OK);
                resp.getWriter().write("Order deleted successfully.");
            } else {
                resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Failed to delete order.");
            }
        } else {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing orderId parameter.");
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        var action = req.getParameter("action");

        if ("getAllOrders".equals(action)) {
            getAllOrders(req, resp);
        } else if ("getOrderId".equals(action)) {
            generateOrderId(req, resp);
        } else if ("getOrder".equals(action)) {
            var orderId = req.getParameter("orderId");
            getOrder(req, resp, orderId);
        } else {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid action parameter");
        }
    }

    private void getOrder(HttpServletRequest req, HttpServletResponse resp, String orderId) {
        try {
            if (orderId != null) {
                OrderDBProcess orderDBProcess = new OrderDBProcess();
                CombinedOrderDTO order = orderDBProcess.getOrder(orderId, connection);
                if (order != null) {
                    Jsonb jsonb = JsonbBuilder.create();
                    String json = jsonb.toJson(order);
                    resp.setContentType("application/json");
                    resp.getWriter().write(json);
                } else {
                    resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Order not found.");
                }
            } else {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing orderId parameter.");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void generateOrderId(HttpServletRequest req, HttpServletResponse resp) {
        OrderDBProcess orderDBProcess = new OrderDBProcess();
        var orderId = orderDBProcess.generateOrderId(connection);
        Jsonb jsonb = JsonbBuilder.create();
        try {
            String json = jsonb.toJson(orderId);
            resp.setContentType("application/json");
            resp.getWriter().write(json);
        } catch (IOException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            throw new RuntimeException(e);
        }
    }

    private void getAllOrders(HttpServletRequest req, HttpServletResponse resp) {
        try {
            OrderDBProcess orderDBProcess = new OrderDBProcess();
            List<CombinedOrderDTO> allOrders = orderDBProcess.getAllOrders(connection);
            Jsonb jsonb = JsonbBuilder.create();
            String json = jsonb.toJson(allOrders);
            resp.setContentType("application/json");
            resp.getWriter().write(json);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (req.getContentType() != null && req.getContentType().toLowerCase().startsWith("application/json")) {
            Jsonb jsonb = JsonbBuilder.create();
            CombinedOrderDTO combinedOrderDTO = jsonb.fromJson(req.getReader(), CombinedOrderDTO.class);
            OrderDBProcess orderDBProcess = new OrderDBProcess();
            boolean result = orderDBProcess.updateOrder(combinedOrderDTO, connection);
            if (result) {
                resp.setStatus(HttpServletResponse.SC_OK);
                resp.getWriter().write("Order information update successfully.");
            } else {
                resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Failed to update Order information.");
            }
        } else {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
        }
    }
}
