package owlvernyte.springfood.controller.Admin;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import owlvernyte.springfood.entity.*;
import owlvernyte.springfood.repository.*;
import owlvernyte.springfood.service.*;

import java.io.ByteArrayOutputStream;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Controller
public class BookingDetailController {
    @Autowired
    private OrderDetailService orderDetailService;
    @Autowired
    private OrderDetailRepository orderDetailRepository;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private OrderService orderService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private RoleService roleService;
    @Autowired
    private BookingService bookingService;
    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private BookingDetailService bookingDetailService;
    @Autowired
    private ProductService productService;
    @Autowired
    private ProductCategoryService productCategoryService;

    @Autowired
    private ReservationService reservationService;
    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ReservationRepository reservationRepository;
    @Autowired
    private BookingDetailRepository bookingDetailRepository;
    @Autowired
    private JavaMailSender mailSender;

//    @GetMapping("/admin/list_booking")
//    public String showListBooking(Model model, Authentication authentication){
//        String username = authentication.getName();
//        model.addAttribute("listStaff", userService.getAllUser());
//        model.addAttribute("listRole", roleService.getAllRole());
//        User user = userRepository.findByUsername(username);
//        model.addAttribute("user", user);
//        model.addAttribute("username", username);
//
//        model.addAttribute("listBooking", bookingService.getAllBooking());
//
//
//        return findPaginatedBooking(1,model,"name","asc");
//    }



    @PostMapping("/admin/{id}/updateBookingStatus")
    public String updateBookingStatus(@PathVariable("id") Long id, @RequestParam("status") String status, Model model, HttpSession session, HttpServletRequest request) {
        Booking booking = bookingRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid booking id: " + id));
        LocalDate currentDateTime = booking.getDateArrive();
        booking.setStatus(status);
        booking.setDateArrive(currentDateTime);
        bookingRepository.save(booking);
        String referer = request.getHeader("Referer");
        return "redirect:" + referer;
    }


    @GetMapping("/admin/bookingDetail/{id}")
    public String showBookingDetail(Authentication authentication,@PathVariable(value = "id") long id, Model model) {
        Booking booking = bookingService.getBookingById(id);
        model.addAttribute("booking", booking);
        model.addAttribute("TableName",booking.getDesk());
        model.addAttribute("RoomName",booking.getReservation().getName());
        String statusCanceled = booking.getStatus();
        if (statusCanceled.equals("3")) {
            model.addAttribute("statusCanceled", true);
        } else {
            model.addAttribute("statusCanceled", false);
        }


        if (booking != null) {
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
        Collection<ReservationItem> allReservationItems = bookingService.getAllReservationItem();

        model.addAttribute("allReservationItem", allReservationItems);
        String username = authentication.getName();
        User user = userRepository.findByUsername(username);
        model.addAttribute("user", user);
        model.addAttribute("username", username);
        List<Product> productList = productService.getAllProduct();

        model.addAttribute("listProductCategory", productCategoryService.getAllProductCategory());
        model.addAttribute("listProduct", productRepository.findAll());
        model.addAttribute("totalAmount", bookingService.getAmount());
        List<Product> listChosen = new ArrayList<>();

        Collection<ReservationItem> reservationItems = bookingService.getAllReservationItem();
        for (ReservationItem reservationItem : reservationItems) {
            if (reservationItem.getProductId() != null) {
                Product product = productRepository.findById(reservationItem.getProductId()).orElse(null);
                if (product != null && reservationItem.getQuantity() > 0) {
                    listChosen.add(product);
                }
            }
        }
        model.addAttribute("listChosen",listChosen);



        return "Admin/booking_detail";
    }

    @PostMapping("/admin/booking/updateBooking")
    public String updateBookingCartAdmin(@RequestParam("productId") Integer productId, @RequestParam("quantity") Integer quantity, @RequestParam("id") long reservationId) {
        bookingService.update(productId, quantity);
        return "redirect:/admin/bookingDetail/" + reservationId;
    }

    @PostMapping("/admin/booking/search")
    public String searchBooking(@RequestParam("idReservation") long idReservation,@RequestParam("keyword") String keyword, Model model ,Authentication authentication ) {
        String username = authentication.getName();
        model.addAttribute("listStaff", userService.getAllUser());
        model.addAttribute("listRole", roleService.getAllRole());
        User user = userRepository.findByUsername(username);
        model.addAttribute("user", user);
        model.addAttribute("username", username);
        List<Booking> listBooking = bookingService.searchBookingAdmin(keyword);
        if (listBooking.isEmpty()) {
            String errorMessage = "No matching booking found";
            model.addAttribute("errorMessage", errorMessage);
            return findPaginatedBooking(idReservation,1,model,"name","asc");
        } else {
            model.addAttribute("listBooking", listBooking);
        }

        return "Admin/list_searchBooking"  ;
    }


    @GetMapping("/admin/exportBooking-pdf")
    public void exportPdfBooking(HttpServletResponse response) throws Exception {
        // Lấy danh sách ticket
        List<Booking> bookings = bookingRepository.findAll();

        // Tạo tài liệu PDF bằng iText
        Document document = new Document();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter.getInstance(document, baos);
        document.open();

        // Tạo bảng để hiển thị thông tin ticket
        PdfPTable table = new PdfPTable(8);
        table.setWidthPercentage(100f);
        table.setSpacingBefore(10f);
        table.setSpacingAfter(10f);
        float[] columnWidths = { 1f, 2.5f, 3f, 4f, 3f, 3f, 3f, 2f };
        table.setWidths(columnWidths);
        // Tạo tiêu đề cho các cột
        PdfPCell noCell = new PdfPCell(new Paragraph("No."));
        PdfPCell idCell = new PdfPCell(new Paragraph("Booking ID"));
        PdfPCell nameCell = new PdfPCell(new Paragraph("Customer Name"));
        PdfPCell emailCell = new PdfPCell(new Paragraph("Email"));
        PdfPCell phoneCell = new PdfPCell(new Paragraph("phone"));
         PdfPCell createDateCell = new PdfPCell(new Paragraph("Create date"));
        PdfPCell dateTimeCell = new PdfPCell(new Paragraph("Date arrive"));
        PdfPCell statusCell = new PdfPCell(new Paragraph("Status"));

        // Thiết lập style cho các cột
        noCell.setBackgroundColor(BaseColor.LIGHT_GRAY);
        idCell.setBackgroundColor(BaseColor.LIGHT_GRAY);
        nameCell.setBackgroundColor(BaseColor.LIGHT_GRAY);
        emailCell.setBackgroundColor(BaseColor.LIGHT_GRAY);
        phoneCell.setBackgroundColor(BaseColor.LIGHT_GRAY);
         createDateCell.setBackgroundColor(BaseColor.LIGHT_GRAY);
        dateTimeCell.setBackgroundColor(BaseColor.LIGHT_GRAY);
        statusCell.setBackgroundColor(BaseColor.LIGHT_GRAY);

        // Thêm các cột vào bảng
        table.addCell(noCell);
        table.addCell(idCell);
        table.addCell(nameCell);
        table.addCell(emailCell);
        table.addCell(phoneCell);
         table.addCell(createDateCell);
        table.addCell(dateTimeCell);
        table.addCell(statusCell);

        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));

        // Thêm thông tin của từng ticket vào bảng
        int rowNum = 1;
        for (Booking booking : bookings) {
            table.addCell(String.valueOf(rowNum));
            table.addCell(booking.getCode());
            table.addCell(booking.getName());
            table.addCell(booking.getEmail());
            table.addCell(booking.getPhone());

            table.addCell(String.valueOf(booking.getBookingDate()));
            table.addCell(String.valueOf(booking.getDateArrive()));
            table.addCell(booking.getStatusString());
            rowNum++;
        }

        // Thêm bảng vào tài liệu PDF
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        document.add(new Paragraph("Booking list"));
        document.add(new Paragraph("Export Date: " + dateFormat.format(new Date())));
        document.add(table);
        document.close();

        // Thiết lập thông tin trả về
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=\"Booking-list.pdf\"");
        response.setContentLength(baos.size());

        // Ghi tài liệu PDF vào Response
        ServletOutputStream outputStream = response.getOutputStream();
        baos.writeTo(outputStream);
        outputStream.flush();
    }


    @GetMapping("/admin/exportBooking-excel")
    public void exportExcelBooking(HttpServletResponse response) throws Exception {

        List<Booking> bookings = bookingRepository.findAll();

        // Tạo workbook Excel mới
        Workbook workbook = new XSSFWorkbook();

        // Tạo một trang mới
        Sheet sheet = workbook.createSheet("All Booking");

        // Tạo tiêu đề cho các cột
        Row headerRow = sheet.createRow(0);
        headerRow.setHeightInPoints(20);

        CellStyle headerStyle = workbook.createCellStyle();
        headerStyle.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        headerStyle.setAlignment(HorizontalAlignment.CENTER);
        headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerFont.setColor(IndexedColors.WHITE.getIndex());
        headerStyle.setFont(headerFont);

        Cell noCell = headerRow.createCell(0);
        noCell.setCellValue("No.");
        noCell.setCellStyle(headerStyle);

        Cell idCell = headerRow.createCell(1);
        idCell.setCellValue("Booking ID");
        idCell.setCellStyle(headerStyle);

        Cell nameCell = headerRow.createCell(2);
        nameCell.setCellValue("Customer name");
        nameCell.setCellStyle(headerStyle);

        Cell emailCell = headerRow.createCell(3);
        emailCell.setCellValue("Email");
        emailCell.setCellStyle(headerStyle);



        Cell phoneCell = headerRow.createCell(4);
        phoneCell.setCellValue("Phone");
        phoneCell.setCellStyle(headerStyle);



        Cell orderDateCell = headerRow.createCell(5);
        orderDateCell.setCellValue("Order date");
        orderDateCell.setCellStyle(headerStyle);

        Cell addressCell = headerRow.createCell(6);
        addressCell.setCellValue("Date arrive");
        addressCell.setCellStyle(headerStyle);

        Cell statusCell = headerRow.createCell(7);
        statusCell.setCellValue("Status");
        statusCell.setCellStyle(headerStyle);

        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));

        // Thêm thông tin của từng ticket vào sheet
        int rowNum = 1;
        for (Booking booking : bookings) {
             Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(rowNum - 1);
            row.createCell(1).setCellValue(booking.getCode());
            row.createCell(2).setCellValue(booking.getName());
            row.createCell(3).setCellValue(booking.getEmail());

            row.createCell(4).setCellValue(booking.getPhone());
             row.createCell(5).setCellValue(booking.getBookingDate().toString());
            row.createCell(6).setCellValue(booking.getDateArrive().toString());
            row.createCell(7).setCellValue(booking.getStatusString());
        }

        // Tạo định dạng cho ngày xuất file
        CellStyle dateStyle = workbook.createCellStyle();
        dateStyle.setAlignment(HorizontalAlignment.RIGHT);
        dateStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        dateStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        dateStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        // Thêm ngày xuất file vào sheet
        Row dateRow = sheet.createRow(rowNum++);
        dateRow.setHeightInPoints(20);
        Cell dateCell = dateRow.createCell(0);
        dateCell.setCellValue("Exported on:");
        dateCell.setCellStyle(dateStyle);

        Cell exportDateCell = dateRow.createCell(1);
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        exportDateCell.setCellValue(dateFormat.format(new Date()));
        exportDateCell.setCellStyle(dateStyle);

        // Căn chỉnh cột và tự động điều chỉnh độ rộng của các cột
        for (int i = 0; i < 9; i++) {
            sheet.autoSizeColumn(i);
            sheet.setColumnWidth(i, sheet.getColumnWidth(i) + 1000);
        }

        // Thiết lập thông tin trả về
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=\"All-booking.xlsx\"");

        // Ghi workbook Excel vào Response
        workbook.write(response.getOutputStream());
        workbook.close();
    }









    @GetMapping("/admin/list_room")
    public String showListRoom(Model model, Authentication authentication){
        String username = authentication.getName();
        model.addAttribute("listStaff", userService.getAllUser());
        model.addAttribute("listRole", roleService.getAllRole());
        User user = userRepository.findByUsername(username);
        model.addAttribute("user", user);
        model.addAttribute("username", username);

        model.addAttribute("listBooking", reservationRepository.findAll());


        return findPaginatedRoom(1,model,"name","asc");
    }


    @GetMapping("/admin/pageRoom/{pageNo}")
    public String findPaginatedRoom(@PathVariable(value = "pageNo")int pageNo, Model model, @RequestParam("sortField") String sortField, @RequestParam("sortDir") String sortDir){
        int pageSize=10;
        Page<Reservation> page= reservationService.findPaginatedReservation(pageNo,pageSize,sortField,sortDir);
        List<Reservation> reservationList  = page.getContent();
        model.addAttribute("currentPage", pageNo);
        model.addAttribute("totalPages",page.getTotalPages());
        model.addAttribute("totalItems",page.getTotalElements());
        model.addAttribute("pageSize", pageSize);

        model.addAttribute("sortField",sortField);
        model.addAttribute("sortDir",sortDir);
        model.addAttribute("reverseSortDir",sortDir.equals("asc")?"desc":"asc");

        model.addAttribute("reservationList",reservationList);
        return "Admin/list_room";

    }

    @GetMapping("/admin/reservationDetail/{id}")
    public String showAllReservationDetail(  Authentication authentication,@PathVariable(value = "id") long id, Model model) {
        String username = authentication.getName();
        model.addAttribute("idReservation",id);
        model.addAttribute("listStaff", userService.getAllUser());
        model.addAttribute("listRole", roleService.getAllRole());
        User user = userRepository.findByUsername(username);
        model.addAttribute("user", user);
        model.addAttribute("username", username);

        List<Booking> bookingsByReservationId = bookingService.getBookingsByReservationId(id);
        model.addAttribute("listBooking", bookingsByReservationId);

        return findPaginatedBooking(id,1,model,"name","asc");
    }
    @GetMapping("/admin/pageBooking/{pageNo}")
    public String findPaginatedBooking(@RequestParam("idReservation") long reservationId, @PathVariable(value = "pageNo") int pageNo, Model model, @RequestParam("sortField") String sortField, @RequestParam("sortDir") String sortDir) {

        int pageSize = 10;
        Page<Booking> page = bookingService.findPaginatedIdReservation(reservationId, pageNo, pageSize, sortField, sortDir);
        List<Booking> bookingList = page.getContent();
        model.addAttribute("currentPage", pageNo);
        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("totalItems", page.getTotalElements());
        model.addAttribute("pageSize", pageSize);
        model.addAttribute("idReservation",reservationId);
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");
        Reservation reservation = reservationService.getReservationById(reservationId);
        model.addAttribute("RoomName",reservation.getName());
        model.addAttribute("listBooking", bookingList);
        return "Admin/list_booking";
    }

    @PostMapping("/admin/placeDishes")
    public String AminPlaceBooking(@RequestParam("name") String name,
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
        booking.setName(name);
        booking.setPhone(phone);
        booking.setEmail(email);
        booking.setBookingDate(LocalDate.now());
        booking.setReservation(reservation);

        booking.setUser(user);
//        booking.setTotal(bookingService.getAmount());
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
             model.addAttribute("errorMessages", errorMessages);
            return "Admin/errorPageBooking";
        }
        Random random = new Random();
        int randomNumber = random.nextInt(900000) + 100000;
        String code = "TB" + String.valueOf(randomNumber);
        booking.setCode(code);


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
//        String formattedPriceTotal = formatterTotal.format(booking.getTotal());
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

//            LocalDateTime dateTime = booking.getDateTime();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
//            String formattedDateTime = dateTime.format(formatter);
//            htmlContent += "<p>Date come: " + formattedDateTime + "</p>";
//
//            htmlContent += "<p>Total: " + formattedPriceTotal + "</p>";
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

    @GetMapping("/admin/errorPageBooking")
    private String e(){
        return"Admin/errorPageBooking";
    }

}
