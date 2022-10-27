package model;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum Protocol {
    HTTP(HttpRequest.class, HttpResponse.class);

    public final Class<? extends ProtocolRequest> requestClass;

    public final Class<? extends ProtocolResponse> responseClass;
}
