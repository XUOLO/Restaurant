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
import owlvernyte.springfood.repository.BookingDetailRepository;
import owlvernyte.springfood.repository.BookingRepository;
import owlvernyte.springfood.repository.ProductRepository;
import owlvernyte.springfood.repository.ReservationRepository;
import owlvernyte.springfood.service.*;

import java.security.Principal;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Controller
public class BookingController {

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

    @PostMapping("/user/BookATable")
    public String showTable(Model model, Authentication authentication, HttpSession session){
        if (authentication == null || !authentication.isAuthenticated()) {

            return "Admin/login";
        }
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
    @GetMapping("/user/showReservation/{id}")
    public String viewReservation(Model model, Principal principal, HttpSession session,@PathVariable(value = "id") long id) {

        Reservation reservation = reservationService.getReservationById(id);
        model.addAttribute("reservation", reservation);
        Reservation reservationId =reservationService.viewById(id);
        model.addAttribute("reservationId",reservationId);

        Collection<ReservationItem> allReservationItems = bookingService.getAllReservationItem();

        model.addAttribute("allReservationItem", allReservationItems);
        model.addAttribute("listReservationCategory", reservationCategoryService.getAllReservationCategory());
        model.addAttribute("totalAmount", bookingService.getAmount());
        String name = (String) session.getAttribute("name");
        model.addAttribute("name", name);
        boolean hasItems = !allReservationItems.isEmpty();
        model.addAttribute("hasItems", hasItems);
        boolean isAuthenticated = principal != null;
        model.addAttribute("isAuthenticated", isAuthenticated);
        Booking booking = new Booking(); // Khởi tạo đối tượng booking
        model.addAttribute("booking", booking); // Truyền booking vào model attribute

         model.addAttribute("listProductCategory",productCategoryService.getAllProductCategory());
        model.addAttribute("listProduct",productService.getAllProduct());

        if (reservation != null) {
            List<Product> allProducts = productService.getAllProduct();

            for (Product product : allProducts) {
                ReservationItem reservationItem = new ReservationItem();
                reservationItem.setProductId(product.getId());
                reservationItem.setName(product.getName());
                reservationItem.setImage(product.getImage());
                reservationItem.setPrice(product.getPrice());
                reservationItem.setQuantity(product.getQuantity());
                 reservationItem.setProductCategory(product.getProductCategory().getName());
                bookingService.add(reservationItem);
            }
        }
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
                               @RequestParam("reservationId") long reservationId,
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
//        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new BookingNotFoundException(bookingId));
//        booking.setId(reservationId);
        booking.setName(name);
        booking.setPhone(phone);
        booking.setEmail(email);
        booking.setBookingDate(LocalDate.now());
        booking.setReservation(reservation);

        booking.setUser(user);
        booking.setTotal(bookingService.getAmount());
        booking.setStatus("1");

        Collection<ReservationItem> reservationItems = bookingService.getAllReservationItem();
        List<String> errorMessages = new ArrayList<>();

        for (ReservationItem reservationItem : reservationItems) {
            if (reservationItem.getProductId() != null) {
                Long productId = reservationItem.getProductId();
                int quantityToOrder = reservationItem.getQuantity();

                Product product = productRepository.findById(productId).orElse(null);
                if (product != null) {
                    int availableQuantity = product.getQuantity();
                    if (quantityToOrder > availableQuantity) {
                        String errorMessage = "Product '" + product.getName() + "' not enough quantity.";
                        errorMessages.add(errorMessage);

                    }
                }
            }
        }

        if (!errorMessages.isEmpty()) {
            // Có ít nhất một sản phẩm không đủ số lượng, thực hiện xử lý thông báo lỗi, ví dụ: đẩy danh sách thông báo lỗi vào model và trả về trang lỗi
            model.addAttribute("errorMessages", errorMessages);
            return "User/ErrorPageBooking";
        }
        Random random = new Random();
        int randomNumber = random.nextInt(900000) + 100000;
        String code = "TB" + String.valueOf(randomNumber);
        booking.setCode(code);

        if (bindingResult.hasErrors()) {

            redirectAttributes.addFlashAttribute("failMessage", "Invalid date");
            String referer = request.getHeader("Referer");
            return "redirect:" + referer;
        }
        if (bookingRepository.existsByDateTime(booking.getDateTime())) {
            redirectAttributes.addFlashAttribute("DuplicateDate", "The selected time is already booked. Please choose another time.");
            String referer = request.getHeader("Referer");
            return "redirect:" + referer;
        }else
        bookingService.saveBooking(booking);

        // Trừ số lượng sản phẩm trong cơ sở dữ liệu
        for (ReservationItem reservationItem : reservationItems) {
            if (reservationItem.getProductId() != null) {
                Long productId = reservationItem.getProductId();
                int quantityToOrder = reservationItem.getQuantity();

                Product product = productRepository.findById(productId).orElse(null);
                if (product != null) {
                    int availableQuantity = product.getQuantity();
                    int updatedQuantity = availableQuantity - quantityToOrder;
                    product.setQuantity(updatedQuantity);
                    productRepository.save(product);
                }
            }
        }

        for (ReservationItem reservationItem : reservationItems) {
            if (reservationItem.getProductId() != null) {
                Product product = productRepository.findById(reservationItem.getProductId()).orElse(null);
                if (product != null && reservationItem.getQuantity() > 0) {
                    BookingDetail bookingDetail = new BookingDetail();
                    bookingDetail.setBooking(booking);
                    bookingDetail.setProduct(product);
                    bookingDetail.setPrice(product.getPrice());
                    bookingDetail.setQuantity(reservationItem.getQuantity());
                    bookingDetail.setDateTime(LocalDateTime.now());
                    bookingDetailRepository.save(bookingDetail);
                }
            }
        }

        NumberFormat formatterTotal = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        String formattedPriceTotal = formatterTotal.format(booking.getTotal());
         MimeMessage message = mailSender.createMimeMessage();

        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            // Đặt các thuộc tính của email
            helper.setTo(email);
            helper.setSubject("Order info #" + booking.getCode());

            // Đặt nội dung email dưới dạng HTML
            String tableContent = "<table style=\"border-collapse: collapse;\">";
            tableContent += "<tr style=\"background-color: #f8f8f8;\"><th style=\"padding: 10px; border: 1px solid #ddd;\">Dishes Name</th><th style=\"padding: 10px; border: 1px solid #ddd;\">Quantity</th><th style=\"padding: 10px; border: 1px solid #ddd;\">Price</th></tr>";

            for (ReservationItem reservationItem : reservationItems) {
                if (reservationItem.getProductId() != null) {
                    Product product = productRepository.findById(reservationItem.getProductId()).orElse(null);
                    if (product != null && reservationItem.getQuantity() > 0) {
                        tableContent += "<tr>";
                        tableContent += "<td style=\"padding: 10px; border: 1px solid #ddd;\">" + product.getName() + "</td>";
                        tableContent += "<td style=\"padding: 10px; border: 1px solid #ddd;\">" + reservationItem.getQuantity() + "</td>";
                        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
                        String formattedPrice = formatter.format(product.getPrice());
                        tableContent += "<td style=\"padding: 10px; border: 1px solid #ddd;\">" + formattedPrice + "</td>";
                        tableContent += "</tr>";
                    }
                }
            }

            tableContent += "</table>";

            String htmlContent = "<html><body>";
            htmlContent += "<h2>Order info #" + booking.getCode() + "</h2>";
            htmlContent += "<p>Hello " + booking.getName() + ",</p>";
            htmlContent += "<p>Thank you for your order. Here are the details about your order:</p>";
            htmlContent += "<p>Order code " + booking.getCode() + "</p>";

            LocalDate bookingDate = booking.getBookingDate();
            DateTimeFormatter formatterBookingDate = DateTimeFormatter.ofPattern("dd-MM-yyyy ");
            String formattedBookingDate = bookingDate.format(formatterBookingDate);
            htmlContent += "<p>Booking date " + formattedBookingDate + "</p>";

            LocalDateTime dateTime = booking.getDateTime();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
            String formattedDateTime = dateTime.format(formatter);
            htmlContent += "<p>Date come: " + formattedDateTime + "</p>";

             htmlContent += "<p>Total: " + formattedPriceTotal + "</p>";
            htmlContent += tableContent;
            htmlContent += "</body></html>";

            helper.setText(htmlContent, true);

            mailSender.send(message);

        } catch (Exception e) {
            e.printStackTrace();

            model.addAttribute("errorMessage", "email fail.");
            return "User/ErrorPage";
        }
        bookingService.clear();

        return "redirect:/user/checkOutSuccess";
    }

}
