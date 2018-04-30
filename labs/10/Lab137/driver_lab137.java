package Lab137;

import java.util.ArrayList;

/**
 * Program to practice using interfaces
 *
 * @author William Kluge Contact: klugewilliam@gmail.com
 */
public class driver_lab137 {

  /**
   * Main method for lab 13.7
   *
   * @param args This program does not utilize arguments
   */
  public static void main(String[] args) {

    ArrayList<GeometricObject> shapes = new ArrayList<>();
    shapes.add(new Square(89));
    shapes.add(new Square(40));
    shapes.add(new Square(534));
    shapes.add(new Square(4321));
    shapes.add(new Square(45));

    for (GeometricObject shape : shapes) {
      if (shape instanceof Colorable) {
        System.out.println("Area = " + Math.pow(((Square) shape).getSide(), 2));
        ((Colorable) shape).howToColor();
      }
    }

  }
}
