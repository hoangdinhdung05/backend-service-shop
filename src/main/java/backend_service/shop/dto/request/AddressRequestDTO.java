package backend_service.shop.dto.request;

import lombok.Getter;
import java.io.Serializable;

@Getter
public class AddressRequestDTO implements Serializable {
    private String apartmentNumber;
    private String floor;
    private String building;
    private String streetNumber;
    private String street;
    private String city;
    private String country;
    private Integer addressType;
}
