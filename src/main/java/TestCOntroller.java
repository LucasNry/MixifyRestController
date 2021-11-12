import annotations.GetOperation;
import model.HttpResponse;

public class TestCOntroller {

    @GetOperation(endpoint = "/test")
    public HttpResponse get() {
        return HttpResponse.builder().build();
    }
}
