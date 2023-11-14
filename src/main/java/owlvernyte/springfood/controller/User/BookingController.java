package owlvernyte.springfood.controller.User;

import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import owlvernyte.springfood.entity.*;
import owlvernyte.springfood.repository.*;
import owlvernyte.springfood.service.*;

import java.security.Principal;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Controller
public class BookingController {

    @Autowired
    private DeskService deskService;
    @Autowired
    private BookingService bookingService;

    @Autowired
    private ReservationCategoryService reservationCategoryService;
    @Autowired
    ProductService productService;
    @Autowired
    private ReservationService reservationService;
    @Autowired
    private ProductCategoryService productCategoryService;
    @Autowired
    private UserService userService;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private JavaMailSender mailSender;
    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private BookingDetailRepository bookingDetailRepository;
    @Autowired
    private ReservationRepository reservationRepository;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private DeskRepository deskRepository;

    @GetMapping("/user/BookATable")
    public String showTable(Model model, HttpSession session){

        String username = (String) session.getAttribute("username");
        String name = (String) session.getAttribute("name");
        Long userId = (Long) session.getAttribute("userId");
        model.addAttribute("username", username);
        model.addAttribute("name", name);
        model.addAttribute("userId", userId);
        model.addAttribute("listReservation",reservationService.getAllReservation());
        model.addAttribute("listReservationCategory",reservationCategoryService.getAllReservationCategory());
        model.addAttribute("listCategory",categoryService.getAllCategory());
        return "User/reservation";

    }
//    @GetMapping("/user/showReservation/{id}")
//    public String viewTable(Model model, Principal principal, HttpSession session,@PathVariable(value = "id") long id) {
//
//        Reservation reservation = reservationService.getReservationById(id);
//        model.addAttribute("reservation", reservation);
//        Reservation reservationId =reservationService.viewById(id);
//        model.addAttribute("reservationId",reservationId);
//        String name = (String) session.getAttribute("name");
//        model.addAttribute("name", name);
//        model.addAttribute("listCategory",categoryService.getAllCategory());
//
//
//
//
//
//        List<Desk> desks = deskRepository.findByReservationId(id);
//        model.addAttribute("desks", desks);
//
//        return "User/booking";
//    }
public List<Booking> getBookingsByCurrentDateAndReservationId(LocalDate currentDate, long reservationId) {
    List<Booking> allBookings = bookingRepository.findAll(); // Lấy tất cả đặt bàn từ nguồn dữ liệu
    List<Booking> filteredBookings = new ArrayList<>();
    Reservation reservation = reservationService.viewById(reservationId);
    for (Booking booking : allBookings) {
        if (booking.getDateArrive().isEqual(currentDate) && booking.getReservation().equals(reservation)) {
            filteredBookings.add(booking);
        }
    }

    return filteredBookings;
}
    @GetMapping("/user/showReservation/{id}")
    public String viewReservation(Model model, Principal principal, HttpSession session,@PathVariable(value = "id") long id) {



        List<Desk> desks = deskRepository.findByReservationId(id);
        LocalDate currentDate = LocalDate.now();
        List<Booking> bookings = bookingService.getBookingsByCurrentDateAndReservationId(currentDate, id);
        model.addAttribute("bookings", getBookingsByCurrentDateAndReservationId(currentDate,id));
        model.addAttribute("currentDate", currentDate);
        model.addAttribute("desks", desks);


        Reservation reservation = reservationService.getReservationById(id);
        model.addAttribute("reservation", reservation);
        Reservation reservationId =reservationService.viewById(id);
        model.addAttribute("reservationId",reservationId.getId());


        model.addAttribute("listReservationCategory", reservationCategoryService.getAllReservationCategory());
        model.addAttribute("totalAmount", bookingService.getAmount());
        String name = (String) session.getAttribute("name");
        model.addAttribute("name", name);
        boolean isAuthenticated = principal != null;
        model.addAttribute("isAuthenticated", isAuthenticated);
        Booking booking = new Booking();
        model.addAttribute("booking", booking);
        model.addAttribute("listProductCategory",productCategoryService.getAllProductCategory());
        model.addAttribute("listProduct",productService.getAllProduct());


        String username = (String) session.getAttribute("username");
        Long userId = (Long) session.getAttribute("userId");
        model.addAttribute("username", username);
        model.addAttribute("name", name);
        model.addAttribute("userId", userId);

        model.addAttribute("username", username);
        model.addAttribute("name", name);
        model.addAttribute("userId", userId);
        model.addAttribute("listReservation",reservationService.getAllReservation());
        model.addAttribute("listReservationCategory",reservationCategoryService.getAllReservationCategory());
        model.addAttribute("listCategory",categoryService.getAllCategory());

        return "User/booking";
    }

    @PostMapping("/user/booking/updateBooking")
    public String updateBookingCart(@RequestParam("productId") Integer productId, @RequestParam("quantity") Integer quantity, @RequestParam("id") long reservationId) {
        bookingService.update(productId, quantity);
        return "redirect:/user/showReservation/" + reservationId;
    }

    @GetMapping("/user/booking/clear")
    public String clearBooking(Model model){
        bookingService.clear();

        return "redirect:/user/showReservation/{id}";
    }


    @PostMapping("/user/placeBooking")
    public String placeBooking(@RequestParam("name") String name,
                              @RequestParam("phone") String phone,
                             @RequestParam("email") String email,
                               @RequestParam("selectDesk") String selectedDesk,
                               @RequestParam("reservationId") long reservationId,
                               @RequestParam("numberOfPeople") int numberOfPeople,
                               Model model,
                               HttpServletRequest request,
                               @ModelAttribute("booking") @Valid Booking booking,
                               BindingResult bindingResult,
                               RedirectAttributes redirectAttributes,
                              HttpSession session) {


        Reservation reservation = reservationService.viewById(reservationId);
        String username = (String) session.getAttribute("username");
        Long userId = (Long) session.getAttribute("userId");
        User user = userService.viewById(userId);


        booking.setUser(user);
        booking.setNumberOfPeople(numberOfPeople);
        booking.setName(name);
        booking.setPhone(phone);
        booking.setEmail(email);
        booking.setBookingDate(LocalDate.now());
        booking.setReservation(reservation);

        booking.setDesk(selectedDesk);

        booking.setStatus("1");

        Collection<ReservationItem> reservationItems = bookingService.getAllReservationItem();


        Random random = new Random();
        int randomNumber = random.nextInt(900000) + 100000;
        String code = "TB" + String.valueOf(randomNumber);
        booking.setCode(code);

        if (bindingResult.hasErrors()) {

            redirectAttributes.addFlashAttribute("failMessage", "Ngày không hợp lệ");
            String referer = request.getHeader("Referer");
            return "redirect:" + referer;
        }

        if (selectedDesk == null || selectedDesk.isEmpty()) {
            redirectAttributes.addFlashAttribute("NotChosenYet", "Bạn chưa chọn bàn");
            String referer = request.getHeader("Referer");
            return "redirect:" + referer;
        }
        LocalDate date = booking.getDateArrive() ;
        if (bookingRepository.existsByDateArriveAndDesk(date, selectedDesk)) {
            redirectAttributes.addFlashAttribute("DuplicateDate", "Rất tiếc nhà hàng không phục vụ khung giờ đã chọn. Chọn 1 khung giờ khác.");
            String referer = request.getHeader("Referer");
            return "redirect:" + referer;
        }else
        bookingService.saveBooking(booking);




//        NumberFormat formatterTotal = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
//
//         MimeMessage message = mailSender.createMimeMessage();
//
//        try {
//            MimeMessageHelper helper = new MimeMessageHelper(message, true);
//
//            // Đặt các thuộc tính của email
//            helper.setTo(email);
//            helper.setSubject("Order info #" + booking.getCode());
//
//            // Đặt nội dung email dưới dạng HTML
//            String tableContent = "<table style=\"border-collapse: collapse;\">";
//            tableContent += "<tr style=\"background-color: #f8f8f8;\"><th style=\"padding: 10px; border: 1px solid #ddd;\">Dishes Name</th><th style=\"padding: 10px; border: 1px solid #ddd;\">Quantity</th><th style=\"padding: 10px; border: 1px solid #ddd;\">Price</th></tr>";
//
//            for (ReservationItem reservationItem : reservationItems) {
//                if (reservationItem.getProductId() != null) {
//                    Product product = productRepository.findById(reservationItem.getProductId()).orElse(null);
//                    if (product != null && reservationItem.getQuantity() > 0) {
//                        tableContent += "<tr>";
//                        tableContent += "<td style=\"padding: 10px; border: 1px solid #ddd;\">" + product.getName() + "</td>";
//                        tableContent += "<td style=\"padding: 10px; border: 1px solid #ddd;\">" + reservationItem.getQuantity() + "</td>";
//                        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
//                        String formattedPrice = formatter.format(product.getPrice());
//                        tableContent += "<td style=\"padding: 10px; border: 1px solid #ddd;\">" + formattedPrice + "</td>";
//                        tableContent += "</tr>";
//                    }
//                }
//            }
//
//            tableContent += "</table>";
//
//            String htmlContent = "<html><body>";
//            htmlContent += "<h2>Order info #" + booking.getCode() + "</h2>";
//            htmlContent += "<p>Hello " + booking.getName() + ",</p>";
//            htmlContent += "<p>Thank you for your order. Here are the details about your order:</p>";
//            htmlContent += "<p>Order code " + booking.getCode() + "</p>";
//
//            LocalDate bookingDate = booking.getBookingDate();
//            DateTimeFormatter formatterBookingDate = DateTimeFormatter.ofPattern("dd-MM-yyyy ");
//            String formattedBookingDate = bookingDate.format(formatterBookingDate);
//            htmlContent += "<p>Booking date " + formattedBookingDate + "</p>";
//
//            LocalDateTime dateTime = booking.getDateTime();
//            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
//            String formattedDateTime = dateTime.format(formatter);
//            htmlContent += "<p>Date come: " + formattedDateTime + "</p>";
//
//            htmlContent += tableContent;
//            htmlContent += "</body></html>";
//
//            helper.setText(htmlContent, true);
//
//            mailSender.send(message);
//
//        } catch (Exception e) {
//            e.printStackTrace();
//
//            model.addAttribute("errorMessage", "email fail.");
//            return "User/ErrorPage";
//        }
        bookingService.clear();

        return "redirect:/user/checkOutSuccess";
    }
    @PostMapping("/user/booking-list")
    public String showBookingList( @RequestParam("reservationId") long reservationId,   @RequestParam("dateArrive") LocalDate dateArrive, Model model, Principal principal, HttpSession session ) {
        // Lấy danh sách đặt bàn từ cơ sở dữ liệu dựa trên ngày đến đã chọn (dateArrive)

        List<Booking> bookingList = getBookingList(dateArrive );
        LocalDate currentDate = LocalDate.now();
        model.addAttribute("bookings", getBookingsByCurrentDateAndReservationId(dateArrive,reservationId));
        // Gửi danh sách đặt bàn đến view để hiển thị

        model.addAttribute("dateArrive", dateArrive);
        Reservation reservation = reservationService.getReservationById(reservationId);
        model.addAttribute("reservation", reservation);
        Reservation reservationIds =reservationService.viewById(reservationId);
        model.addAttribute("reservationId",reservationIds.getId());


        model.addAttribute("listReservationCategory", reservationCategoryService.getAllReservationCategory());
        model.addAttribute("totalAmount", bookingService.getAmount());
        String name = (String) session.getAttribute("name");
        model.addAttribute("name", name);
        boolean isAuthenticated = principal != null;
        model.addAttribute("isAuthenticated", isAuthenticated);
        Booking booking = new Booking();
        model.addAttribute("booking", booking);
        model.addAttribute("listProductCategory",productCategoryService.getAllProductCategory());
        model.addAttribute("listProduct",productService.getAllProduct());


        String username = (String) session.getAttribute("username");
        Long userId = (Long) session.getAttribute("userId");
        model.addAttribute("username", username);
        model.addAttribute("name", name);
        model.addAttribute("userId", userId);

        model.addAttribute("username", username);
        model.addAttribute("name", name);
        model.addAttribute("userId", userId);
        model.addAttribute("listReservation",reservationService.getAllReservation());
        model.addAttribute("listReservationCategory",reservationCategoryService.getAllReservationCategory());
        model.addAttribute("listCategory",categoryService.getAllCategory());
        return "User/findBooking";
    }
    private List<Booking> getBookingList(LocalDate dateArrive ) {


        return bookingRepository.findByDateArrive(dateArrive );
    }
}
