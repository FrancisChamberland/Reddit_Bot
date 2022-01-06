package bot.reddit.models;

import com.google.gson.JsonObject;

public class Comment {
    public JsonObject jsonComment;
    public String authorName;
    public int score;
    public String text;

    public Comment(JsonObject jsonComment){
        this.jsonComment = jsonComment;
        this.setProperties();;
    }

    public void setProperties(){
        this.authorName = this.jsonComment.get("author").getAsString();
        this.score = this.jsonComment.get("score").getAsInt();
        this.text = this.jsonComment.get("body").getAsString();
    }

    @Override
    public String toString() {
        String authorDisplay = "Author : " + this.authorName;
        String scoreDisplay = "Score : " + this.score;
        String textDisplay = this.text;
        return String.format("%s\n%s\n%s\n",
                authorDisplay, scoreDisplay, textDisplay);
    }
}
