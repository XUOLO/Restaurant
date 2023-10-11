package owlvernyte.springfood.controller.Admin;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import owlvernyte.springfood.entity.Product;
import owlvernyte.springfood.entity.Reservation;
import owlvernyte.springfood.entity.ReservationCategory;
import owlvernyte.springfood.entity.User;
import owlvernyte.springfood.repository.ProductRepository;
import owlvernyte.springfood.repository.ReservationCategoryRepository;
import owlvernyte.springfood.repository.ReservationRepository;
import owlvernyte.springfood.repository.UserRepository;
import owlvernyte.springfood.service.*;

import javax.sql.rowset.serial.SerialException;
import java.io.IOException;
import java.sql.Blob;
import java.sql.SQLException;
import java.time.LocalDateTime;

@Controller
public class ReservationController {

    @Autowired
    private ProductService productService;
    @Autowired
    private ProductCategoryService productCategoryService;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleService roleService;
    @Autowired
    private ReservationCategoryService reservationCategoryService;
    @Autowired
    private ReservationCategoryRepository reservationCategoryRepository;
    @Autowired
    private ReservationService reservationService;
    @Autowired
    private ReservationRepository reservationRepository;


    @GetMapping("/displayReservation")
    public ResponseEntity<byte[]> displayImage(@RequestParam("id") long id) throws IOException, SQLException
    {
        Reservation reservation = reservationService.viewById(id);
        byte [] imageBytes = null;
        imageBytes = reservation.getImage().getBytes(1,(int) reservation.getImage().length());
        return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(imageBytes);
    }
    @GetMapping("/admin/list_reservation")
    public String showListReservation(Authentication authentication, Model model) {



        String username = authentication.getName();
        User user = userRepository.findByUsername(username);
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ADMIN"));
        boolean isEmployee = authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("EMPLOYEE"));
        model.addAttribute("user", user);

        model.addAttribute("username", username);
        model.addAttribute("isAdmin", isAdmin);
        model.addAttribute("isEmployee", isEmployee);
        model.addAttribute("listReservation", reservationService.getAllReservation());
        model.addAttribute("listReservationCategory", reservationCategoryService.getAllReservationCategory());

        return "Admin/list_reservation";
    }


    @GetMapping("/admin/new_reservation")
    public String showReservation(Authentication authentication,Model model) {
        Reservation reservation = new Reservation();
        model.addAttribute("reservation", reservation);
        model.addAttribute("listReservationCategory",  reservationCategoryService.getAllReservationCategory());
        String username = authentication.getName();
        User user = userRepository.findByUsername(username);
        model.addAttribute("user", user);
        model.addAttribute("username", username);

        return "Admin/new_reservation";
    }
    @PostMapping("/admin/addReservation")
    public String addReservation(@Valid @ModelAttribute("reservation") Reservation reservation, BindingResult bindingResult, @RequestParam("image") MultipartFile file, Model model) throws IOException, SerialException, SQLException {
        if (file.isEmpty()) {
            reservation.setImage(null);
        } else {
            byte[] bytes = file.getBytes();
            Blob blob = new javax.sql.rowset.serial.SerialBlob(bytes);
            reservation.setImage(blob);
        }
        reservation.setCreateTime(LocalDateTime.now());
        reservation.setStatus("1");
        reservationService.saveReservation(reservation);
        return "redirect:/admin/list_reservation";
    }


    @PostMapping("/admin/{id}/updateStatusReservation")
    public String updateProductStatusReservation(@PathVariable("id") Long id, @RequestParam("status") String status, Model model, HttpSession session, HttpServletRequest request) {
        Reservation reservation = reservationRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid reservation id: " + id));
        reservation.setStatus(status);
        reservationService.saveReservation(reservation);
        String referer = request.getHeader("Referer");


        return "redirect:" + referer;
    }


    @GetMapping("/admin/deleteReservation/{id}")
    public String deleteProduct(@PathVariable(value = "id") long id) {
        this.reservationService.deleteReservationById(id);
        return "redirect:/admin/list_reservation";
    }

}
