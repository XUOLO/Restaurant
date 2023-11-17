package owlvernyte.springfood.controller.Admin;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import owlvernyte.springfood.entity.OrderDetail;
import owlvernyte.springfood.entity.User;
import owlvernyte.springfood.repository.UserRepository;
import owlvernyte.springfood.service.OrderDetailService;

import java.util.List;

@Controller
public class HomeController {
@Autowired
private OrderDetailService orderDetailService;

//    @Autowired
//    private UserService userService;
    @Autowired
    private UserRepository userRepository;
//    @GetMapping("/login")
//    public String showLogin( Model model ) {
//
//         return "Admin/login";
//    }
//
//
//

    @GetMapping("/admin")
    public String index(Authentication authentication, Model model) {

        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ADMIN"));
        boolean isEmployee = authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("EMPLOYEE"));
        String username = authentication.getName();
        User user = userRepository.findByUsername(username);
        model.addAttribute("user", user);
        model.addAttribute("username", username);
        model.addAttribute("isAdmin", isAdmin);
        model.addAttribute("isEmployee", isEmployee);
//        List<OrderDetail> topProducts = orderDetailService.getTop4ProductsByProductIdCount();
//        model.addAttribute("topProducts", topProducts);
        return "Admin/index";
    }

    @GetMapping("/welcome")
    public String greeting() {
        return "welcome";
    }



}
