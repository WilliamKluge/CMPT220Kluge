package Lab911;

/**
 * Handles a 2 x 2 system of linear equations
 */
public class LinearEquation {

  /**
   * Variables for equations
   */
  private double a, b, c, d, e, f;

  /**
   * Constructor for the LinearEquation class
   *
   * @param a Value of a
   * @param b Value of v
   * @param c Value of c
   * @param d Value of d
   * @param e Value of e
   * @param f Value of f
   */
  public LinearEquation(double a, double b, double c, double d, double e, double f) {
    this.a = a;
    this.b = b;
    this.c = c;
    this.d = d;
    this.e = e;
    this.f = f;
  }

  /**
   * Get the value of a
   *
   * @return Value of a
   */
  public double getA() {
    return a;
  }

  /**
   * Set the value of a
   *
   * @param a Value to set a to
   */
  public void setA(double a) {
    this.a = a;
  }

  /**
   * Get the value of a
   *
   * @return Value of a
   */
  public double getB() {
    return b;
  }

  /**
   * Set the value of b
   *
   * @param b Value to set b to
   */
  public void setB(double b) {
    this.b = b;
  }

  /**
   * Get the value of c
   *
   * @return Value of c
   */
  public double getC() {
    return c;
  }

  /**
   * Set the value of c
   *
   * @param c Value to set c to
   */
  public void setC(double c) {
    this.c = c;
  }

  /**
   * Get the value of d
   *
   * @return Value of d
   */
  public double getD() {
    return d;
  }

  /**
   * Set the value of d
   *
   * @param d Value to set d to
   */
  public void setD(double d) {
    this.d = d;
  }

  /**
   * Get the value of e
   *
   * @return Value of e
   */
  public double getE() {
    return e;
  }

  /**
   * Set the value of e
   *
   * @param e Value to set e to
   */
  public void setE(double e) {
    this.e = e;
  }

  /**
   * Get the value of f
   *
   * @return Value of f
   */
  public double getF() {
    return f;
  }

  /**
   * Set the value of f
   *
   * @param f Value to set f to
   */
  public void setF(double f) {
    this.f = f;
  }

  /**
   * Determines if the equation can be solved
   *
   * @return If the equation can be solved
   */
  public boolean isSolvable() {

    return a * d - b * c != 0;

  }

  /**
   * Solves the equation for X
   *
   * @return Value of X
   */
  public double getX() {

    return (e * d - b * f) / (a * d - b * c);

  }

  /**
   * Solves the equation for Y
   *
   * @return Value of Y
   */
  public double getY() {

    return (a * f - e * c) / (a * d - b * c);

  }

}
