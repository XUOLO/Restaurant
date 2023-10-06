package owlvernyte.springfood.controller.User;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class IngredientController {

    @GetMapping("/orderByRecipe")
    public String orderByRecipe(){
        return "User/orderByRecipe";
    }
}
