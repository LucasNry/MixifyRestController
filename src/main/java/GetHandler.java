import annotations.GetOperation;
import model.HttpResponse;
import model.QueryParameters;
import model.RequestStatus;

public class GetHandler {

    public GetHandler() {
    }

    @GetOperation(endpoint = "/test")
    public HttpResponse getHandler(QueryParameters queryParameters, String body) {
        return HttpResponse
                .builder()
                .requestStatus(RequestStatus.OK)
                .build();
    }
}
