package lk.ijse.codingchallengejavaee.db;

import lk.ijse.codingchallengejavaee.dto.OrderDetailsDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;

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
}
