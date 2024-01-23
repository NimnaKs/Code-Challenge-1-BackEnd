package lk.ijse.codingchallengejavaee.db;

import lk.ijse.codingchallengejavaee.dto.CombinedOrderDTO;
import lk.ijse.codingchallengejavaee.dto.OrderDTO;
import lk.ijse.codingchallengejavaee.dto.OrderDetailsDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrderDBProcess {

    private static final Logger logger = LoggerFactory.getLogger(OrderDBProcess.class);

    public String generateOrderId(Connection connection) {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "SELECT MAX(order_id) as last_order_id FROM Orders"
            );

            ResultSet resultSet = preparedStatement.executeQuery();

            return (resultSet.next() && resultSet.getString("last_order_id") != null) ?
                    "order-" + String.format("%03d", Integer.parseInt(resultSet.getString("last_order_id").substring(6)) + 1)
                    : "order-001";

        } catch (SQLException e) {
            logger.error("Error generating order ID", e);
            throw new RuntimeException(e);
        }
    }

    public boolean saveOrder(CombinedOrderDTO combinedOrderDTO, Connection connection) {
        try {
            connection.setAutoCommit(false);

            if (save(combinedOrderDTO.getOrderDTO(), connection)) {

                for (OrderDetailsDTO orderDetailsDTO : combinedOrderDTO.getOrderDetailsDTOS()) {
                    boolean isSavedOrderDetails = new OrderDetailsDBProcess().saveOrderDetails(orderDetailsDTO, connection);

                    if (isSavedOrderDetails) {
                        boolean isSavedItemDetails = new ItemDBProcess().updateItemOrder(orderDetailsDTO, connection);

                        if (!isSavedItemDetails) {
                            return false;
                        }
                    } else {
                        return false;
                    }
                }

                connection.commit();
                return true;
            }

            return false;
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException rollbackException) {
                throw new RuntimeException(rollbackException);
            }
            logger.error("Error saving order", e);
            throw new RuntimeException(e);
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private boolean save(OrderDTO orderDTO, Connection connection) throws SQLException {
        String save_item = "INSERT INTO Orders (order_date, order_id, customer_id, total, discount, cash) VALUES  (?,?,?,?,?,?);";

        var preparedStatement = connection.prepareStatement(save_item);
        preparedStatement.setDate(1, Date.valueOf(orderDTO.getOrder_date()));
        preparedStatement.setString(2, orderDTO.getOrder_id());
        preparedStatement.setString(3, orderDTO.getCustomer_id());
        preparedStatement.setDouble(4, orderDTO.getTotal());
        preparedStatement.setDouble(5, orderDTO.getDiscount());
        preparedStatement.setDouble(6, orderDTO.getCash());

        boolean result = preparedStatement.executeUpdate() != 0;
        if (result) {
            logger.info("Order information saved successfully: {}", orderDTO.getOrder_id());
        } else {
            logger.error("Failed to save order information: {}", orderDTO.getOrder_id());
        }
        return result;
    }

    public boolean delete(String orderId, Connection connection) {
        try {
            connection.setAutoCommit(false);

            ArrayList<OrderDetailsDTO> orderDetailsDTOS = new OrderDetailsDBProcess().getOrderDetails(orderId, connection);

            for (OrderDetailsDTO orderDetailsDTO : orderDetailsDTOS) {
                orderDetailsDTO.setQty(-orderDetailsDTO.getQty());
                if (!new ItemDBProcess().updateItemOrder(orderDetailsDTO, connection)) {
                    return false;
                }
            }

            if (new OrderDetailsDBProcess().deleteOrderDetails(orderId, connection)) {
                if (deleteOrder(orderId, connection)) {
                    connection.commit();
                    return true;
                }
            }

            connection.rollback();
            return false;

        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException rollbackException) {
                throw new RuntimeException(rollbackException);
            }
            logger.error("Error deleting order", e);
            throw new RuntimeException(e);
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private boolean deleteOrder(String orderId, Connection connection) throws SQLException {
        String deleteOrderQuery = "DELETE FROM Orders WHERE order_id = ?;";
        PreparedStatement deleteOrderStatement = connection.prepareStatement(deleteOrderQuery);
        deleteOrderStatement.setString(1, orderId);

        boolean result = deleteOrderStatement.executeUpdate() != 0;
        if (result) {
            logger.info("Order information deleted successfully: {}", orderId);
        } else {
            logger.error("Failed to delete order information: {}", orderId);
        }
        return result;
    }

    public CombinedOrderDTO getOrder(String orderId, Connection connection) {
        try {
            String getOrderQuery = "SELECT * FROM Orders WHERE order_id = ?;";

            PreparedStatement getOrderStatement = connection.prepareStatement(getOrderQuery);
            getOrderStatement.setString(1, orderId);
            ResultSet resultSet = getOrderStatement.executeQuery();

            OrderDTO orderDTO = new OrderDTO();

            if (resultSet.next()) {
                orderDTO.setOrder_date(String.valueOf(resultSet.getDate("order_date")));
                orderDTO.setOrder_id(resultSet.getString("order_id"));
                orderDTO.setCustomer_id(resultSet.getString("customer_id"));
                orderDTO.setTotal(resultSet.getDouble("total"));
                orderDTO.setDiscount(resultSet.getDouble("discount"));
                orderDTO.setCash(resultSet.getDouble("cash"));
            }

            ArrayList<OrderDetailsDTO> orderDetailsDTOS = new OrderDetailsDBProcess().getOrderDetails(orderId, connection);

            return new CombinedOrderDTO(orderDTO, orderDetailsDTOS);

        } catch (SQLException e) {
            logger.error("Error retrieving order information", e);
            throw new RuntimeException(e);
        }
    }

    public List<CombinedOrderDTO> getAllOrders(Connection connection) {
        try {
            String getOrderQuery = "SELECT * FROM Orders;";

            PreparedStatement getOrderStatement = connection.prepareStatement(getOrderQuery);
            ResultSet resultSet = getOrderStatement.executeQuery();

            ArrayList<CombinedOrderDTO> combinedOrderDTOS = new ArrayList<>();

            while (resultSet.next()) {
                OrderDTO orderDTO = new OrderDTO();
                orderDTO.setOrder_date(String.valueOf(resultSet.getDate("order_date")));
                orderDTO.setOrder_id(resultSet.getString("order_id"));
                orderDTO.setCustomer_id(resultSet.getString("customer_id"));
                orderDTO.setTotal(resultSet.getDouble("total"));
                orderDTO.setDiscount(resultSet.getDouble("discount"));
                orderDTO.setCash(resultSet.getDouble("cash"));
                ArrayList<OrderDetailsDTO> orderDetailsDTOS = new OrderDetailsDBProcess().getOrderDetails(orderDTO.getOrder_id(), connection);
                combinedOrderDTOS.add(new CombinedOrderDTO(orderDTO, orderDetailsDTOS));
            }

            logger.info("Retrieved all orders successfully");
            return combinedOrderDTOS;

        } catch (SQLException e) {
            logger.error("Error retrieving all orders", e);
            throw new RuntimeException(e);
        }
    }

    public boolean updateOrder(CombinedOrderDTO combinedOrderDTO, Connection connection) {
        try {
            connection.setAutoCommit(false);

            ArrayList<OrderDetailsDTO> orderDetailsDTOS = new OrderDetailsDBProcess().getOrderDetails(
                    combinedOrderDTO.getOrderDTO().getOrder_id(), connection);

            for (OrderDetailsDTO orderDetailsDTO : orderDetailsDTOS) {
                orderDetailsDTO.setQty(-orderDetailsDTO.getQty());
                if (!new ItemDBProcess().updateItemOrder(orderDetailsDTO, connection)) {
                    return false;
                }
            }

            if (update(combinedOrderDTO.getOrderDTO(), connection)) {

                for (OrderDetailsDTO orderDetailsDTO : combinedOrderDTO.getOrderDetailsDTOS()) {
                    boolean isUpdatedOrderDetails = new OrderDetailsDBProcess().updateOrderDetails(orderDetailsDTO, connection);

                    if (isUpdatedOrderDetails) {
                        boolean isUpdatedItemDetails = new ItemDBProcess().updateItemOrder(orderDetailsDTO, connection);

                        if (!isUpdatedItemDetails) {
                            return false;
                        }

                    } else {
                        return false;
                    }
                }

                connection.commit();
                return true;
            }

            return false;
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException rollbackException) {
                throw new RuntimeException(rollbackException);
            }
            logger.error("Error updating order", e);
            throw new RuntimeException(e);
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private boolean update(OrderDTO orderDTO, Connection connection) throws SQLException {
        String update_item = "UPDATE Orders SET order_date=?, customer_id=?, total=?, discount=?, cash=? WHERE order_id=?;";

        var preparedStatement = connection.prepareStatement(update_item);
        preparedStatement.setDate(1, Date.valueOf(orderDTO.getOrder_date()));
        preparedStatement.setString(2, orderDTO.getCustomer_id());
        preparedStatement.setDouble(3, orderDTO.getTotal());
        preparedStatement.setDouble(4, orderDTO.getDiscount());
        preparedStatement.setDouble(5, orderDTO.getCash());
        preparedStatement.setString(6, orderDTO.getOrder_id());

        boolean result = preparedStatement.executeUpdate() != 0;
        if (result) {
            logger.info("Order information updated successfully: {}", orderDTO.getOrder_id());
        } else {
            logger.error("Failed to update order information: {}", orderDTO.getOrder_id());
        }
        return result;
    }

}
