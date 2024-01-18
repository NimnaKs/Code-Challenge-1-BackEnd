package lk.ijse.codingchallengejavaee.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data

public class CustomerDTO {
    private String customerId;
    private String name;
    private String address;
    private String contact;
    private String email;
}
