package owlvernyte.springfood.controller.User;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import owlvernyte.springfood.entity.Booking;
import owlvernyte.springfood.entity.Order;
import owlvernyte.springfood.entity.User;
import owlvernyte.springfood.service.BookingService;

import java.util.List;

@Controller
public class SearchBookingController
{

    @Autowired
    private BookingService bookingService;

    @GetMapping("/user/searchBooking")
    public String searchBookingUser(){

        return "User/searchBooking";
    }


    @PostMapping("/user/searchBookingByPhone")
    public String searchBookingUsers(HttpServletRequest request, RedirectAttributes redirectAttributes, @RequestParam("keyword") String keyword, Model model  ) {

        List<Booking> listSearchBooking = bookingService.searchBookingUser(keyword);
        if (listSearchBooking.isEmpty()) {
             redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy đơn!!");
            String referer = request.getHeader("Referer");
            return "redirect:" + referer;
         } else {
            model.addAttribute("listSearchBooking", listSearchBooking);
        }

        return "User/list_searchBooking"  ;
    }
}
