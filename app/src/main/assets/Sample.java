package example;


import java.util.ArrayList;
import java.util.List;

public class TestClass {};

public class Example {
  TestClass class1 = new TestClass();
  private ArrayList<String> names;
  private ArrayList<ArrayList<Int>> numberGrid;

  public Example() {
    names = new ArrayList<>();
  }

  public void addName(String name) {
    names.add(name);
  }

  public List<String> getNames() {
    return new ArrayList<>(names);
  }

  public static void main(String args[]) {
    Boolean testBool = true;
    String testString = "This is a string!";
    Integer testInt = 137;
    ArrayList<String> mainList = new ArrayList<String>();
    ArrayList<ArrayList<Int>> mainGrid = new ArrayList<ArrayList<Int>>();
    TestClass class1 = new TestClass();

    mainList.add("This is a string!");
    println(testInt);
  }
}

