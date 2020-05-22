package pers;
public class Telephone {

  int telephone;
  static int area;
  String address;

  public Telephone(int telephone, int area, String address) {
    this.telephone = telephone;
    this.area = area;
    this.address = address;
  }

  public int getTelephone() {
    return telephone;
  }

  static String doSmth() {
    return "hello";
  }

  static int returnThree2() {
    return 3;
  }

  public int getArea() {
    return area;
  }
}