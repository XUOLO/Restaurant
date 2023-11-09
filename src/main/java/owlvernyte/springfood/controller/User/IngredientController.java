package owlvernyte.springfood.controller.User;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import owlvernyte.springfood.entity.Product;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@Controller
public class IngredientController {

    @GetMapping("/orderByRecipe")
    public String orderByRecipe(){
        return "User/orderByRecipe";
    }


    @GetMapping("/user/ingredient")
    public String viewIngredientpage(Model model, Principal principal, HttpSession session) {





        String username = (String) session.getAttribute("username");
        String name = (String) session.getAttribute("name");
        Long userId = (Long) session.getAttribute("userId");
        model.addAttribute("username", username);
        model.addAttribute("name", name);
        model.addAttribute("userId", userId);

        boolean isAuthenticated = principal != null;
        model.addAttribute("isAuthenticated", isAuthenticated);




        return "User/ingredient";
    }
}
