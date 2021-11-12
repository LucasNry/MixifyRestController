package model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum ConnectionType {
    KEEP_ALIVE("keep-alive"),
    CLOSE("close");

    @Getter
    private String value;

    public static ConnectionType fromValue(String value) {
        for (ConnectionType connectionType : ConnectionType.values()) {
            if (connectionType.value.equals(value)) {
                return connectionType;
            }
        }

        return CLOSE;
    }
}
