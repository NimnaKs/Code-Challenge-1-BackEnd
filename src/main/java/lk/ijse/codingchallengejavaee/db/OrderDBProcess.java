package lk.ijse.codingchallengejavaee.db;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

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

}
