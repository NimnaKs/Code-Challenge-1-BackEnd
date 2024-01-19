package lk.ijse.codingchallengejavaee.db;

import lk.ijse.codingchallengejavaee.dto.CustomerDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;

public class CustomerDBProcess {
    private static final Logger logger = LoggerFactory.getLogger(CustomerDBProcess.class);

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
            logger.error("Error retrieving customer ID", e);
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

            boolean result = preparedStatement.executeUpdate() != 0;
            if (result) {
                logger.info("Customer information saved successfully: {}", customerDTO.getCustomerId());
            } else {
                logger.error("Failed to save customer information: {}", customerDTO.getCustomerId());
            }
            return result;

        } catch (SQLException e) {
            logger.error("Error saving customer information", e);
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

            boolean result = preparedStatement.executeUpdate() != 0;
            if (result) {
                logger.info("Customer information updated successfully: {}", customerDTO.getCustomerId());
            } else {
                logger.error("Failed to update customer information: {}", customerDTO.getCustomerId());
            }
            return result;

        } catch (SQLException e) {
            logger.error("Error updating customer information", e);
            throw new RuntimeException(e);
        }
    }

    public boolean deleteCustomer(String customerId, Connection connection) {
        try {
            String delete_customer = "DELETE FROM Customer WHERE customerId = ?;";
            var preparedStatement = connection.prepareStatement(delete_customer);
            preparedStatement.setString(1, customerId);

            boolean result = preparedStatement.executeUpdate() != 0;
            if (result) {
                logger.info("Customer information deleted successfully: {}", customerId);
            } else {
                logger.error("Failed to delete customer information: {}", customerId);
            }
            return result;

        } catch (SQLException e) {
            logger.error("Error deleting customer information", e);
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

            logger.info("Retrieved all customers successfully");
            return customerDTOs;

        } catch (SQLException e) {
            logger.error("Error retrieving all customers", e);
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
                CustomerDTO customerDTO = new CustomerDTO(
                        resultSet.getString("customerId"),
                        resultSet.getString("name"),
                        resultSet.getString("address"),
                        resultSet.getString("contact"),
                        resultSet.getString("email")
                );
                logger.info("Retrieved customer successfully: {}", customerId);
                return customerDTO;
            }
        } catch (SQLException e) {
            logger.error("Error retrieving customer information", e);
            throw new RuntimeException(e);
        }
        logger.warn("Customer not found: {}", customerId);
        return null;
    }

    public ArrayList<String> getAllCustomerIds(Connection connection) {
        try {
            String get_all = "SELECT customerId FROM Customer;";
            var preparedStatement = connection.prepareStatement(get_all);

            ResultSet resultSet = preparedStatement.executeQuery();

            ArrayList<String> customerIds = new ArrayList<>();

            while (resultSet.next()) {
                customerIds.add(resultSet.getString("customerId"));
            }

            logger.info("Retrieved all customer IDs successfully");
            return customerIds;

        } catch (SQLException e) {
            logger.error("Error retrieving all customer IDs", e);
            throw new RuntimeException(e);
        }
    }
}
