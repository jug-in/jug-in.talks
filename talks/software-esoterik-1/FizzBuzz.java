/**
 * A program that prints the numbers from 1 to 100. But for multiples of three
 * it prints "Fizz", for multiples of five it prints "Buzz" instead of the
 * number. For numbers which are multiples of both it prints "FizzBuzz".
 */
public class FizzBuzz {
    public static void main(String[] args) {
        for (int i = 1; i <= 100; i++) {
            if (i % 15 == 0) {
                System.out.println("FizzBuzz");
            } else if (i % 3 == 0) {
                System.out.println("Fizz");
            } else if (i % 5 == 0) {
                System.out.println("Buzz");
            } else {
                System.out.println(i);
            }
        }
    }
}