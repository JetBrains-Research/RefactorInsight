package misc;
public class PriceWithDiscount {

    int price;

    public PriceWithDiscount(int price, int discount) {
        this.price = price;
        Bycicle.discount = discount;
    }

    public int getPrice() {
        return this.price;
    }

    public void applyDiscount() {
        price -= discount;
    }
}
