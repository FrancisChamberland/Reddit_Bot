package bot.reddit.utils;

import java.util.Scanner;

public class ConsoleScanner {

    private static Scanner scanner;

    public static String getStringInput(){
        scanner = new Scanner(System.in);
        return scanner.next();
    }

    public static int getLimitedIntInput(int min, int max){
        int intInput = getIntInput();
        while (intInput < min || intInput > max){
            System.out.printf("The number must be between %s and %s, please enter a valid number\n",
                    min, max);
            intInput = getIntInput();
        }
        return intInput;
    }

    public static int getIntInput(){
        scanner = new Scanner(System.in);
        while (scanner.hasNext() && !scanner.hasNextInt()) {
            System.out.println("This is not a number, please enter a valid number");
            scanner.next();
        }
        return scanner.nextInt();
    }
}
