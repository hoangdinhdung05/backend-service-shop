package backend_service.shop.util;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum CategoryStatus {
    @JsonProperty("ACTIVE")
    ACTIVE,
    @JsonProperty("NONE")
    NONE
}
