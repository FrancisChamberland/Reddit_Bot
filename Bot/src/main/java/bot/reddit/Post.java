package bot.reddit;

import java.util.List;

public class Post {
    public String subreddit;
    public String title;
    public int score;
    public int commentCount;
    public double upvoteRatio;
    public boolean stickied;
    public String commentUrl;
    public List<Comment> comments;


    public Post(String subreddit, String title, int score, int commentCount, double upvoteRatio, boolean stickied, String commentUrl){
        this.subreddit = subreddit;
        this.title = title;
        this.score = score;
        this.commentCount = commentCount;
        this.upvoteRatio = upvoteRatio;
        this.stickied = stickied;
        this.commentUrl = commentUrl + ".json";
    }

    @Override
    public String toString() {
        return String.format("%s\nScore : %s\nNumber of comments : %s\n", title, score, commentCount);
    }
}
