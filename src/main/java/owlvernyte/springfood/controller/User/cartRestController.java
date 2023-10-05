package owlvernyte.springfood.controller.User;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import owlvernyte.springfood.entity.CartItem;
import owlvernyte.springfood.entity.Product;
import owlvernyte.springfood.service.CategoryService;
import owlvernyte.springfood.service.ProductService;
import owlvernyte.springfood.service.ShoppingCartService;

@RestController
public class cartRestController {

    @Autowired
    private ShoppingCartService shoppingCartService;

    @Autowired
    private CategoryService categoryService;
    @Autowired
    ProductService productService;

    @GetMapping("/user/shoppingCart/add/{id}")
    public String shoppingCartAdd(@PathVariable("id") Integer id) {
        Product product = productService.getProductById(id);
        if (product != null) {
            CartItem cartItem = new CartItem();
            cartItem.setProductId(product.getId());
            cartItem.setName(product.getName());
            cartItem.setPrice(product.getPrice());
            cartItem.setQuantity(product.getQuantity());
            cartItem.setImage(product.getImage());
            cartItem.setProductCategory(product.getProductCategory().getName());
            shoppingCartService.add(cartItem);

            return "success"; // Trả về một phản hồi JSON hoặc thông báo thành công
        }

        return "error"; // Trả về một phản hồi JSON hoặc thông báo lỗi
    }
}
