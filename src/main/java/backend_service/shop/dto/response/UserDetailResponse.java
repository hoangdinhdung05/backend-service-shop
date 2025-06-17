package backend_service.shop.dto.response;

import backend_service.shop.util.Gender;
import backend_service.shop.util.UserStatus;
import lombok.*;
import java.io.Serializable;
import java.util.Date;

@Getter
@Builder
public class UserDetailResponse implements Serializable {
    private Long id;

    private String firstName;

    private String lastName;

    private String email;

    private String phone;

    private Date dateOfBirth;

    private Gender gender;

    private String username;

    private String type;

    private UserStatus status;

}
