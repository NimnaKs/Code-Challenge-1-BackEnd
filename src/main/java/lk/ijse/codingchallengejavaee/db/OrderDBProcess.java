package lk.ijse.codingchallengejavaee.db;

import lk.ijse.codingchallengejavaee.dto.CombinedOrderDTO;
import lk.ijse.codingchallengejavaee.dto.OrderDTO;
import lk.ijse.codingchallengejavaee.dto.OrderDetailsDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.List;

public class OrderDBProcess {

    final static Logger logger = LoggerFactory.getLogger(OrderDBProcess.class);

    public String generateOrderId(Connection connection) {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "SELECT MAX(order_id) as last_order_id FROM Orders"
            );

            ResultSet resultSet = preparedStatement.executeQuery();

            return (resultSet.next() && resultSet.getString("last_order_id") != null) ?
                    "order-" + String.format("%03d", Integer.parseInt(resultSet.getString("last_item_code").substring(6)) + 1)
                    : "order-001";

        } catch (SQLException e) {
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
            throw new RuntimeException(e);
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

    }

    private boolean save(OrderDTO orderDTO, Connection connection) {
        try {
            String save_item = "INSERT INTO Orders (order_date, order_id, customer_id, total, discount, cash) VALUES  (?,?,?,?,?,?);";

            var preparedStatement = connection.prepareStatement(save_item);
            preparedStatement.setDate(1, Date.valueOf(orderDTO.getOrder_date()));
            preparedStatement.setString(2, orderDTO.getOrder_id());
            preparedStatement.setString(3, orderDTO.getCustomer_id());
            preparedStatement.setDouble(4, orderDTO.getTotal());
            preparedStatement.setDouble(5, orderDTO.getDiscount());
            preparedStatement.setDouble(6, orderDTO.getCash());

            return preparedStatement.executeUpdate() != 0;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean delete(String orderId, Connection connection) {
        try {
            connection.setAutoCommit(false);

            if(new OrderDetailsDBProcess().deleteOrderDetails(orderId,connection)){
                if(deleteOrder(orderId,connection)){
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
            throw new RuntimeException(e);
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private boolean deleteOrder(String orderId, Connection connection) {
        String deleteOrderQuery = "DELETE FROM Orders WHERE order_id = ?;";
        try {
            PreparedStatement deleteOrderStatement = connection.prepareStatement(deleteOrderQuery);
            deleteOrderStatement.setString(1, orderId);
            return deleteOrderStatement.executeUpdate() != 0;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public OrderDTO getOrder(String orderId, Connection connection) {
    }

    public List<OrderDTO> getAllOrders(Connection connection) {
    }

    public boolean updateOrder(CombinedOrderDTO combinedOrderDTO, Connection connection) {
    }
}
