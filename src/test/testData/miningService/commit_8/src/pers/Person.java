package pers;
public class Person {

  Telephone telephone;

  public Person(Telephone telephone) {
    this.telephone = telephone;
  }

  public int getTelephone() {
    return telephone.getTelephone();
  }

  public int getArea() {
    return telephone.getArea();
  }
}