package bot.reddit;

import bot.reddit.exceptions.ReaderException;
import bot.reddit.models.Post;
import bot.reddit.readers.PostReader;
import bot.reddit.readers.SubredditReader;
import bot.reddit.utils.ConsoleScanner;

public class Main {
    static String subreddit;
    static int numberOfPosts;
    static SubredditReader subredditReader;
    static PostReader postReader;

    public static void main( String[] args ) {
        System.out.println("Welcome to console Reddit!");
        exploreReddit();
    }

    public static void exploreReddit(){
        chooseSubreddit();
        connectToSubredditIfValid();
    }

    public static void chooseSubreddit(){
        System.out.println("Please enter the subreddit you wish to explore");
        subreddit = ConsoleScanner.getStringInput();
        System.out.println("How many posts would you like to explore? (between 1 and 100)");
        numberOfPosts = ConsoleScanner.getLimitedIntInput(1, 100);
    }

    public static void connectToSubredditIfValid(){
        while (!connectionSucceeded(subreddit, numberOfPosts)){
            chooseSubreddit();
        }
        System.out.printf("\nYou are connected to %s\n\n", subreddit);
        scanUserCommand();
    }

    public static boolean connectionSucceeded(String subreddit, int numberOfPosts){
        System.out.printf("Connecting to %s... ", subreddit);
        try{
            subredditReader = new SubredditReader(subreddit, numberOfPosts);
            System.out.println("Connection succeeded!");
            return true;
        } catch (ReaderException e){
            System.out.println("Connection failed : " + e.getMessage());
            return false;
        }
    }

    public static void scanUserCommand(){
        System.out.println("1 >> Show the posts" +
                "\n2 >> Show non stickied posts" +
                "\n3 >> Save all post's images" +
                "\n4 >> Read a post's comments" +
                "\n5 >> Connect to another subreddit" +
                "\n6 >> Close console");

        String command = ConsoleScanner.getStringInput();
        executeUserCommand(command);
    }

    public static void executeUserCommand(String input){
        switch (input){
            case "1":
                showPosts();
                break;
            case "2":
                showNonStickiedPosts();
                break;
            case "3":
                savePostsImages();
                break;
            case "4":
                readPost();
                break;
            case "5":
                exploreReddit();
                break;
            case "6":
                System.exit(0);
                break;
            default:
                System.out.printf("Command '%s' is invalid\n", input);
        }
        scanUserCommand();
    }

    public static void showPosts(){
        subredditReader.printAllPosts();
    }

    public static void showNonStickiedPosts(){
        subredditReader.printNotStickiedPosts();
    }

    public static void savePostsImages(){
        subredditReader.savePostsImages();
    }

    public static void readPost() {
        System.out.println("Enter the number of the post you want to explore");
        try {
            printPostComments();
        } catch (ReaderException e){
            System.out.println("Connection failed : " + e.getMessage());
        }
    }

    public static void printPostComments() throws ReaderException {
        int indexOfPost = ConsoleScanner.getLimitedIntInput(0, subredditReader.numberOfPost);
        Post post = subredditReader.posts.get(indexOfPost);
        postReader = new PostReader(post);
        postReader.printComments();
    }
}
