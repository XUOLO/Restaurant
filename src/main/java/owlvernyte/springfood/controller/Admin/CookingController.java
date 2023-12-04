package owlvernyte.springfood.controller.Admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import owlvernyte.springfood.entity.User;
import owlvernyte.springfood.repository.CookingRepository;
import owlvernyte.springfood.repository.ProductRepository;
import owlvernyte.springfood.repository.UserRepository;
import owlvernyte.springfood.service.CookingService;

@Controller
public class CookingController {
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CookingRepository cookingRepository;
    @Autowired
    private CookingService cookingService;



    @GetMapping("/admin/list_cooking")
    public String showListC(Model model, Authentication authentication){

        String username = authentication.getName();

        User user = userRepository.findByUsername(username);
        model.addAttribute("user", user);
        model.addAttribute("username", username);

        return "Admin/cheff_order";
    }

}
