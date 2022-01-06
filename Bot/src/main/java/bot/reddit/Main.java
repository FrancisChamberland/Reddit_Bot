package bot.reddit;

import bot.reddit.exceptions.ReaderException;
import bot.reddit.exceptions.UrlException;
import bot.reddit.readers.PostReader;
import bot.reddit.readers.SubredditReader;

public class Main {
    public static void main( String[] args ) {
        String subreddit = args[0];
        int numberOfPosts = Integer.parseInt(args[1]);

        try{
            SubredditReader subredditReader = new SubredditReader(subreddit, numberOfPosts);
            PostReader postReader = new PostReader(subredditReader.posts.get(0));
            postReader.printComments();
        } catch (ReaderException e){
            System.out.println(e.getMessage());
        }
    }
}
