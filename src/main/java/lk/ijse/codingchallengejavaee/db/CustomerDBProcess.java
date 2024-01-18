package lk.ijse.codingchallengejavaee.db;

import lk.ijse.codingchallengejavaee.dto.CustomerDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class CustomerDBProcess {
    final static Logger logger = LoggerFactory.getLogger(CustomerDBProcess.class);

    public String getCustomerId(Connection connection) {
        try {

            PreparedStatement preparedStatement = connection.prepareStatement(
                    "SELECT MAX(customerId) as last_customer_id FROM customer"
            );

            ResultSet resultSet = preparedStatement.executeQuery();

            return (resultSet.next() && resultSet.getString("last_customer_id") != null) ?
                    "cust-" + String.format("%03d", Integer.parseInt(resultSet.getString("last_customer_id").substring(5)) + 1)
                    : "cust-001";

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean saveCustomer(CustomerDTO customerDTO, Connection connection) {
        try {
            String save_customer = "INSERT INTO Customer(customerId, name, address, contact, email) VALUES (?,?,?,?,?);";
            var preparedStatement = connection.prepareStatement(save_customer);
            preparedStatement.setString(1, customerDTO.getCustomerId());
            preparedStatement.setString(2, customerDTO.getName());
            preparedStatement.setString(3, customerDTO.getAddress());
            preparedStatement.setString(4, customerDTO.getContact());
            preparedStatement.setString(5, customerDTO.getEmail());

            return preparedStatement.executeUpdate() != 0;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean updateCustomer(CustomerDTO customerDTO, Connection connection) {
        try {
            String update_customer = "UPDATE Customer SET name = ?, address = ?, contact = ?, email = ? WHERE customerId = ?;";
            var preparedStatement = connection.prepareStatement(update_customer);
            preparedStatement.setString(1, customerDTO.getName());
            preparedStatement.setString(2, customerDTO.getAddress());
            preparedStatement.setString(3, customerDTO.getContact());
            preparedStatement.setString(4, customerDTO.getEmail());
            preparedStatement.setString(5, customerDTO.getCustomerId());

            return preparedStatement.executeUpdate() != 0;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean deleteCustomer(String customerId, Connection connection) {
        try {
            String delete_customer = "DELETE FROM Customer WHERE customerId = ?;";
            var preparedStatement = connection.prepareStatement(delete_customer);
            preparedStatement.setString(1, customerId);

            return preparedStatement.executeUpdate() != 0;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public ArrayList<CustomerDTO> getAllCustomers(Connection connection) {
        try {
            String get_all = "SELECT customerId, name, address, contact, email FROM Customer;";
            var preparedStatement = connection.prepareStatement(get_all);

            ResultSet resultSet = preparedStatement.executeQuery();

            ArrayList<CustomerDTO> customerDTOs = new ArrayList<>();

            while (resultSet.next()) {
                CustomerDTO customerDTO = new CustomerDTO(
                        resultSet.getString("customerId"),
                        resultSet.getString("name"),
                        resultSet.getString("address"),
                        resultSet.getString("contact"),
                        resultSet.getString("email")
                );
                customerDTOs.add(customerDTO);
            }

            return customerDTOs;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public CustomerDTO getCustomer(Connection connection, String customerId) {
        String get_customer = "SELECT customerId, name, address, contact, email FROM Customer WHERE customerId = ?;";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(get_customer);
            preparedStatement.setString(1, customerId);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return new CustomerDTO(
                        resultSet.getString("customerId"),
                        resultSet.getString("name"),
                        resultSet.getString("address"),
                        resultSet.getString("contact"),
                        resultSet.getString("email")
                );
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }
}
