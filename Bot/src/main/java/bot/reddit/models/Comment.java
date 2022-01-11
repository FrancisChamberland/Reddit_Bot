package bot.reddit.models;

import com.google.gson.JsonObject;

public class Comment {
    public JsonObject jsonComment;
    public String authorName;
    public int score;
    public String text;

    public Comment(JsonObject jsonComment){
        this.jsonComment = jsonComment;
        setProperties();
    }

    public void setProperties(){
        authorName = jsonComment.get("author").getAsString();
        score = jsonComment.get("score").getAsInt();
        text = jsonComment.get("body").getAsString();
    }

    @Override
    public String toString() {
        String authorDisplay = "Author : " + authorName;
        String scoreDisplay = "Score : " + score;
        String textDisplay = text;
        return String.format("%s\n%s\n%s\n",
                authorDisplay, scoreDisplay, textDisplay);
    }
}
