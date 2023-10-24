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
import jakarta.validation.Valid;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import owlvernyte.springfood.entity.Order;
import owlvernyte.springfood.entity.Product;
import owlvernyte.springfood.entity.User;
import owlvernyte.springfood.repository.ProductRepository;
import owlvernyte.springfood.repository.UserRepository;
import owlvernyte.springfood.service.ProductCategoryService;
import owlvernyte.springfood.service.ProductService;
import owlvernyte.springfood.service.RoleService;
import owlvernyte.springfood.service.UserService;

import javax.sql.rowset.serial.SerialException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.Blob;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Controller
public class ProductController {
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

    @GetMapping("/display")
    public ResponseEntity<byte[]> displayImage(@RequestParam("id") long id) throws IOException, SQLException
    {
        Product product = productService.viewById(id);
        byte [] imageBytes = null;
        imageBytes = product.getImage().getBytes(1,(int) product.getImage().length());
        return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(imageBytes);
    }
    @GetMapping("/admin/list_product")
    public String showListProduct(Authentication authentication,Model model) {



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
        model.addAttribute("listProduct", productService.getAllProduct());
        model.addAttribute("listProductCategory", productCategoryService.getAllProductCategory());

        return findPaginatedRequest(1,model);
    }

    @GetMapping("/admin/pageProduct/{pageNo}")
    public String findPaginatedRequest(@PathVariable(value = "pageNo")int pageNo,Model model){
        int pageSize=10;
        Page<Product> page= productService.findPaginatedProduct(pageNo,pageSize);
        List<Product> productList = page.getContent();
        model.addAttribute("currentPage", pageNo);
        model.addAttribute("totalPages",page.getTotalPages());
        model.addAttribute("totalItems",page.getTotalElements());
        model.addAttribute("listProduct",productList);
//        User sessionUser = (User) session.getAttribute("user");
//
//        model.addAttribute("user", sessionUser);
        return "Admin/list_product";

    }



    @GetMapping("/admin/new_product")
    public String showNewProduct(Authentication authentication,Model model) {
        Product product = new Product();
        model.addAttribute("product", product);
        model.addAttribute("listProductCategory",  productCategoryService.getAllProductCategory());
        String username = authentication.getName();
        User user = userRepository.findByUsername(username);
        model.addAttribute("user", user);
        model.addAttribute("username", username);

        return "Admin/new_product";
    }


    @PostMapping("/admin/addProduct")
    public String addProduct(@Valid @ModelAttribute("product") Product product, BindingResult bindingResult, @RequestParam("image") MultipartFile file, Model model) throws IOException, SerialException, SQLException {
        if (file.isEmpty()) {
            product.setImage(null);
        } else {
            byte[] bytes = file.getBytes();
            Blob blob = new javax.sql.rowset.serial.SerialBlob(bytes);
            product.setImage(blob);
        }
        product.setCreateTime(LocalDateTime.now());
        product.setStatus("2");
        productService.saveProduct(product);
        return "redirect:/admin/list_product";
    }






    @PostMapping("/admin/{id}/updateStatus")
    public String updateProductStatus(@PathVariable("id") Long id, @RequestParam("status") String status, Model model, HttpSession session, HttpServletRequest request) {
        Product product = productRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid ticket id: " + id));
        product.setStatus(status);
        productRepository.save(product);
        String referer = request.getHeader("Referer");


        return "redirect:" + referer;
    }



    @PostMapping("/admin/updateProduct")
    public String updateProduct(@ModelAttribute("product") @Valid  Product product, BindingResult bindingResult,
                           Model model,
                           @RequestParam("image") MultipartFile file) throws IOException, SerialException, SQLException {

        if (file.isEmpty()) {
            product.setImage(null);
        } else {
            byte[] bytes = file.getBytes();
            Blob blob = new javax.sql.rowset.serial.SerialBlob(bytes);
            product.setImage(blob);
        }

            productService.saveProduct(product);
            return "redirect:/admin/list_product";

    }
    @GetMapping("/admin/showFormForUpdateProduct/{id}")
    public String showFormForUpdateSP(Authentication authentication,@PathVariable(value = "id") long id, Model model) {
        Product product = productService.getProductById(id);
        model.addAttribute("product", product);
        model.addAttribute("listProductCategory", productCategoryService.getAllProductCategory());
        String username = authentication.getName();
        User user = userRepository.findByUsername(username);
        model.addAttribute("user", user);
        model.addAttribute("username", username);

        return "Admin/update_product";
    }
    @GetMapping("/admin/deleteProduct/{id}")
    public String deleteProduct(@PathVariable(value = "id") long id) {
        this.productService.deleteProductById(id);
        return "redirect:/admin/list_product";
    }

    @PostMapping("/admin/product/search")
    public String searchTicket(@RequestParam("keyword") String keyword, Model model ,Authentication authentication ) {
        String username = authentication.getName();
        model.addAttribute("listStaff", userService.getAllUser());
        model.addAttribute("listRole", roleService.getAllRole());
        User user = userRepository.findByUsername(username);
        model.addAttribute("user", user);
        model.addAttribute("username", username);
        List<Product> products = productService.searchProductAdmin(keyword);
        if (products.isEmpty()) {
            String errorMessage = "No matching products found";
            model.addAttribute("errorMessage", errorMessage);
        } else {
            model.addAttribute("listProduct", products);
        }

        return "Admin/list_product"  ;
    }

    @GetMapping("/admin/exportDishes-pdf")
    public void exportPdf(HttpServletResponse response) throws Exception {
        // Lấy danh sách ticket
        List<Product> products = productRepository.findAll();

        // Tạo tài liệu PDF bằng iText
        Document document = new Document();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter.getInstance(document, baos);
        document.open();

        // Tạo bảng để hiển thị thông tin ticket
        PdfPTable table = new PdfPTable(7);
        table.setWidthPercentage(100f);
        table.setSpacingBefore(10f);
        table.setSpacingAfter(10f);

        // Tạo tiêu đề cho các cột
        PdfPCell noCell = new PdfPCell(new Paragraph("No."));
        PdfPCell idCell = new PdfPCell(new Paragraph("Product ID"));
        PdfPCell nameCell = new PdfPCell(new Paragraph("Name"));
        PdfPCell productCategoryCell = new PdfPCell(new Paragraph("Product Category"));
        PdfPCell priceCell = new PdfPCell(new Paragraph("Price"));
        PdfPCell quantityCell = new PdfPCell(new Paragraph("Quantity"));
        PdfPCell descriptionCell = new PdfPCell(new Paragraph("Description"));

        // Thiết lập style cho các cột
        noCell.setBackgroundColor(BaseColor.LIGHT_GRAY);
        idCell.setBackgroundColor(BaseColor.LIGHT_GRAY);
        nameCell.setBackgroundColor(BaseColor.LIGHT_GRAY);
        productCategoryCell.setBackgroundColor(BaseColor.LIGHT_GRAY);
        priceCell.setBackgroundColor(BaseColor.LIGHT_GRAY);
        quantityCell.setBackgroundColor(BaseColor.LIGHT_GRAY);
        descriptionCell.setBackgroundColor(BaseColor.LIGHT_GRAY);

        // Thêm các cột vào bảng
        table.addCell(noCell);
        table.addCell(idCell);
        table.addCell(nameCell);
        table.addCell(productCategoryCell);
        table.addCell(priceCell);
        table.addCell(quantityCell);
        table.addCell(descriptionCell);

        // Thêm thông tin của từng ticket vào bảng
        int rowNum = 1;
        for (Product product : products) {
            table.addCell(String.valueOf(rowNum));
            table.addCell(product.getId().toString());
            table.addCell(product.getName());
            table.addCell(product.getProductCategory().getName());
            table.addCell(String.valueOf(product.getPrice()));
            table.addCell(String.valueOf(product.getQuantity()));
            table.addCell(product.getDescription());
            rowNum++;
        }

        // Thêm bảng vào tài liệu PDF
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        document.add(new Paragraph("Dishes list"));
        document.add(new Paragraph("Export Date: " + dateFormat.format(new Date())));
        document.add(table);
        document.close();

        // Thiết lập thông tin trả về
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=\"Dishes-list.pdf\"");
        response.setContentLength(baos.size());

        // Ghi tài liệu PDF vào Response
        ServletOutputStream outputStream = response.getOutputStream();
        baos.writeTo(outputStream);
        outputStream.flush();
    }


    @GetMapping("/admin/exportDishes-excel")
    public void exportExcel(HttpServletResponse response) throws Exception {

        List<Product> products = productRepository.findAll();

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
        idCell.setCellValue("Dishes ID");
        idCell.setCellStyle(headerStyle);

        Cell nameCell = headerRow.createCell(2);
        nameCell.setCellValue("Name");
        nameCell.setCellStyle(headerStyle);

        Cell produtcCategoryCell = headerRow.createCell(3);
        produtcCategoryCell.setCellValue("Product Category");
        produtcCategoryCell.setCellStyle(headerStyle);

        Cell priceCell = headerRow.createCell(4);
        priceCell.setCellValue("price");
        priceCell.setCellStyle(headerStyle);

        Cell quantityCell = headerRow.createCell(5);
        quantityCell.setCellValue("Quantity");
        quantityCell.setCellStyle(headerStyle);

        Cell descriptionCell = headerRow.createCell(6);
        descriptionCell.setCellValue("Description");
        descriptionCell.setCellStyle(headerStyle);
        Cell statusCell = headerRow.createCell(7);
        statusCell.setCellValue("Status");
        statusCell.setCellStyle(headerStyle);

        // Thêm thông tin của từng ticket vào sheet
        int rowNum = 1;
        for (Product product : products) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(rowNum - 1);
            row.createCell(1).setCellValue(product.getId().toString());
            row.createCell(2).setCellValue(product.getName());
            row.createCell(3).setCellValue(product.getProductCategory().getName());
            row.createCell(4).setCellValue(product.getPrice());
            row.createCell(5).setCellValue(product.getQuantity());
            row.createCell(6).setCellValue(product.getDescription());
            row.createCell(7).setCellValue(product.getStatusString());
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
        for (int i = 0; i < 8; i++) {
            sheet.autoSizeColumn(i);
            sheet.setColumnWidth(i, sheet.getColumnWidth(i) + 1000);
        }

        // Thiết lập thông tin trả về
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=\"All-dishes.xlsx\"");

        // Ghi workbook Excel vào Response
        workbook.write(response.getOutputStream());
        workbook.close();
    }
}
