package backend_service.shop.util;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum CartStatus {
    @JsonProperty("ACTIVE")
    ACTIVE,
    @JsonProperty("INACTIVE")
    INACTIVE
}
