package backend_service.shop.dto.request;

import lombok.Getter;

@Getter
public class AddressRequestDTO {
    private String apartmentNumber;
    private String floor;
    private String building;
    private String streetNumber;
    private String street;
    private String city;
    private String country;
    private Integer addressType;
}
