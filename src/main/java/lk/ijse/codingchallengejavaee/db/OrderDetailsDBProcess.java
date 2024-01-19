package lk.ijse.codingchallengejavaee.db;

import lk.ijse.codingchallengejavaee.dto.OrderDetailsDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;

public class OrderDetailsDBProcess {

    final static Logger logger = LoggerFactory.getLogger(OrderDetailsDBProcess.class);

    public boolean saveOrderDetails(OrderDetailsDTO orderDetailsDTO, Connection connection) {

        try {

            String save_item = "INSERT INTO OrderDetails (order_id, item_id, price, qty) VALUES  (?,?,?,?);";

            var preparedStatement = connection.prepareStatement(save_item);
            preparedStatement.setString(1,  orderDetailsDTO.getOrder_id());
            preparedStatement.setString(2, orderDetailsDTO.getItem_id());
            preparedStatement.setDouble(3,orderDetailsDTO.getPrice());
            preparedStatement.setInt(4,orderDetailsDTO.getQty());

            return preparedStatement.executeUpdate() != 0;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean deleteOrderDetails(String orderId, Connection connection) {
        String deleteOrderDetailsQuery = "DELETE FROM OrderDetails WHERE order_id = ?;";
        try {
            PreparedStatement deleteOrderDetailsStatement = connection.prepareStatement(deleteOrderDetailsQuery);
            deleteOrderDetailsStatement.setString(1, orderId);
            return deleteOrderDetailsStatement.executeUpdate() != 0;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public ArrayList<OrderDetailsDTO> getOrderDetails(String orderId, Connection connection) {
        ArrayList<OrderDetailsDTO> orderDetailsList = new ArrayList<>();

        try {
            String getOrderDetailsQuery = "SELECT * FROM OrderDetails WHERE order_id = ?;";
            try (PreparedStatement getOrderDetailsStatement = connection.prepareStatement(getOrderDetailsQuery)) {
                getOrderDetailsStatement.setString(1, orderId);
                ResultSet resultSet = getOrderDetailsStatement.executeQuery();

                while (resultSet.next()) {
                    OrderDetailsDTO orderDetailsDTO = new OrderDetailsDTO();
                    orderDetailsDTO.setOrder_id(resultSet.getString("order_id"));
                    orderDetailsDTO.setItem_id(resultSet.getString("item_id"));
                    orderDetailsDTO.setPrice(resultSet.getDouble("price"));
                    orderDetailsDTO.setQty(resultSet.getInt("qty"));
                    orderDetailsList.add(orderDetailsDTO);
                }
            }
            return orderDetailsList;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
