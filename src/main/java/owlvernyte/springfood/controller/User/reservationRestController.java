package owlvernyte.springfood.controller.User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import owlvernyte.springfood.entity.Booking;
import owlvernyte.springfood.entity.BookingDTO;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Controller
public class reservationRestController {
    @GetMapping("/user/bookings")
    public String getBookings() {
        // Replace this with your actual logic to fetch bookings from a database or any other source


        return "User/clendar";
    }



}
