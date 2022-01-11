package bot.reddit.readers;

import bot.reddit.models.Post;
import bot.reddit.exceptions.ReaderException;
import bot.reddit.exceptions.UrlException;
import bot.reddit.utils.JsonGateway;
import com.google.gson.*;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class SubredditReader {
    public static final String IMAGE_PATH = "saves/";
    public static final String ERROR_SUBREDDIT = "Subreddit %s doesn't exist\n";

    public String url;
    public String subreddit;
    public int numberOfPost;
    public JsonObject jsonSubreddit;
    public List<Post> posts;

    public SubredditReader(String subreddit, int numberOfPost) throws ReaderException {
        try {
            this.subreddit = subreddit;
            this.numberOfPost = numberOfPost;
            url = getUrl();
            jsonSubreddit = getJsonSubreddit();
            posts = getPosts();
            deleteFolderContent(IMAGE_PATH);
        } catch (Exception e){
            throw new ReaderException(String.format(ERROR_SUBREDDIT, this.subreddit));
        }
    }

    public void printPostImages(){
        for (Post post : posts){
            if (post.hasOneImage() || post.hasMultipleImages())
                System.out.println(post.getImageMessage());
        }
    }

    public void printAllPosts(){
        for (Post post : posts){
            printPost(post);
        }
    }

    public void printNotStickiedPosts(){
        for (Post post : posts){
            if (!post.isStickied)
                printPost(post);
        }
    }

    public void printPost(Post post){
        System.out.printf("[%s] %s\n", posts.indexOf(post), post.toString());
    }

    public void savePostsImages(){
        for (Post post : posts){
            if (post.hasOneImage() || post.hasMultipleImages())
                for (String imageUrl : post.imagesUrls){
                    saveImageToFile(imageUrl);
                }
        }
    }

    private void saveImageToFile(String imageUrl){
        try (InputStream in = new URL(imageUrl).openStream()){
            String image = formatImageUrl(imageUrl);
            Files.copy(in, Paths.get(IMAGE_PATH + image));
            System.out.printf("Saving : %s\n", imageUrl);
        } catch (IOException e) {
            System.out.printf("Could not save : %s\n", imageUrl);
        }
    }

    private static void deleteFolderContent(String folderPath) {
        File folder = new File(folderPath);
        File[] files = folder.listFiles();
        if (files != null) {
            for(File file: files) {
                if (!file.delete())
                    System.out.printf("Could not delete file : %s\n", file);
            }
        }
    }

    private String formatImageUrl(String imageUrl){
        String imageFormatted = imageUrl.split("/")[3];
        return imageFormatted.split("\\?")[0];
    }

    private JsonObject getJsonSubreddit() throws UrlException {
        JsonObject jsonSubreddit = JsonGateway.getJsonObjectFromUrl(url);
        return jsonSubreddit.getAsJsonObject("data");
    }

    private List<Post> getPosts(){
        List<Post> posts = new ArrayList<>();
        JsonArray jsonPosts = jsonSubreddit.get("children").getAsJsonArray();
        for (int i = 0; i < numberOfPost; i++){
            Post post = new Post(jsonPosts.get(i).getAsJsonObject());
            posts.add(post);
        }
        return posts;
    }

    private String getUrl(){
        return String.format("https://www.reddit.com/r/%s/.json?limit=%s", subreddit, numberOfPost);
    }
}
