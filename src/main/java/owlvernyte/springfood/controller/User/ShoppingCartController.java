package owlvernyte.springfood.controller.User;


import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import owlvernyte.springfood.entity.CartItem;
import owlvernyte.springfood.service.CategoryService;
import owlvernyte.springfood.service.ProductService;
import owlvernyte.springfood.service.ShoppingCartService;

import java.security.Principal;
import java.util.Collection;


@Controller
public class ShoppingCartController {

    @Autowired
    private ShoppingCartService shoppingCartService;

        @Autowired
        private CategoryService categoryService;
    @Autowired
    ProductService productService;

    @GetMapping("/user/viewCart")
    public String viewCart(Model model, Principal principal, HttpSession session) {
        Collection<CartItem> allCartItems = shoppingCartService.getAllCartItem();

        model.addAttribute("AllCartItem", allCartItems);
        model.addAttribute("listCategory", categoryService.getAllCategory());
        model.addAttribute("totalAmount", shoppingCartService.getAmount());
        String name = (String) session.getAttribute("name");
        model.addAttribute("name", name);
        boolean hasItems = !allCartItems.isEmpty();
        model.addAttribute("hasItems", hasItems);
        boolean isAuthenticated = principal != null;
        model.addAttribute("isAuthenticated", isAuthenticated);
        return "User/ShoppingCart";
    }




    @GetMapping("/user/shoppingCart/clear")
    public String clearCart(Model model){
         shoppingCartService.clear();
        return "redirect:/user/viewCart";
    }


    @GetMapping("/user/shoppingCart/remove/{id}")
    public String removeItemCart(@PathVariable("id") int id){
        shoppingCartService.remove(id);
        return "redirect:/user/viewCart";
    }
    @PostMapping("/user/shoppingCart/updateCart")
    public String updateItemCart(@RequestParam("productId") Integer productId, @RequestParam("quantity") Integer quantity){
        shoppingCartService.update(productId, quantity);
        return "redirect:/user/viewCart";
    }

}
