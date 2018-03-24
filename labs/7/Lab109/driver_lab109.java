package Lab109;

import java.util.Arrays;

/**
 * Tests the Course class
 */
public class driver_lab109 {

  /**
   * Main method for lab 10.9
   */
  public static void main(String[] args) {

    Course course = new Course("How to Read 401");

    course.addStudent("Jimmy Englishmajor");
    course.addStudent("Sarah Imanartist");
    course.addStudent("Ima Dropdisclass");

    course.dropStudent("Ima Dropdisclass");

    System.out.println(Arrays.toString(course.getStudents()));
  }

}
