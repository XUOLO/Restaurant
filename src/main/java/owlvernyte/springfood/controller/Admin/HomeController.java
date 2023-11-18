package owlvernyte.springfood.controller.Admin;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import owlvernyte.springfood.entity.Order;
import owlvernyte.springfood.entity.User;
import owlvernyte.springfood.repository.UserRepository;
import owlvernyte.springfood.service.OrderDetailService;
import owlvernyte.springfood.service.OrderService;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Controller
public class HomeController {
    @Autowired
    private OrderDetailService orderDetailService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private UserRepository userRepository;

@GetMapping("/admin/date")
public String calculateRevenueByDate(@RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date, Model model) {
    // Logic tính toán doanh thu dựa trên ngày và lưu kết quả vào model
    double revenue = calculateRevenue(date);
    model.addAttribute("date", date);
    model.addAttribute("revenueDate", revenue);
    long countOrder= orderService.countOrdersToday();
    model.addAttribute("countOrder",countOrder);
    return "Admin/index";
    }
    private double calculateRevenue(LocalDate date) {
        // Giả sử bạn có một danh sách các đơn hàng
        List<Order> orders = getOrderListByDate(date);

        // Tính tổng doanh thu từ các đơn hàng
        double revenue = 0.0;
        for (Order order : orders) {
            revenue += order.getTotal();
        }

        return revenue;
    }

    private List<Order> getOrderListByDate(LocalDate date) {

        List<Order> orders = orderService.getOrdersByDate(date);
        return orders;
    }
    @GetMapping("/admin/month")
    public String calculateRevenueByMonth(@RequestParam("month") String monthString, Model model) {
        YearMonth yearMonth = YearMonth.parse(monthString);
        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atEndOfMonth();

        double revenue = calculateRevenue(startDate, endDate);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        model.addAttribute("revenueMonth", revenue);
        long countOrder = orderService.countOrdersToday();
        model.addAttribute("countOrder", countOrder);
        return "Admin/index";
    }
    private double calculateRevenue(LocalDate startDate, LocalDate endDate) {
        double revenue = 0.0;

        List<Order> orders = orderService.getOrdersInRange(startDate, endDate);
        for (Order order : orders) {
            revenue += order.getTotal();
        }

        return revenue;
    }
    @GetMapping("/admin/year")
    public String calculateRevenueByYear(@RequestParam("year") int year, Model model) {
        double totalRevenue = 0.0;

        for (int month = 1; month <= 12; month++) {
            YearMonth yearMonth = YearMonth.of(year, month);
            LocalDate startDate = yearMonth.atDay(1);
            LocalDate endDate = yearMonth.atEndOfMonth();

            double revenue = calculateRevenue(startDate, endDate);
            totalRevenue += revenue;

            // Thêm doanh thu của từng tháng vào model
            model.addAttribute("revenueMonth" + month, revenue);
        }

         model.addAttribute("totalRevenue", totalRevenue);

        long countOrder = orderService.countOrdersToday();
        model.addAttribute("countOrder", countOrder);

        return "Admin/index";
    }
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

        long countOrder= orderService.countOrdersToday();
        model.addAttribute("countOrder",countOrder);
        return "Admin/index";
    }

    @GetMapping("/welcome")
    public String greeting() {
        return "welcome";
    }



}
