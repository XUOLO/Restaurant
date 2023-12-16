package owlvernyte.springfood.entity;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;

import java.sql.Blob;

public class ReservationItem {


    private long productId;
    private String name;
    private String productCategory;
    private int price;
    private int quantity;
    private Blob image;
    private String recipe;
    private String videoRecipe;


    public String getRecipe() {
        return recipe;
    }

    public void setRecipe(String recipe) {
        this.recipe = recipe;
    }

    public String getVideoRecipe() {
        return videoRecipe;
    }

    public void setVideoRecipe(String videoRecipe) {
        this.videoRecipe = videoRecipe;
    }

    public Blob getImage() {
        return image;
    }

    public String getProductCategory() {
        return productCategory;
    }

    public void setProductCategory(String productCategory) {
        this.productCategory = productCategory;
    }

    public void setImage(Blob image) {
        this.image = image;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(long productId) {
        this.productId = productId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

}
