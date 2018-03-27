//package Lab109;

import java.lang.reflect.Array;
import java.util.Arrays;

/**
 * Stores and manipulates data on courses
 */
public class Course {

  /* Name of the course */
  private String courseName;
  /* Students in this course */
  private String[] students = new String[100];
  /* Number of students in this course */
  private int numberOfStudents;

  /**
   * Creates a new Course object
   *
   * @param courseName Name of the course
   */
  public Course(String courseName) {
    this.courseName = courseName;
  }

  /**
   * Adds a new student to the course
   *
   * @param student Name of the student to be add
   */
  public void addStudent(String student) {
    if (numberOfStudents < students.length) {
      students[numberOfStudents] = student;
      numberOfStudents++;
    } else {
      String[] newStudents = new String[++numberOfStudents];
      System.arraycopy(students, 0, newStudents, 0, students.length);
      newStudents[newStudents.length - 1] = student;
      students = newStudents.clone();
    }
  }

  /**
   * @return Array of the names of the students in this course
   */
  public String[] getStudents() {
    return Arrays.copyOf(students, numberOfStudents);
  }

  /**
   * @return Number of students in this course
   */
  public int getNumberOfStudents() {
    return numberOfStudents;
  }

  /**
   * @return Name of the course
   */
  public String getCourseName() {
    return courseName;
  }

  /**
   * Removes a student's name from this course's array of students
   *
   * @param student The name of the student to be dropped from the array of students
   */
  public void dropStudent(String student) {
    for (int i = 0; i < students.length; i++) {
      if (students[i] != null && students[i].equals(student)) {
        String[] newStudents = new String[students.length - 1];
        System.arraycopy(students, 0, newStudents, 0, i);
        System.arraycopy(students, i + 1, newStudents, i, students.length - i - 1);
        students = newStudents.clone();
        --numberOfStudents;
      }
    }
  }

  /**
   * Removes all students from the course
   */
  public void clear() {
    numberOfStudents = 100;
    students = new String[100];
  }
}