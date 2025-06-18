package backend_service.shop.util;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum ProductStatus {
    @JsonProperty("ACTIVE")
    ACTIVE,
    @JsonProperty("INACTIVE")
    INACTIVE
}
