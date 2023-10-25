package owlvernyte.springfood.controller.Admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import owlvernyte.springfood.entity.User;
import owlvernyte.springfood.repository.UserRepository;
import owlvernyte.springfood.service.BookingService;
import owlvernyte.springfood.service.OrderService;
import owlvernyte.springfood.service.UserService;

@Controller
public class StatisticController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private BookingService bookingService;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserService userService;
    @GetMapping("/admin/statistic")
    public String showStatistic(Model model, Authentication authentication){

        String username = authentication.getName();
        User user = userRepository.findByUsername(username);
        model.addAttribute("user", user);
        model.addAttribute("username", username);
        model.addAttribute("roles", user.getRoles());

        Long countOrder = orderService.CountOrder();
        Long countConfirmOrder = orderService.countConfirmedOrder();
        Long countCancelOrder = orderService.countCancelOrder();
        Long countHandlingOrder = orderService.countHandlingOrder();

        Long countBooking = bookingService.CountBooking();
        Long countConfirmBooking = bookingService.countConfirmedBooking();
        Long countCancelBooking = bookingService.countCancelBooking();
        Long countHandlingBooking = bookingService.countHandlingBooking();

        model.addAttribute("countCustomer", userService.countCustomers());
        model.addAttribute("countAdmin", userService.countAdmin());
        model.addAttribute("countEmployee", userService.countEmployee());
        model.addAttribute("countEmployeeAndAdmin", userService.countEmployeeAndAdmin());


        model.addAttribute("countOrder",countOrder);
        model.addAttribute("countConfirmOrder",countConfirmOrder);
        model.addAttribute("countCancelOrder",countCancelOrder);
        model.addAttribute("countHandlingOrder",countHandlingOrder);

        model.addAttribute("countBooking",countBooking);
        model.addAttribute("countConfirmBooking",countConfirmBooking);
        model.addAttribute("countCancelBooking",countCancelBooking);
        model.addAttribute("countHandlingBooking",countHandlingBooking);


        return "Admin/statistic";

    }
}
