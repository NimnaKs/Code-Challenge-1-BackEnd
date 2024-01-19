package lk.ijse.codingchallengejavaee.db;

import lk.ijse.codingchallengejavaee.dto.ItemDTO;
import lk.ijse.codingchallengejavaee.dto.OrderDetailsDTO;
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

public class ItemDBProcess {
    private static final Logger logger = LoggerFactory.getLogger(ItemDBProcess.class);

    public boolean saveItem(ItemDTO itemModel, Connection connection) {
        try {
            String save_item = "INSERT INTO Item(item_code, item_name, price, qty_on_hand) VALUES (?,?,?,?);";
            var preparedStatement = connection.prepareStatement(save_item);
            preparedStatement.setString(1, itemModel.getItem_code());
            preparedStatement.setString(2, itemModel.getItem_name());
            preparedStatement.setDouble(3, itemModel.getPrice());
            preparedStatement.setInt(4, itemModel.getQty_on_hand());

            boolean result = preparedStatement.executeUpdate() != 0;
            if (result) {
                logger.info("Item information saved successfully: {}", itemModel.getItem_code());
            } else {
                logger.error("Failed to save item information: {}", itemModel.getItem_code());
            }
            return result;

        } catch (SQLException e) {
            logger.error("Error saving item information", e);
            throw new RuntimeException(e);
        }
    }

    public boolean updateItem(ItemDTO itemModel, Connection connection) {
        try {
            String update_item = "UPDATE Item SET item_name = ?, price = ?, qty_on_hand = ? WHERE item_code = ?;";
            var preparedStatement = connection.prepareStatement(update_item);
            preparedStatement.setString(1, itemModel.getItem_name());
            preparedStatement.setDouble(2, itemModel.getPrice());
            preparedStatement.setInt(3, itemModel.getQty_on_hand());
            preparedStatement.setString(4, itemModel.getItem_code());

            boolean result = preparedStatement.executeUpdate() != 0;
            if (result) {
                logger.info("Item information updated successfully: {}", itemModel.getItem_code());
            } else {
                logger.error("Failed to update item information: {}", itemModel.getItem_code());
            }
            return result;

        } catch (SQLException e) {
            logger.error("Error updating item information", e);
            throw new RuntimeException(e);
        }
    }

    public boolean deleteItem(String itemCode, Connection connection) {
        try {
            String delete_item = "DELETE FROM Item WHERE item_code = ?;";
            var preparedStatement = connection.prepareStatement(delete_item);
            preparedStatement.setString(1, itemCode);

            boolean result = preparedStatement.executeUpdate() != 0;
            if (result) {
                logger.info("Item information deleted successfully: {}", itemCode);
            } else {
                logger.error("Failed to delete item information: {}", itemCode);
            }
            return result;

        } catch (SQLException e) {
            logger.error("Error deleting item information", e);
            throw new RuntimeException(e);
        }
    }

    public ArrayList<ItemDTO> getAllItems(Connection connection) {
        try {
            String get_all = "SELECT item_code, item_name, qty_on_hand, price FROM Item;";
            var preparedStatement = connection.prepareStatement(get_all);

            ResultSet resultSet = preparedStatement.executeQuery();

            ArrayList<ItemDTO> itemModels = new ArrayList<>();

            while (resultSet.next()) {
                ItemDTO itemModel = new ItemDTO(
                        resultSet.getString("item_code"),
                        resultSet.getString("item_name"),
                        resultSet.getInt("qty_on_hand"),
                        resultSet.getDouble("price")
                );
                itemModels.add(itemModel);
            }

            logger.info("Retrieved all items successfully");
            return itemModels;

        } catch (SQLException e) {
            logger.error("Error retrieving all items", e);
            throw new RuntimeException(e);
        }
    }

    public ItemDTO getItem(String itemCode, Connection connection) {
        String get_item = "SELECT item_code, item_name, qty_on_hand, price FROM Item WHERE item_code = ?;";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(get_item);
            preparedStatement.setString(1, itemCode);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                ItemDTO itemDTO = new ItemDTO(
                        resultSet.getString("item_code"),
                        resultSet.getString("item_name"),
                        resultSet.getInt("qty_on_hand"),
                        resultSet.getDouble("price")
                );
                logger.info("Retrieved item successfully: {}", itemCode);
                return itemDTO;
            }
        } catch (SQLException e) {
            logger.error("Error retrieving item information", e);
            throw new RuntimeException(e);
        }
        logger.warn("Item not found: {}", itemCode);
        return null;
    }

    public String generateItemCode(Connection connection) {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "SELECT MAX(item_code) as last_item_code FROM Item"
            );

            ResultSet resultSet = preparedStatement.executeQuery();

            return (resultSet.next() && resultSet.getString("last_item_code") != null) ?
                    "item-" + String.format("%03d", Integer.parseInt(resultSet.getString("last_item_code").substring(5)) + 1)
                    : "item-001";

        } catch (SQLException e) {
            logger.error("Error generating item code", e);
            throw new RuntimeException(e);
        }
    }

    public ArrayList<String> getAllItemIds(Connection connection) {
        try {
            String get_all = "SELECT item_code FROM Item;";
            var preparedStatement = connection.prepareStatement(get_all);

            ResultSet resultSet = preparedStatement.executeQuery();

            ArrayList<String> itemIds = new ArrayList<>();

            while (resultSet.next()) {
                itemIds.add(resultSet.getString("item_code"));
            }

            logger.info("Retrieved all item IDs successfully");
            return itemIds;

        } catch (SQLException e) {
            logger.error("Error retrieving all item IDs", e);
            throw new RuntimeException(e);
        }
    }

    public boolean updateItemOrder(OrderDetailsDTO orderDetailsDTO, Connection connection) {
        try {
            String updateItemQtyQuery = "UPDATE Item SET qty_on_hand = qty_on_hand - ? WHERE item_code = ?;";

            PreparedStatement updateItemQtyStatement = connection.prepareStatement(updateItemQtyQuery);
            updateItemQtyStatement.setInt(1, orderDetailsDTO.getQty());
            updateItemQtyStatement.setString(2, orderDetailsDTO.getItem_id());

            boolean result = updateItemQtyStatement.executeUpdate() != 0;
            if (result) {
                logger.info("Item quantity updated successfully for order: {}", orderDetailsDTO.getOrder_id());
            } else {
                logger.error("Failed to update item quantity for order: {}", orderDetailsDTO.getOrder_id());
            }
            return result;

        } catch (SQLException e) {
            logger.error("Error updating item quantity for order", e);
            throw new RuntimeException(e);
        }
    }
}
