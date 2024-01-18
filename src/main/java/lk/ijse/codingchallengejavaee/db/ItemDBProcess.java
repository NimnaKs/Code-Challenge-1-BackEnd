package lk.ijse.codingchallengejavaee.db;

import lk.ijse.codingchallengejavaee.dto.ItemDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.sql.*;
import java.util.ArrayList;

public class ItemDBProcess {
    final static Logger logger = LoggerFactory.getLogger(ItemDBProcess.class);

    public boolean saveItem(ItemDTO itemModel, Connection connection) {

        try {
            String save_item = "INSERT INTO Item(item_code, item_name, price, qty_on_hand) VALUES (?,?,?,?);";
            var preparedStatement = connection.prepareStatement(save_item);
            preparedStatement.setString(1, itemModel.getItem_code());
            preparedStatement.setString(2, itemModel.getItem_name());
            preparedStatement.setDouble(3, itemModel.getPrice());
            preparedStatement.setInt(4, itemModel.getQty_on_hand());

            return preparedStatement.executeUpdate() != 0;

        } catch (SQLException e) {
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

            return preparedStatement.executeUpdate() != 0;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean deleteItem(String itemCode, Connection connection) {
        try {
            String delete_item = "DELETE FROM Item WHERE item_code = ?;";
            var preparedStatement = connection.prepareStatement(delete_item);
            preparedStatement.setString(1, itemCode);

            return preparedStatement.executeUpdate() != 0;

        } catch (SQLException e) {
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

            return itemModels;

        } catch (SQLException e) {
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
                return new ItemDTO(
                        resultSet.getString("item_code"),
                        resultSet.getString("item_name"),
                        resultSet.getInt("qty_on_hand"),
                        resultSet.getDouble("price")
                );
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
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
            throw new RuntimeException(e);
        }
    }

}
