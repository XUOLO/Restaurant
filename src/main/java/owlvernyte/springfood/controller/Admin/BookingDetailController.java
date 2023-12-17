package owlvernyte.springfood.controller.Admin;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import jakarta.mail.internet.MimeMessage;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
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
import java.net.http.HttpRequest;
import java.security.Principal;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
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
    @PersistenceContext
    private EntityManager entityManager;
    @Autowired
    private DeskRepository deskRepository;
    @Autowired
    private ReservationCategoryService reservationCategoryService;

    @Autowired
    private ReceiptRepository receiptRepository;

    @Autowired
    private CookingRepository cookingRepository;

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
    public String showBookingDetail(Authentication authentication, @PathVariable(value = "id") long id, Model model) {
        Booking booking = bookingService.getBookingById(id);
        model.addAttribute("booking", booking);
        model.addAttribute("bookingId", booking.getId());
        model.addAttribute("id", booking.getReservation().getId());

        model.addAttribute("TableName", booking.getDesk());
        model.addAttribute("RoomName", booking.getReservation().getName());
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
        model.addAttribute("listChosen", listChosen);
        model.addAttribute("statusCanceled",statusCanceled);
        List<BookingDetail> bookingDetails = bookingDetailService.getBookingDetailsByBooking(booking);
        model.addAttribute("bookingDetails", bookingDetails);
        double total = calculateTotalForBooking(id);
        model.addAttribute("totalAmount",total);


        return "Admin/booking_detail";
    }

    @PostMapping("/admin/paymentBooking")
    private String paymentBooking(Model model, @ModelAttribute("receipt") @Valid Receipt receiptt, @RequestParam("bookingId") long bookingId, Authentication authentication) {
        double total = calculateTotalForBooking(bookingId);
        String username = authentication.getName();
        User user = userRepository.findByUsername(username);
        model.addAttribute("user", user.getName());
        model.addAttribute("username", username);

        Booking booking = bookingService.getBookingById(bookingId);
        model.addAttribute("id", booking.getReservation().getId());

        Receipt receipt = new Receipt();
        receipt.setBooking(booking);
        receipt.setName(user.getName());
        LocalDate localDate = LocalDate.now();
        receipt.setPaymentDate(localDate);
        receipt.setTotal(total);
        receiptRepository.save(receipt);

        List<ChosenProduct> listChosen = new ArrayList<>();
        List<BookingDetail> bookingDetailList = bookingDetailRepository.findAll();
        for (BookingDetail bookingDetail : bookingDetailList) {
            if (bookingDetail.getBooking().getId().equals(booking.getId())) {
                Product product = productRepository.findById(bookingDetail.getProduct().getId()).orElse(null);
                if (product != null) {
                    ChosenProduct chosenProduct = new ChosenProduct();
                    chosenProduct.setProduct(product);
                    chosenProduct.setQuantity((int) bookingDetail.getQuantity());
                    listChosen.add(chosenProduct);
                }
            }
        }

        model.addAttribute("listChosen", listChosen);
        booking.setStatus("4");
        bookingRepository.save(booking);
        model.addAttribute("TableName", booking.getDesk());

        Reservation reservation = reservationService.getReservationById(booking.getReservation().getId());
        model.addAttribute("RoomName", reservation.getName());

        model.addAttribute("localDate", localDate);

        model.addAttribute("totalBooking", total);
        return "Admin/page_receipt";
    }
    public double calculateTotalForBooking(Long bookingId) {
        String queryString = "SELECT SUM(bd.total) FROM BookingDetail bd WHERE bd.booking.id = :bookingId";
        TypedQuery<Double> query = entityManager.createQuery(queryString, Double.class);
        query.setParameter("bookingId", bookingId);
        Double total = query.getSingleResult();
        return total != null ? total : 0.0;
    }

    @PostMapping("/admin/booking/updateBooking")
    public String updateBookingCartAdmin(@RequestParam("productId") Integer productId, HttpServletRequest request, @RequestParam("quantity") Integer quantity, @RequestParam("id") long reservationId) {
        bookingService.update(productId, quantity);
        String referer = request.getHeader("Referer");
        return "redirect:" + referer;
    }

    @GetMapping("/admin/ListDishes/clear")
    public String clearListDishesBooking(Model model, HttpServletRequest request) {
        bookingService.clear();
        String referer = request.getHeader("Referer");
        return "redirect:" + referer;
    }


    @GetMapping("/admin/ListDishes/remove/{id}")
    public String removeListDishes(@PathVariable("id") int id, HttpServletRequest request) {
        bookingService.remove(id);
        String referer = request.getHeader("Referer");
        return "redirect:" + referer;
    }

    @PostMapping("/admin/booking/search")
    public String searchBooking(@RequestParam("idReservation") long idReservation, @RequestParam("keyword") String keyword, Model model, Authentication authentication) {
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
            return findPaginatedBooking(idReservation, 1, model, "name", "asc");
        } else {
            model.addAttribute("listBooking", listBooking);
        }

        return "Admin/list_searchBooking";
    }


    @GetMapping("/admin/exportBooking-pdf")
    public void exportPdfBooking(HttpServletResponse response) throws Exception {
        List<Booking> bookings = bookingRepository.findAll();

        Document document = new Document();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter.getInstance(document, baos);
        document.open();

        // Create a table to display booking information
        PdfPTable table = new PdfPTable(9);
        table.setWidthPercentage(100f);
        table.setSpacingBefore(10f);
        table.setSpacingAfter(10f);

        // Create column headers
        PdfPCell noCell = new PdfPCell(new Paragraph("Stt."));
        PdfPCell idCell = new PdfPCell(new Paragraph("Mã bàn"));
        PdfPCell nameCell = new PdfPCell(new Paragraph("Tên khách"));
        PdfPCell phoneCell = new PdfPCell(new Paragraph("SDT"));
        PdfPCell roomCell = new PdfPCell(new Paragraph("Phòng"));
        PdfPCell tableCell = new PdfPCell(new Paragraph("Bàn"));
        PdfPCell createDateCell = new PdfPCell(new Paragraph("Ngày đặt"));
        PdfPCell dateTimeCell = new PdfPCell(new Paragraph("Ngày đến"));
        PdfPCell statusCell = new PdfPCell(new Paragraph("Tình trạng"));

        // Set styles for column headers
        noCell.setBackgroundColor(BaseColor.LIGHT_GRAY);
        idCell.setBackgroundColor(BaseColor.LIGHT_GRAY);
        nameCell.setBackgroundColor(BaseColor.LIGHT_GRAY);
        phoneCell.setBackgroundColor(BaseColor.LIGHT_GRAY);
        roomCell.setBackgroundColor(BaseColor.LIGHT_GRAY);
        tableCell.setBackgroundColor(BaseColor.LIGHT_GRAY);
        createDateCell.setBackgroundColor(BaseColor.LIGHT_GRAY);
        dateTimeCell.setBackgroundColor(BaseColor.LIGHT_GRAY);
        statusCell.setBackgroundColor(BaseColor.LIGHT_GRAY);

        // Add column headers to the table
        table.addCell(noCell);
        table.addCell(idCell);
        table.addCell(nameCell);
        table.addCell(phoneCell);
        table.addCell(roomCell);
        table.addCell(tableCell);
        table.addCell(createDateCell);
        table.addCell(dateTimeCell);
        table.addCell(statusCell);

        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        // Add booking information to the table
        int rowNum = 1;
        for (Booking booking : bookings) {
            LocalDate bookingDate = booking.getBookingDate();
            String formattedBookingDate = bookingDate.format(dateFormatter);

            LocalDate dateArrive = booking.getDateArrive();
            String formattedDateArrive = dateArrive.format(dateFormatter);
            table.addCell(String.valueOf(rowNum));
            table.addCell(booking.getCode());
            table.addCell(booking.getName());
            table.addCell(booking.getPhone());
            table.addCell(booking.getReservation().getName());
            table.addCell(booking.getDesk());
            table.addCell(formattedBookingDate);
            table.addCell(formattedDateArrive);
            table.addCell(booking.getStatusString());
            rowNum++;
        }

        // Set column widths based on content length
        float[] columnWidths = new float[table.getNumberOfColumns()];
        for (int i = 0; i < table.getNumberOfColumns(); i++) {
            float maxWidth = 0f;
            for (int j = 0; j < table.getRows().size(); j++) {
                PdfPCell cell = table.getRow(j).getCells()[i];
                float cellWidth = cell.getPhrase().getContent().length() * 7f; // Adjust the multiplier based on the desired width
                maxWidth = Math.max(maxWidth, cellWidth);
            }
            columnWidths[i] = maxWidth + 5f; // Add a small padding to the column width
        }
        table.setWidths(columnWidths);

        // Add the table to the PDF document
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        document.add(new Paragraph("Danh_sách_đặt_bàn"));
        document.add(new Paragraph("Xuất_ngày: " + dateFormat.format(new Date())));
        document.add(table);
        document.close();

        // Set response headers and write the PDF document to the response
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=\"Booking_list.pdf\"");
        response.setContentLength(baos.size());

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
        Sheet sheet = workbook.createSheet("Danh sách đặt bàn");

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
        noCell.setCellValue("Stt.");
        noCell.setCellStyle(headerStyle);

        Cell idCell = headerRow.createCell(1);
        idCell.setCellValue("Mã bàn");
        idCell.setCellStyle(headerStyle);

        Cell nameCell = headerRow.createCell(2);
        nameCell.setCellValue("Tên khách ");
        nameCell.setCellStyle(headerStyle);

        Cell emailCell = headerRow.createCell(3);
        emailCell.setCellValue("Email");
        emailCell.setCellStyle(headerStyle);


        Cell phoneCell = headerRow.createCell(4);
        phoneCell.setCellValue("SDT");
        phoneCell.setCellStyle(headerStyle);

        Cell roomCell = headerRow.createCell(5);
        roomCell.setCellValue("Phòng");
        roomCell.setCellStyle(headerStyle);

        Cell tableCell = headerRow.createCell(6);
        tableCell.setCellValue("Bàn");
        tableCell.setCellStyle(headerStyle);

        Cell orderDateCell = headerRow.createCell(7);
        orderDateCell.setCellValue("Ngày đặt");
        orderDateCell.setCellStyle(headerStyle);

        Cell addressCell = headerRow.createCell(8);
        addressCell.setCellValue("Ngày đến");
        addressCell.setCellStyle(headerStyle);

        Cell statusCell = headerRow.createCell(9);
        statusCell.setCellValue("Tình trạng");
        statusCell.setCellStyle(headerStyle);

        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");




        int rowNum = 1;
        for (Booking booking : bookings) {
            LocalDate bookingDate = booking.getBookingDate();
            String formattedBookingDate = bookingDate.format(dateFormatter);

            LocalDate dateArrive = booking.getDateArrive();
            String formattedDateArrive = dateArrive.format(dateFormatter);
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(rowNum - 1);
            row.createCell(1).setCellValue(booking.getCode());
            row.createCell(2).setCellValue(booking.getName());
            row.createCell(3).setCellValue(booking.getEmail());

            row.createCell(4).setCellValue(booking.getPhone());
            row.createCell(5).setCellValue(booking.getReservation().getName());
            row.createCell(6).setCellValue(booking.getDesk());

            row.createCell(7).setCellValue(formattedBookingDate);
            row.createCell(8).setCellValue(formattedDateArrive);
            row.createCell(9).setCellValue(booking.getStatusString());
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
        dateCell.setCellValue(" Xuất ngày:");
        dateCell.setCellStyle(dateStyle);

        Cell exportDateCell = dateRow.createCell(1);
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        exportDateCell.setCellValue(dateFormat.format(new Date()));
        exportDateCell.setCellStyle(dateStyle);

        // Căn chỉnh cột và tự động điều chỉnh độ rộng của các cột
        for (int i = 0; i < 11; i++) {
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
    public String showListRoom(Model model, Authentication authentication) {
        String username = authentication.getName();
        model.addAttribute("listStaff", userService.getAllUser());
        model.addAttribute("listRole", roleService.getAllRole());
        User user = userRepository.findByUsername(username);
        model.addAttribute("user", user);
        model.addAttribute("username", username);

        model.addAttribute("listBooking", reservationRepository.findAll());


        return findPaginatedRoom(1, model, "name", "asc");
    }


    @GetMapping("/admin/pageRoom/{pageNo}")
    public String findPaginatedRoom(@PathVariable(value = "pageNo") int pageNo, Model model, @RequestParam("sortField") String sortField, @RequestParam("sortDir") String sortDir) {
        int pageSize = 10;
        Page<Reservation> page = reservationService.findPaginatedReservation(pageNo, pageSize, sortField, sortDir);
        List<Reservation> reservationList = page.getContent();
        model.addAttribute("currentPage", pageNo);
        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("totalItems", page.getTotalElements());
        model.addAttribute("pageSize", pageSize);

        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");

        model.addAttribute("reservationList", reservationList);
        return "Admin/list_room";

    }

    @GetMapping("/admin/reservationDetail/{id}")
    public String showAllReservationDetail(Authentication authentication, @PathVariable(value = "id") long id, Model model) {
        String username = authentication.getName();
        model.addAttribute("idReservation", id);
        model.addAttribute("listStaff", userService.getAllUser());
        model.addAttribute("listRole", roleService.getAllRole());
        User user = userRepository.findByUsername(username);
        model.addAttribute("user", user);
        model.addAttribute("username", username);

        List<Booking> bookingsByReservationId = bookingService.getBookingsByReservationId(id);
        model.addAttribute("listBooking", bookingsByReservationId);

        return findPaginatedBooking(id, 1, model, "name", "asc");
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
        model.addAttribute("idReservation", reservationId);
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");
        Reservation reservation = reservationService.getReservationById(reservationId);
        model.addAttribute("RoomName", reservation.getName());
        model.addAttribute("listBooking", bookingList);
        return "Admin/list_booking";
    }

    @PostMapping("/admin/placeDishes")
    public String AminPlaceBooking(@RequestParam("bookingId") long bookingId, Model model, @ModelAttribute("bookingDetail") @Valid BookingDetail bookingDetaill, HttpServletRequest request, BindingResult bindingResult, RedirectAttributes redirectAttributes, HttpSession session) {
        String username = (String) session.getAttribute("username");
        Long userId = (Long) session.getAttribute("userId");
        User user = userService.viewById(userId);


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
        Booking bo = bookingService.getBookingById(bookingId);

        for (ReservationItem reservationItem : reservationItems) {
            if (reservationItem.getProductId() != null) {
                Product product = productRepository.findById(reservationItem.getProductId()).orElse(null);
                if (product != null && reservationItem.getQuantity() > 0) {

                    BookingDetail existingBookingDetail = bookingDetailService.findBookingDetailByBookingAndProduct(bo, product);
                    Cooking cooking = new Cooking();
                    cooking.setOrderCode(bo.getCode());
                    cooking.setProduct(product);
                    cooking.setQuantity(reservationItem.getQuantity());
                    cooking.setTime(LocalDateTime.now());
                    cookingRepository.save(cooking);
                    if (existingBookingDetail != null) {
                        LocalDateTime dateTime = LocalDateTime.now();
                        existingBookingDetail.setQuantity(existingBookingDetail.getQuantity() + reservationItem.getQuantity());
                        existingBookingDetail.setTotal(existingBookingDetail.getTotal() + product.getPrice() * reservationItem.getQuantity());
                        existingBookingDetail.setDateTime(dateTime);
                         bookingDetailRepository.save(existingBookingDetail);
                    } else {

                        BookingDetail bookingDetail = new BookingDetail();
                        bookingDetail.setBooking(bo);
                        bookingDetail.setProduct(product);
                        bookingDetail.setPrice(product.getPrice());
                        bookingDetail.setQuantity(reservationItem.getQuantity());
                        bookingDetail.setDateTime(LocalDateTime.now());
                        bookingDetail.setTotal(product.getPrice() * reservationItem.getQuantity());
                        bookingDetailRepository.save(bookingDetail);


                     }
                }
            }
        }


        bookingService.clear();

        redirectAttributes.addFlashAttribute("successMessage", "Gọi món thành công");
        String referer = request.getHeader("Referer");
        return "redirect:" + referer;
    }

    @GetMapping("/admin/errorPageBooking")
    private String e() {
        return "Admin/errorPageBooking";
    }



    @GetMapping("/admin/addTable/{id}")
    public String viewTable(Model model, Principal principal, HttpSession session, @PathVariable(value = "id") long id) {


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
         return "Admin/booking";
    }

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


    @PostMapping("/admin/placeBooking")
    public String AdminPlaceBooking(@RequestParam("name") String name,
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
        String setEmail= null;
        if(email==null){
            booking.setEmail(setEmail);
        }
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



//        LocalDate date = booking.getDateArrive();
//        if (bookingRepository.existsByDateArriveAndDesk(date, selectedDesk, reservationId)) {
//            redirectAttributes.addFlashAttribute("DuplicateDate", "Rất tiếc nhà hàng không phục vụ khung giờ đã chọn. Chọn 1 khung giờ khác.");
//            String referer = request.getHeader("Referer");
//            return "redirect:" + referer;
//        }
        else
            bookingService.saveBooking(booking);
        bookingService.clear();
        redirectAttributes.addFlashAttribute("SuccessMessage", "Tạo bàn thành công");

        return "redirect:/admin/addTable/"+reservationId  ;
    }

    @PostMapping("/admin/booking-list")
    public String showBookingList( @RequestParam("reservationId") long reservationId,   @RequestParam("dateArrive") LocalDate dateArrive, Model model, Principal principal, HttpSession session ) {
        // Lấy danh sách đặt bàn từ cơ sở dữ liệu dựa trên ngày đến đã chọn (dateArrive)

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
         return "Admin/findBooking";
    }
}
