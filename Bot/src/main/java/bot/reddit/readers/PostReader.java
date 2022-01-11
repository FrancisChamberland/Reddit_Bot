package bot.reddit.readers;

import bot.reddit.models.Comment;
import bot.reddit.models.Post;
import bot.reddit.exceptions.ReaderException;
import bot.reddit.exceptions.UrlException;
import bot.reddit.utils.JsonGateway;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

public class PostReader {
    private static final String ERROR_COMMENTS = "Could not extract comments from : %s\n";

    public Post post;
    public List<Comment> comments;

    public PostReader(Post post) throws ReaderException {
        try{
            this.post = post;
            this.comments = getComments();
        } catch (UrlException e){
            throw new ReaderException(String.format(ERROR_COMMENTS, this.post.title));
        }
    }

    public void printComments(){
        for (Comment comment : comments){
            System.out.println(comment.toString());
        }
    }

    public JsonObject getJsonComments() throws UrlException {
        JsonArray jsonCommentPage = JsonGateway.getJsonArrayFromUrl(post.commentUrl);
        JsonObject jsonCommentSection = jsonCommentPage.get(1).getAsJsonObject();
        return jsonCommentSection.getAsJsonObject("data");
    }

    public List<Comment> getComments() throws UrlException {
        List<Comment> comments = new ArrayList<>();
        JsonObject jsonCommentSection = getJsonComments();
        JsonArray jsonComments = jsonCommentSection.getAsJsonArray("children");
        for (JsonElement jsonElementComment : jsonComments){
            JsonObject jsonComment = jsonElementComment.getAsJsonObject();
            Comment comment = new Comment(jsonComment.getAsJsonObject("data"));
            comments.add(comment);
        }
        return comments;
    }
}
