//package Lab115;

// JA: No test class?
import java.util.ArrayList;

/**
 * Stores and manipulates data on courses
 */
public class Course {

  /* Name of the course */
  private String courseName;
  /* Students in this course */
  private ArrayList<String> students;
  /* Number of students in this course */
  private int numberOfStudents;

  /**
   * Creates a new Lab115.Lab109.Course object
   *
   * @param courseName Name of the course
   */
  public Course(String courseName) {
    this.courseName = courseName;
    students = new ArrayList<>();
  }

  /**
   * Adds a new student to the course
   *
   * @param student Name of the student to be add
   */
  public void addStudent(String student) {
    students.add(student);
  }

  /**
   * @return Array of the names of the students in this course
   */
  public String[] getStudents() {
    String[] a = new String[students.size()];
    students.toArray(a);
    return a;
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
    students.remove(student);
  }

  /**
   * Removes all students from the course
   */
  public void clear() {
    students = new ArrayList<>();
  }
}