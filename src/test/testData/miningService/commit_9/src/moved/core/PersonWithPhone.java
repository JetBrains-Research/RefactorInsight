package core;
public class PersonWithPhone {

  Telephone telephone;
  String address;

  public PersonWithPhone(Telephone telephone, String address) {
    this.telephone = telephone;
    this.address = address;
  }

  public int getTelephone() {
    return telephone.getTelephone();
  }

  public String address() {
    return address + " " + telephone.area;
  }

  public int getArea() {
    return telephone.getArea();
  }
}