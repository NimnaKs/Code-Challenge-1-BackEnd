package lk.ijse.codingchallengejavaee.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class OrderDTO {
    private Date order_date;
    private String order_id;
    private String customer_id;
    private double total;
    private double discount;
    private double cash;
}
