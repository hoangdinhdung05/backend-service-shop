package backend_service.shop.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import java.time.LocalDateTime;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "BlacklistedToken")
@Table(name = "tbl_blacklisted_token")
public class BlacklistedToken extends AbstractEntity<Long> {

    @Column(length = 1000, nullable = false, unique = true)
    private String token;

    private LocalDateTime expiredAt;

}
