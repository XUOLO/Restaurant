package owlvernyte.springfood.entity;

public class ChosenProduct {
    private Product product;
    private int quantity;

    public ChosenProduct(Product product, int quantity) {
        this.product = product;
        this.quantity = quantity;
    }

    public ChosenProduct() {

    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
