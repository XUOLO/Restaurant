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
import java.util.Timer;
import java.util.TimerTask;
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
                               @RequestParam("timeArrive") LocalTime timeArrive,

                               Model model,
                               HttpServletRequest request,
                               @ModelAttribute("booking") @Valid Booking booking,
                               BindingResult bindingResult,
                               RedirectAttributes redirectAttributes,
                              HttpSession session) {


        Reservation reservation = reservationService.viewById(reservationId);
        String username = (String) session.getAttribute("username");
        Long userId = (Long) session.getAttribute("userId");


        User user = null;

        if (userId != null) {
            user = userService.viewById(userId);
        }
        booking.setTimeArrive(timeArrive);
        booking.setUser(user);
        booking.setNumberOfPeople(numberOfPeople);
        booking.setName(name);
        booking.setPhone(phone);
        booking.setEmail(email);
        booking.setBookingDate(LocalDate.now());
        booking.setReservation(reservation);
        booking.setBookingDateTime(LocalDateTime.now());

        booking.setDesk(selectedDesk);

        booking.setStatus("2");

        Collection<ReservationItem> reservationItems = bookingService.getAllReservationItem();


        Random random = new Random();
        int randomNumber = random.nextInt(900000) + 100000;
        String code = "TB" + String.valueOf(randomNumber);
        booking.setCode(code);
        int randomNumberCode = random.nextInt(90000) + 10000;
        String confirmCode = String.valueOf(randomNumberCode);
        booking.setConfirmCode(confirmCode);
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
        else{
            Timer timer = new Timer();
            timer.schedule(new StatusChangeTask(booking, bookingService), 180000);
            bookingService.saveBooking(booking);
        }





        NumberFormat formatterTotal = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));

         MimeMessage message = mailSender.createMimeMessage();

        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            // Đặt các thuộc tính của email
            helper.setTo(email);
            helper.setSubject("Thông tin đặt bàn #" + booking.getCode());

            // Đặt nội dung email dưới dạng HTML



            String htmlContent = "<html><body>";
            htmlContent += "<h2>Thông tin đặt bàn #" + booking.getCode() + "</h2>";
            htmlContent += "<p>Xin chào " + booking.getName() + ",</p>";
            htmlContent += "<p>Cảm ơn đã đặt bàn tại nhà hàng chúng tôi:</p>";
            htmlContent += "<p>Bàn số :" +selectedDesk+ "(" +booking.getReservation().getName()+  ")</p>";
            htmlContent += "<p>Số lượng người đến :  " + booking.getNumberOfPeople() + "</p>";
            htmlContent += "<p>Đây là mã xác nhận đặt bàn của bạn : " + booking.getConfirmCode() + "</p>";

            LocalDate bookingDate = booking.getBookingDate();
            DateTimeFormatter formatterBookingDate = DateTimeFormatter.ofPattern("dd-MM-yyyy ");
            String formattedBookingDate = bookingDate.format(formatterBookingDate);
            htmlContent += "<p>Ngày đặt : " + formattedBookingDate + "</p>";




            htmlContent += "</body></html>";

            helper.setText(htmlContent, true);

            mailSender.send(message);

        } catch (Exception e) {
            e.printStackTrace();

            model.addAttribute("errorMessage", "email fail.");
            return "User/ErrorPage";
        }
        bookingService.clear();
        redirectAttributes.addFlashAttribute("SuccessMessage", "Chúng tôi đã gửi mã otp đến mail của bạn \n Sau 3' không xác nhận thì bàn đã đặt sẽ tự động hủy !!");
        model.addAttribute("SuccessMessage","Chúng tôi đã gửi mã xác nhận đến mail của bạn .\n Sau 3' không xác nhận thì bàn đã đặt sẽ tự động hủy !!");
        model.addAttribute("reservation", reservation);
        model.addAttribute("bookingId",booking.getId());
        model.addAttribute("reservationId",reservationId);

        return "User/confirmBooking";
    }

    @PostMapping("/user/confirmBooking")
    public String confirmBooking( RedirectAttributes redirectAttributes,Model model,@RequestParam("bookingId") long bookingId,@RequestParam("confirmCode") String confirmCode ){



        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new IllegalArgumentException("Invalid booking id: " + bookingId));

        LocalDateTime sentTime = booking.getBookingDateTime();
        LocalDateTime currentTime = LocalDateTime.now();
        int maxConfirmationTimeMinutes = 180; //180 giay

        if (sentTime.plusSeconds(maxConfirmationTimeMinutes).isBefore(currentTime)) {
            // Quá thời gian chờ, từ chối xác nhận
            Reservation reservation = reservationService.viewById(booking.getReservation().getId());

            model.addAttribute("reservation", reservation);
            model.addAttribute("bookingId",bookingId);
            model.addAttribute("errorMessage","Quá thời gian xác nhận!");
            return "User/confirmBooking";
        } else  if(booking.getConfirmCode().equals(confirmCode)){
            LocalDate currentDateTime = booking.getDateArrive();
            booking.setStatus("1");
            booking.setDateArrive(currentDateTime);
             bookingRepository.save(booking);
        }else {
            Reservation reservation = reservationService.viewById(booking.getReservation().getId());

            model.addAttribute("reservation", reservation);
            model.addAttribute("bookingId",bookingId);
            model.addAttribute("errorMessage","Mã xác nhận không chính xác!");
            return "User/confirmBooking";
        }

        redirectAttributes.addFlashAttribute("SuccessConfirm", "Xác nhận thành công!");
        return "redirect:/user/showReservation/"+booking.getReservation().getId();
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


    @GetMapping("/user/placeBookingSuccess")
    public String placeBookingSuccess(Model model,HttpSession session,Principal principal) {
        String name = (String) session.getAttribute("name");

        model.addAttribute("name", name);
//        boolean isAuthenticated = principal != null;
//        model.addAttribute("isAuthenticated", isAuthenticated);
        model.addAttribute("listCategory", categoryService.getAllCategory());


        return "User/placeBookingSuccess";
    }
}
