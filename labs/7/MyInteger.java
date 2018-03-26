//package Lab103;

/**
 * Wraps a primitive int with some methods
 */
public class MyInteger {

  /* The value represented by this object */
  private int value;

  /**
   * Constructor for MyInteger
   *
   * @param value Value to initialize this object with
   */
  public MyInteger(int value) {

    this.value = value;

  }

  /**
   * Gets the value of the int represented by this object
   *
   * @return The value of the int represented by this object
   */
  public int getValue() {
    return value;
  }

  /**
   * Tests if the value of this object is even
   *
   * @return If the value of this object is even
   */
  public boolean isEven() {
    return value % 2 == 0;
  }

  /**
   * Tests if the value of this object is odd
   *
   * @return If the value of this object is odd
   */
  public boolean isOdd() {
    return !isEven();
  }

  /**
   * Tests if the value of this object is prime
   *
   * @return If the value of this object is prime
   */
  public boolean isPrime() {
    return MyInteger.isPrime(this);
  }

  /**
   * Tests if the value of a MyInteger object is even
   *
   * @param val MyInteger object to check
   * @return If the value of the object is even
   */
  public static boolean isEven(MyInteger val) {
    return val.value % 2 == 0;
  }

  /**
   * Tests if the value of a MyInteger object is odd
   *
   * @param val MyInteger object to check
   * @return If the value of the object is odd
   */
  public static boolean isOdd(MyInteger val) {
    return !MyInteger.isEven(val);
  }

  /**
   * Tests if the value of a MyInteger object is prime
   *
   * @param val Value to check for prime
   * @return If the value is prime
   */
  public static boolean isPrime(MyInteger val) {
    boolean prime = true;

    for (int i = 2; i < val.value / 2; ++i) {
      if (val.value % i == 0) {
        prime = false;
        break;
      }
    }

    return prime;
  }

  /**
   * Tests if the value of an integer is equal to the value of this object
   *
   * @param num int to check
   * @return If the two values are equal
   */
  public boolean equals(int num) {
    return num == value;
  }

  /**
   * Tests if the value of this object is equal to the value of another MyInteger object
   *
   * @param num MyInteger object to check
   * @return If the two values are equal
   */
  public boolean equals(MyInteger num) {
    return num.value == value;
  }

  /**
   * Converts an array of characters into an int
   *
   * @param chars Character array to convert
   * @return Integer value of chars
   */
  public static int parseInt(char[] chars) {
    return Integer.valueOf(String.valueOf(chars));
  }

  /**
   * Converts a string into an integer
   *
   * @param str String to convert
   * @return Integer value of str
   */
  public static int parseInt(String str) {
    return Integer.valueOf(str);
  }

}
