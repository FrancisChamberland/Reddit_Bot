package bot.reddit.readers;

import bot.reddit.models.Post;
import bot.reddit.exceptions.ReaderException;
import bot.reddit.exceptions.UrlException;
import bot.reddit.jsonExtractor;
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
    public static final String IMAGE_PATH = "SubredditImages/";
    public static final String ERROR_SUBREDDIT = "Subreddit is invalid : %s\n";

    public String url;
    public String subreddit;
    public int numberOfPost;
    public JsonObject jsonSubreddit;
    public List<Post> posts;

    public SubredditReader(String subreddit, int numberOfPost) throws ReaderException {
        try {
            this.subreddit = subreddit;
            this.numberOfPost = numberOfPost;
            this.url = this.getUrl();
            this.jsonSubreddit = this.getJsonSubreddit();
            this.posts = this.getPosts();
            deleteFolderContent(IMAGE_PATH);
        } catch (UrlException e){
            throw new ReaderException(String.format(ERROR_SUBREDDIT, this.subreddit));
        }
    }

    public void printPostImages(){
        for (Post post : this.posts){
            if (post.hasOneImage() || post.hasMultipleImages())
                System.out.println(post.getImageMessage());
        }
    }

    public void printPosts(){
        for (Post post : this.posts){
            System.out.println(post.toString());
        }
    }

    public void printNotStickiedPosts(){
        for (Post post : this.posts){
            if (!post.isStickied)
                System.out.println(post.toString());
        }
    }

    public void savePostsImages(){
        for (Post post : this.posts){
            if (post.hasOneImage() || post.hasMultipleImages())
                for (String imageUrl : post.imagesUrls){
                    this.saveImageToFile(imageUrl);
                }
        }
    }

    private void saveImageToFile(String imageUrl){
        try (InputStream in = new URL(imageUrl).openStream()){
            String image = this.formatImageUrl(imageUrl);
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
        JsonObject jsonSubreddit = jsonExtractor.getJsonObjectFromUrl(url);
        return jsonSubreddit.getAsJsonObject("data");
    }

    private List<Post> getPosts(){
        List<Post> posts = new ArrayList<>();
        JsonArray jsonPosts = this.jsonSubreddit.get("children").getAsJsonArray();
        for (int i = 0; i < this.numberOfPost; i++){
            Post post = new Post(jsonPosts.get(i).getAsJsonObject());
            posts.add(post);
        }
        return posts;
    }

    private String getUrl(){
        return String.format("https://www.reddit.com/r/%s/.json?limit=%s", this.subreddit, this.numberOfPost);
    }
}
