package lk.ijse.codingchallengejavaee.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data

public class ItemDTO {
    private String item_code;
    private String item_name;
    private double price;
    private int qty_on_hand;
}
