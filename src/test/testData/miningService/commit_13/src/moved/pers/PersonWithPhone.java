package pers;
public class PersonWithPhone {

  Telephone telephone;

  public PersonWithPhone(Telephone telephone2) {
    this.telephone = telephone2;
  }

  public int getTelephone() {
    return telephone.getTelephone();
  }

  public String address() {
    return telephone.address + " " + telephone.area + " " + doSmth();
  }

  static String doSmth() {
    return "hello";
  }

  public int getArea() {
    return telephone.getArea() + returnThree();
  }

  static int returnThree() {
    return 3;
  }
}