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
        this.setProperties();
    }

    public void setProperties(){
        this.title = this.jsonPost.get("title").getAsString();
        this.authorName = this.jsonPost.get("author").getAsString();
        this.score = this.jsonPost.get("score").getAsInt();
        this.commentCount = this.jsonPost.get("num_comments").getAsInt();
        this.upvoteRatio = this.jsonPost.get("upvote_ratio").getAsInt();
        this.isStickied = this.jsonPost.get("stickied").getAsBoolean();
        this.subreddit = this.jsonPost.get("subreddit").getAsString();
        this.commentUrl = "https://www.reddit.com/"
                + this.jsonPost.get("permalink").getAsString() + ".json";
        this.setImages();
    }

    public void setImages(){
        if (this.hasOneImage()){
            String imageUrl = this.getImageUrl();
            this.imagesUrls = Collections.singletonList(imageUrl);
        } else if (this.hasMultipleImages()){
            this.imagesUrls = this.getMultipleImagesUrls();
        }
    }

    public boolean hasOneImage(){
        return !this.jsonPost.get("is_self").getAsBoolean()
                && jsonPost.get("preview") != null;
    }

    public boolean hasMultipleImages(){
        return !jsonPost.get("is_self").getAsBoolean()
                && jsonPost.get("media_metadata") != null;
    }

    public String getImageUrl(){
        String encodedImageUrl = this.getJsonImageUrl().get("url").getAsString();
        return decodeImageUrl(encodedImageUrl);
    }

    public List<String> getMultipleImagesUrls(){
        List<String> imagesUrls = new ArrayList<>();
        List<JsonObject> jsonImages = this.getMultipleJsonImagesUrls();
        for (JsonObject jsonImage : jsonImages){
            String encodedImageUrl = jsonImage.get("u").getAsString();
            imagesUrls.add(this.decodeImageUrl(encodedImageUrl));
        }
        return imagesUrls;
    }

    public JsonObject getJsonImageUrl(){
        JsonObject jsonPreview = this.jsonPost.getAsJsonObject("preview");
        JsonArray jsonImages = jsonPreview.getAsJsonArray("images");
        JsonObject jsonImage = jsonImages.get(0).getAsJsonObject();
        return jsonImage.getAsJsonObject("source");
    }

    public List<JsonObject> getMultipleJsonImagesUrls(){
        List<JsonObject> imagesUrls = new ArrayList<>();
        List<String> mediaIds = this.getMediaIds();
        JsonObject jsonMedia = this.jsonPost.getAsJsonObject("media_metadata");
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
        JsonObject jsonGalleryData = this.jsonPost.getAsJsonObject("gallery_data");
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
        if (this.hasOneImage()){
            return this.buildImageMessage();
        } else {
            return "No image";
        }
    }

    public String buildImageMessage(){
        StringBuilder images = new StringBuilder();
        for (String imageUrl : this.imagesUrls){
            images.append(imageUrl).append("\n");
        }
        return images.substring(0, images.length() - 1);
    }

    @Override
    public String toString() {
        String titleDisplay = this.title;
        String authorDisplay = "Author : " + this.authorName;
        String scoreDisplay = "Score : " + this.score;
        String imageDisplay = this.getImageMessage();
        String commentCountDisplay = "Number of comments : " + this.commentCount;
        return String.format("%s\n%s\n%s\n%s\n%s\n",
                titleDisplay, authorDisplay, scoreDisplay, imageDisplay, commentCountDisplay);
    }
}
