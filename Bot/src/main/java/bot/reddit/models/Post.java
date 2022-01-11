package bot.reddit.models;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Post {
    public JsonObject jsonPost;
    public String subreddit;
    public String title;
    public String authorName;
    public int score;
    public int commentCount;
    public double upvoteRatio;
    public boolean isStickied;
    public String commentUrl;
    public List<String> imagesUrls;

    public Post(JsonObject jsonPost){
        this.jsonPost = jsonPost.getAsJsonObject("data");
        setProperties();
    }

    public void setProperties(){
        title = jsonPost.get("title").getAsString();
        authorName = jsonPost.get("author").getAsString();
        score = jsonPost.get("score").getAsInt();
        commentCount = jsonPost.get("num_comments").getAsInt();
        upvoteRatio = jsonPost.get("upvote_ratio").getAsInt();
        isStickied = jsonPost.get("stickied").getAsBoolean();
        subreddit = jsonPost.get("subreddit").getAsString();
        commentUrl = "https://www.reddit.com/"
                + jsonPost.get("permalink").getAsString() + ".json";
        setImages();
    }

    public void setImages(){
        if (hasOneImage()){
            String imageUrl = getImageUrl();
            imagesUrls = Collections.singletonList(imageUrl);
        } else if (hasMultipleImages()){
            imagesUrls = getMultipleImagesUrls();
        }
    }

    public boolean hasOneImage(){
        return !jsonPost.get("is_self").getAsBoolean()
                && jsonPost.get("preview") != null;
    }

    public boolean hasMultipleImages(){
        return !jsonPost.get("is_self").getAsBoolean()
                && jsonPost.get("media_metadata") != null;
    }

    public String getImageUrl(){
        String encodedImageUrl = getJsonImageUrl().get("url").getAsString();
        return decodeImageUrl(encodedImageUrl);
    }

    public List<String> getMultipleImagesUrls(){
        List<String> imagesUrls = new ArrayList<>();
        List<JsonObject> jsonImages = getMultipleJsonImagesUrls();
        for (JsonObject jsonImage : jsonImages){
            String encodedImageUrl = jsonImage.get("u").getAsString();
            imagesUrls.add(decodeImageUrl(encodedImageUrl));
        }
        return imagesUrls;
    }

    public JsonObject getJsonImageUrl(){
        JsonObject jsonPreview = jsonPost.getAsJsonObject("preview");
        JsonArray jsonImages = jsonPreview.getAsJsonArray("images");
        JsonObject jsonImage = jsonImages.get(0).getAsJsonObject();
        return jsonImage.getAsJsonObject("source");
    }

    public List<JsonObject> getMultipleJsonImagesUrls(){
        List<JsonObject> imagesUrls = new ArrayList<>();
        List<String> mediaIds = getMediaIds();
        JsonObject jsonMedia = jsonPost.getAsJsonObject("media_metadata");
        for (String mediaId : mediaIds){
            try {
                JsonObject jsonImage = jsonMedia.getAsJsonObject(mediaId);
                JsonArray jsonImageSizes = jsonImage.getAsJsonArray("p");
                JsonObject jsonImageUrl = jsonImageSizes.get(jsonImageSizes.size() - 1).getAsJsonObject();
                imagesUrls.add(jsonImageUrl);
            } catch (Exception ignored){}
        }
        return imagesUrls;
    }

    public List<String> getMediaIds(){
        List<String> mediaIds = new ArrayList<>();
        JsonObject jsonGalleryData = jsonPost.getAsJsonObject("gallery_data");
        JsonArray jsonItems = jsonGalleryData.getAsJsonArray("items");
        for (JsonElement jsonItem : jsonItems){
            JsonElement jsonMediaId = jsonItem.getAsJsonObject().get("media_id");
            mediaIds.add(jsonMediaId.getAsString());
        }
        return mediaIds;
    }

    public String decodeImageUrl(String imageUrl){
        return imageUrl.replace("amp;", "");
    }

    public String getImageMessage() {
        if (hasOneImage()){
            return buildImageMessage();
        } else {
            return "No image";
        }
    }

    public String buildImageMessage(){
        StringBuilder images = new StringBuilder();
        for (String imageUrl : imagesUrls){
            images.append(imageUrl).append("\n");
        }
        return images.substring(0, images.length() - 1);
    }

    @Override
    public String toString() {
        String titleDisplay = title;
        String authorDisplay = "Author : " + authorName;
        String scoreDisplay = "Score : " + score;
        String imageDisplay = getImageMessage();
        String commentCountDisplay = "Number of comments : " + commentCount;
        return String.format("%s\n%s\n%s\n%s\n%s\n",
                titleDisplay, authorDisplay, scoreDisplay, imageDisplay, commentCountDisplay);
    }
}
