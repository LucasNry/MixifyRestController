package model;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum ConnectionType {
    KEEP_ALIVE("keep-alive"),
    CLOSE("close");

    private String value;

    public static ConnectionType fromValue(String value) {
        for (ConnectionType connectionType : ConnectionType.values()) {
            if (value.equals(connectionType.value)) {
                return connectionType;
            }
        }

        return CLOSE;
    }
}
