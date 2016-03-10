import java.util.UUID;

public class Url {
    public String id;
    public String originalUrl;

    public Url() {
    }

    public Url(String originalUrl) {
        this.originalUrl = originalUrl;
        this.id = UUID.randomUUID().toString().split("-")[0];
    }
}
