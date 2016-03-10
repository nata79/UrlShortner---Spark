import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import spark.Request;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import static spark.Spark.*;

public class UrlShortner {
    public static void main(String[] args) {
        Map<String, Url> urlsById  = new ConcurrentHashMap<String, Url>();
        Map<String, Url> urlsByUrl = new ConcurrentHashMap<String, Url>();

        post("/urls", (request, response) -> {
            Map<String, String> body = parseBody(request);

            if(!body.containsKey("originalUrl")) {
                response.status(400);
                return "";
            }

            String originalUrl = body.get("originalUrl");
            Url newUrl = null;

            if (urlsByUrl.containsKey(originalUrl)) {
                newUrl = urlsByUrl.get(originalUrl);
            } else {
                newUrl = new Url(originalUrl);
                urlsById.put(newUrl.id, newUrl);
                urlsByUrl.put(newUrl.originalUrl, newUrl);
            }

            response.type("application/json");
            return toJson(newUrl);
        });

        get("/urls/:id", (request, response) -> {
            String id = request.params("id");

            if (urlsById.containsKey(id)) {
                Url url = urlsById.get(id);

                Gson gson = new Gson();
                String json = gson.toJson(url);

                response.type("application/json");
                return json;
            } else {
                response.status(404);
                return "";
            }
        });

        get("/u/:id", ((request, response) -> {
            String id = request.params("id");

            if (urlsById.containsKey(id)) {
                Url url = urlsById.get(id);

                response.redirect(url.originalUrl);
                return "";
            } else {
                response.status(404);
                return "";
            }
        }));
    }

    private static Map<String, String> parseBody(Request request) {
        Gson gson = new Gson();
        Type type = new TypeToken<Map<String, String>>(){}.getType();

        return gson.fromJson(request.body(), type);
    }

    private static String toJson(Object o) {
        Gson gson = new Gson();
        return gson.toJson(o);
    }
}
