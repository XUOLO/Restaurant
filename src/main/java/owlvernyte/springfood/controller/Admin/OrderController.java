package owlvernyte.springfood.controller.Admin;


import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import owlvernyte.springfood.entity.Order;
import owlvernyte.springfood.entity.OrderDetail;
import owlvernyte.springfood.entity.Product;
import owlvernyte.springfood.entity.User;
import owlvernyte.springfood.repository.OrderDetailRepository;
import owlvernyte.springfood.repository.OrderRepository;
import owlvernyte.springfood.repository.UserRepository;
import owlvernyte.springfood.service.OrderDetailService;
import owlvernyte.springfood.service.OrderService;
import owlvernyte.springfood.service.RoleService;
import owlvernyte.springfood.service.UserService;

import java.io.ByteArrayOutputStream;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

@Controller
public class OrderController {

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
    @GetMapping("/admin/list_order")
    public String showListOrder(Model model, Authentication authentication){
        String username = authentication.getName();
        model.addAttribute("listStaff", userService.getAllUser());
        model.addAttribute("listRole", roleService.getAllRole());
        User user = userRepository.findByUsername(username);
        model.addAttribute("user", user);
        model.addAttribute("username", username);

        model.addAttribute("listOrder", orderService.getAllOrder());


        return findPaginatedOrder(1,model,"name","asc");
    }
    @GetMapping("/admin/pageOrder/{pageNo}")
    public String findPaginatedOrder(@PathVariable(value = "pageNo")int pageNo,Model model,@RequestParam("sortField") String sortField,@RequestParam("sortDir") String sortDir){
        int pageSize=10;
        Page<Order> page= orderService.findPaginatedOrder(pageNo,pageSize,sortField,sortDir);
        List<Order> orderList = page.getContent();
        model.addAttribute("currentPage", pageNo);
        model.addAttribute("totalPages",page.getTotalPages());
        model.addAttribute("totalItems",page.getTotalElements());
        model.addAttribute("pageSize", pageSize);

        model.addAttribute("sortField",sortField);
        model.addAttribute("sortDir",sortDir);
        model.addAttribute("reverseSortDir",sortDir.equals("asc")?"desc":"asc");

        model.addAttribute("listOrder",orderList);
        return "Admin/list_order";

    }



    @PostMapping("/admin/{id}/updateOrderStatus")
    public String updateOrderStatus(@PathVariable("id") Long id, @RequestParam("status") String status, Model model, HttpSession session, HttpServletRequest request) {
        Order order = orderRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid order id: " + id));
        order.setStatus(status);
        orderRepository.save(order);
        String referer = request.getHeader("Referer");
        return "redirect:" + referer;
    }


    @GetMapping("/admin/orderDetail/{id}")
    public String showOrderDetail(Authentication authentication,@PathVariable(value = "id") long id, Model model) {
        Order order = orderService.getOrderById(id);
        model.addAttribute("order", order);

        // Lấy danh sách OrderDetail theo Order
        List<OrderDetail> orderDetails = orderDetailService.getOrderDetailsByOrder(order);
        model.addAttribute("orderDetails", orderDetails);
        model.addAttribute("totalAmount",order.getTotal());

        String username = authentication.getName();
        User user = userRepository.findByUsername(username);
        model.addAttribute("user", user);
        model.addAttribute("username", username);

        return "Admin/order_detail";
    }

    @PostMapping("/admin/order/search")
    public String searchOrder(@RequestParam("keyword") String keyword, Model model ,Authentication authentication ) {
        String username = authentication.getName();
        model.addAttribute("listStaff", userService.getAllUser());
        model.addAttribute("listRole", roleService.getAllRole());
        User user = userRepository.findByUsername(username);
        model.addAttribute("user", user);
        model.addAttribute("username", username);
        List<Order> listOrder = orderService.searchOrderAdmin(keyword);
        if (listOrder.isEmpty()) {
            String errorMessage = "No matching order found";
            model.addAttribute("errorMessage", errorMessage);
            return findPaginatedOrder(1,model,"name","asc");
        } else {
            model.addAttribute("listOrder", listOrder);
        }

        return "Admin/list_searchOrder"  ;
    }
    @GetMapping("/admin/exportOrder-pdf")
    public void exportPdfOrder(HttpServletResponse response) throws Exception {
        // Lấy danh sách ticket
        List<Order> orders = orderRepository.findAll();

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
        PdfPCell idCell = new PdfPCell(new Paragraph("Order ID"));
        PdfPCell nameCell = new PdfPCell(new Paragraph("Customer Name"));
        PdfPCell emailCell = new PdfPCell(new Paragraph("Email"));
        PdfPCell phoneCell = new PdfPCell(new Paragraph("phone"));
        PdfPCell totalCell = new PdfPCell(new Paragraph("Total"));
        PdfPCell createDateCell = new PdfPCell(new Paragraph("Create date"));
        PdfPCell statusCell = new PdfPCell(new Paragraph("Status"));

        // Thiết lập style cho các cột
        noCell.setBackgroundColor(BaseColor.LIGHT_GRAY);
        idCell.setBackgroundColor(BaseColor.LIGHT_GRAY);
        nameCell.setBackgroundColor(BaseColor.LIGHT_GRAY);
        emailCell.setBackgroundColor(BaseColor.LIGHT_GRAY);
        phoneCell.setBackgroundColor(BaseColor.LIGHT_GRAY);
        totalCell.setBackgroundColor(BaseColor.LIGHT_GRAY);
        createDateCell.setBackgroundColor(BaseColor.LIGHT_GRAY);
        statusCell.setBackgroundColor(BaseColor.LIGHT_GRAY);

        // Thêm các cột vào bảng
        table.addCell(noCell);
        table.addCell(idCell);
        table.addCell(nameCell);
        table.addCell(emailCell);
        table.addCell(phoneCell);
        table.addCell(totalCell);
        table.addCell(createDateCell);
        table.addCell(statusCell);

        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));

        // Thêm thông tin của từng ticket vào bảng
        int rowNum = 1;
        for (Order order : orders) {
            table.addCell(String.valueOf(rowNum));
            table.addCell(order.getCode());
            table.addCell(order.getName());
            table.addCell(order.getEmail());
            table.addCell(order.getPhone());
            String total = currencyFormat.format(order.getTotal());
            table.addCell(total);
            table.addCell(String.valueOf(order.getOrderDate()));
            table.addCell(order.getStatusString());
            rowNum++;
        }

        // Thêm bảng vào tài liệu PDF
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        document.add(new Paragraph("Order list"));
        document.add(new Paragraph("Export Date: " + dateFormat.format(new Date())));
        document.add(table);
        document.close();

        // Thiết lập thông tin trả về
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=\"Order-list.pdf\"");
        response.setContentLength(baos.size());

        // Ghi tài liệu PDF vào Response
        ServletOutputStream outputStream = response.getOutputStream();
        baos.writeTo(outputStream);
        outputStream.flush();
    }


    @GetMapping("/admin/exportOrder-excel")
    public void exportExcelOrder(HttpServletResponse response) throws Exception {

        List<Order> orders = orderRepository.findAll();

        // Tạo workbook Excel mới
        Workbook workbook = new XSSFWorkbook();

        // Tạo một trang mới
        Sheet sheet = workbook.createSheet("All Dishes");

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
        idCell.setCellValue("Order ID");
        idCell.setCellStyle(headerStyle);

        Cell nameCell = headerRow.createCell(2);
        nameCell.setCellValue("Customer name");
        nameCell.setCellStyle(headerStyle);

        Cell emailCell = headerRow.createCell(3);
        emailCell.setCellValue("Email");
        emailCell.setCellStyle(headerStyle);

        Cell addressCell = headerRow.createCell(4);
        addressCell.setCellValue("Address");
        addressCell.setCellStyle(headerStyle);
        Cell phoneCell = headerRow.createCell(5);
        phoneCell.setCellValue("Phone");
        phoneCell.setCellStyle(headerStyle);

        Cell totalCell = headerRow.createCell(6);
        totalCell.setCellValue("Total");
        totalCell.setCellStyle(headerStyle);

        Cell orderDateCell = headerRow.createCell(7);
        orderDateCell.setCellValue("Order date");
        orderDateCell.setCellStyle(headerStyle);

        Cell statusCell = headerRow.createCell(8);
        statusCell.setCellValue("Status");
        statusCell.setCellStyle(headerStyle);

        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));

        // Thêm thông tin của từng ticket vào sheet
        int rowNum = 1;
        for (Order order : orders) {
            String total = currencyFormat.format(order.getTotal());
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(rowNum - 1);
            row.createCell(1).setCellValue(order.getCode());
            row.createCell(2).setCellValue(order.getName());
            row.createCell(3).setCellValue(order.getEmail());
            row.createCell(4).setCellValue(order.getAddress());
            row.createCell(5).setCellValue(order.getPhone());
            row.createCell(6).setCellValue(total);
            row.createCell(7).setCellValue(order.getOrderDate().toString());
            row.createCell(8).setCellValue(order.getStatusString());
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
        response.setHeader("Content-Disposition", "attachment; filename=\"All-order.xlsx\"");

        // Ghi workbook Excel vào Response
        workbook.write(response.getOutputStream());
        workbook.close();
    }

}
