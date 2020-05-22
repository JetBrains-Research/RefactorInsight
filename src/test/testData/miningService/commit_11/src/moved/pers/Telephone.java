package pers;
public class Telephone {

  int telephone;
  int area;
  String address;

  public Telephone(int telephone, int area, String address) {
    this.telephone = telephone;
    this.area = area;
    this.address = address;
  }

  public int getTelephone() {
    return telephone;
  }

  public int getArea() {
    return area;
  }
}