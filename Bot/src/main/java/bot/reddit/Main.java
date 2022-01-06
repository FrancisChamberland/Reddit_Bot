package bot.reddit;

import com.google.gson.*;
import javafx.geometry.Pos;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class Main {
    public static void main( String[] args ) throws IOException, InterruptedException {
        URL url = new URL("https://www.reddit.com/r/memes/.json?");
        HttpURLConnection connection = (HttpURLConnection)url.openConnection();
        connection.setRequestProperty("User-agent", "bot");

        InputStream inputStream = connection.getInputStream();
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
        JsonObject jsonObject = JsonParser.parseReader(inputStreamReader).getAsJsonObject();
        JsonObject jsonData = jsonObject.getAsJsonObject("data");
        JsonArray jsonArray = jsonData.get("children").getAsJsonArray();

        for (JsonElement jsonElement : jsonArray){
            JsonObject postJson = (JsonObject)jsonElement.getAsJsonObject().get("data");
            String title = postJson.get("title").getAsString();
            int score = postJson.get("score").getAsInt();
            int commentCount = postJson.get("num_comments").getAsInt();
            double upvoteRatio = postJson.get("upvote_ratio").getAsInt();
            boolean stickied = postJson.get("stickied").getAsBoolean();
            String subreddit = postJson.get("subreddit").getAsString();
            String commentUrl = postJson.get("permalink").getAsString();
            Post post = new Post(subreddit, title, score, commentCount, upvoteRatio, stickied, commentUrl);
            System.out.println(post.toString());
        }

    }
}
