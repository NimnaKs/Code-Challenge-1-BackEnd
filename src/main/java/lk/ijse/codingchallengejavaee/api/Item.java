/*
Project: Coding Challenge - 1
Author: Nimna Kaveesha Sekara
License: MIT (See the LICENSE file for details)
*/

package lk.ijse.codingchallengejavaee.api;

import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lk.ijse.codingchallengejavaee.db.ItemDBProcess;
import lk.ijse.codingchallengejavaee.dto.ItemDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebServlet(name = "item", urlPatterns = "/item")
public class Item extends HttpServlet {

    private static final Logger logger = LoggerFactory.getLogger(Item.class);
    private Connection connection;

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
        var action = req.getParameter("action");

        if ("getAllItems".equals(action)) {
            getAllItems(req, resp);
        } else if ("getItemCode".equals(action)) {
            generateItemCode(req, resp);
        } else if ("getItem".equals(action)) {
            var itemCode = req.getParameter("itemCode");
            getItem(req, resp, itemCode);
        } else if ("getItemIds".equals(action)) {
            getAllItemIds(req, resp);
        } else {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid action parameter");
            logger.warn("Invalid action parameter: {}", action);
        }
    }

    private void getAllItemIds(HttpServletRequest req, HttpServletResponse resp) {
        logger.debug("Handling getAllItemIds");

        ItemDBProcess itemDBProcess = new ItemDBProcess();
        ArrayList<String> allItems = itemDBProcess.getAllItemIds(connection);

        Jsonb jsonb = JsonbBuilder.create();

        try {
            var json = jsonb.toJson(allItems);
            resp.setContentType("application/json");
            resp.getWriter().write(json);
            logger.debug("Returned all item IDs successfully");
        } catch (IOException e) {
            logger.error("Error writing JSON response", e);
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            throw new RuntimeException(e);
        }
    }

    private void getItem(HttpServletRequest req, HttpServletResponse resp, String itemCode) {
        logger.debug("Handling getItem");

        ItemDBProcess itemDBProcess = new ItemDBProcess();
        ItemDTO itemDTO = itemDBProcess.getItem(itemCode, connection);
        Jsonb jsonb = JsonbBuilder.create();

        try {
            var json = jsonb.toJson(itemDTO);
            resp.setContentType("application/json");
            resp.getWriter().write(json);
            logger.debug("Returned item successfully: {}", itemCode);
        } catch (IOException e) {
            logger.error("Error writing JSON response", e);
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            throw new RuntimeException(e);
        }
    }

    private void generateItemCode(HttpServletRequest req, HttpServletResponse resp) {
        logger.debug("Handling generateItemCode");

        ItemDBProcess itemDBProcess = new ItemDBProcess();
        var lastItemCode = itemDBProcess.generateItemCode(connection);
        Jsonb jsonb = JsonbBuilder.create();

        try {
            String json = jsonb.toJson(lastItemCode);
            resp.setContentType("application/json");
            resp.getWriter().write(json);
            logger.debug("Returned last item code successfully: {}", lastItemCode);
        } catch (IOException e) {
            logger.error("Error writing JSON response", e);
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            throw new RuntimeException(e);
        }
    }

    private void getAllItems(HttpServletRequest req, HttpServletResponse resp) {
        logger.debug("Handling getAllItems");

        ItemDBProcess itemDBProcess = new ItemDBProcess();
        ArrayList<ItemDTO> allItems = itemDBProcess.getAllItems(connection);

        Jsonb jsonb = JsonbBuilder.create();

        try {
            var json = jsonb.toJson(allItems);
            resp.setContentType("application/json");
            resp.getWriter().write(json);
            logger.debug("Returned all items successfully");
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
            ItemDTO itemDTO = jsonb.fromJson(req.getReader(), ItemDTO.class);
            ItemDBProcess itemDBProcess = new ItemDBProcess();
            boolean result = itemDBProcess.saveItem(itemDTO, connection);
            if (result) {
                resp.setStatus(HttpServletResponse.SC_OK);
                resp.getWriter().write("Item information saved successfully.");
                logger.info("Item information saved successfully.");
            } else {
                resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Failed to save item information.");
                logger.error("Failed to save item information.");
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
            ItemDTO itemDTO = jsonb.fromJson(req.getReader(), ItemDTO.class);
            ItemDBProcess itemDBProcess = new ItemDBProcess();
            boolean result = itemDBProcess.updateItem(itemDTO, connection);
            if (result) {
                resp.setStatus(HttpServletResponse.SC_OK);
                resp.getWriter().write("Item information updated successfully.");
                logger.info("Item information updated successfully.");
            } else {
                resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Failed to update item information.");
                logger.error("Failed to update item information.");
            }
        } else {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            logger.warn("Invalid request format for PUT");
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        logger.debug("Handling DELETE request");

        var itemCode = req.getParameter("itemCode");
        ItemDBProcess itemDBProcess = new ItemDBProcess();
        boolean result = itemDBProcess.deleteItem(itemCode, connection);
        if (result) {
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().write("Item information deleted successfully.");
            logger.info("Item information deleted successfully: {}", itemCode);
        } else {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Failed to delete item information.");
            logger.error("Failed to delete item information: {}", itemCode);
        }
    }
}
