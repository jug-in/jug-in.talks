import java.util.Arrays;
import java.util.Scanner;

/**
 * A program that reads two words from System.in and prints "true" if they
 * are anagrams, i.e. contain the exact same letters (case insensitive).
 */
public class Anagram {
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        System.out.print("#1: ");
        String first = in.nextLine().toLowerCase();
        System.out.print("#2: ");
        String second = in.nextLine().toLowerCase();

        char[] firstChars = first.toCharArray();
        char[] secondChars = second.toCharArray();
        Arrays.sort(firstChars);
        Arrays.sort(secondChars);

        System.out.println(Arrays.equals(firstChars, secondChars));
    }
}