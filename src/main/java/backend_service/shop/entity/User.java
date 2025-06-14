package backend_service.shop.entity;

import backend_service.shop.dto.validator.EmailPattern;
import backend_service.shop.util.Gender;
import backend_service.shop.util.UserStatus;
import backend_service.shop.util.UserType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "User")
@Table(name = "tbl_user")
public class User extends AbstractEntity {

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "username")
    private String username;

    @Column(name = "email")
    private String email;

    @Column(name = "password")
    private String password;

    @Column(name = "gender")
    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "user_type")
    @Enumerated(EnumType.STRING)
    private UserType userType;

    @Column(name = "user_status")
    @Enumerated(EnumType.STRING)
    private UserStatus userStatus;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, mappedBy = "user")
    @JsonIgnore
    private Set<Address> addresses = new HashSet<>();

    public void saveAddress(Address address) {
        if(address != null) {
            if(addresses == null) {
                addresses = new HashSet<>();
            }
            addresses.add(address);
            address.setUser(this); //save user_id
        }
    }

}
