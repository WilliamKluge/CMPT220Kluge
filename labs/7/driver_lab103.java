//package Lab103;

/**
 * Program to test the MyInteger class
 */
public class driver_lab103 {

  /**
   * Main method of lab 10.3
   */
  public static void main(String[] args) {

    MyInteger myInteger = new MyInteger(5);

    System.out.println("Get value: " + myInteger.getValue());

    System.out.println("Is even: " + myInteger.isEven());

    System.out.println("Is odd: " + myInteger.isOdd());

    System.out.println("Is prime: " + myInteger.isPrime());

    System.out.println("Static is even: " + MyInteger.isEven(myInteger));

    System.out.println("Static is odd: " + MyInteger.isOdd(myInteger));

    System.out.println("Static is prime: " + MyInteger.isPrime(myInteger));

    System.out.println("Is equal primitive (comparing to 5): " + myInteger.equals(5));

    System.out
        .println("Is equal instantiated (comparing to 5): " + myInteger.equals(new MyInteger(5)));

    System.out
        .println("Static parse char[]: " + MyInteger.parseInt(new char[]{'1', '2', '3', '4'}));

    System.out.println("Static parse String: " + MyInteger.parseInt("1234"));

  }

}
