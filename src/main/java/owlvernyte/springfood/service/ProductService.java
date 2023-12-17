package owlvernyte.springfood.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import owlvernyte.springfood.entity.Order;
import owlvernyte.springfood.entity.Product;
import owlvernyte.springfood.entity.Rating;
import owlvernyte.springfood.repository.ProductRepository;
import owlvernyte.springfood.repository.RatingRepository;

import java.util.*;

@Service
public class ProductService {

    @Autowired
    private RatingRepository ratingRepository;
    @Autowired
    private ProductRepository productRepository;

    public List<Product> getAllProduct() {
        return productRepository.findAll();
    }

    public void saveProduct(Product product) {
        this.productRepository.save(product);

    }
    public Product viewById(long id) {
        return productRepository.findById(id).get();
    }

    public Product getProductById(long id) {
        Optional<Product> optional = productRepository.findById(id);
        Product product = null;
        if (optional.isPresent()) {
            product = optional.get();
        }
        else
        {
            throw new RuntimeException(" Cant find product id : " + id);
        }
        return product;
    }
    public void addRating(Long productId, double ratingValue) {
        // Tạo một đối tượng Rating mới
        Rating rating = new Rating();

        // Lấy thông tin sản phẩm từ cơ sở dữ liệu (nếu cần thiết)
        Product product = getProductById(productId);

        // Gán giá trị đánh giá và sản phẩm vào đối tượng Rating
        rating.setRatingValue(ratingValue);
        rating.setProduct(product);

        // Lưu đối tượng Rating vào cơ sở dữ liệu
        ratingRepository.save(rating);
    }
    public List<Rating> getProductRatings(Long productId) {
        return ratingRepository.findByProductId(productId);
    }
    public void deleteProductById(long id) {
        this.productRepository.deleteById(id);
    }
    public List<Product> searchProductAdmin(String keyword) {

        if(keyword!=null){
            return productRepository.findAll(keyword);
        }
        return productRepository.findAll();
    }




    public Page<Product> findPaginatedProduct(int pageNo, int pageSize){
        Pageable pageable= PageRequest.of(pageNo - 1,pageSize);
        return this.productRepository.findAll(pageable);
    }

    public Page<Product> findPaginatedProduct(int pageNo, int pageSize,String sortField,String sortDirection){
        Sort sort= sortDirection.equalsIgnoreCase(Sort.Direction.ASC.name())? Sort.by(sortField).ascending():
                Sort.by(sortField).descending();


        Pageable pageable= PageRequest.of(pageNo - 1,pageSize,sort);
        return this.productRepository.findAll(pageable);
    }


    public List<Product> getTopFourProductsById(List<Long> productIds) {
        return productRepository.findAllById(productIds);
    }


    private Map<String, List<Long>> bmiIdMap = new HashMap<>();

    public ProductService() {
        initializeBmiIdMap();
    }

    private void initializeBmiIdMap() {
        // Thiết lập sẵn các ID ngẫu nhiên cho từng khoảng BMI
        bmiIdMap.put("<18.5", Arrays.asList(3L, 13L, 14L,15L,16L,17L));
        bmiIdMap.put("18.5-24.9", Arrays.asList(1L, 21L, 8L,9L));
        bmiIdMap.put("25-29.9", Arrays.asList(11L, 10L, 20L));
        bmiIdMap.put(">=30", Arrays.asList(4L,8L,9L,10L, 11L, 12L));
    }

    public void setRandomIdByBmiCategory(Product product, String bmiCategory) {
        List<Long> idList = bmiIdMap.get(bmiCategory);
        if (idList != null && !idList.isEmpty()) {
            Random random = new Random();
            int randomIndex = random.nextInt(idList.size());
            Long randomId = idList.get(randomIndex);
            product.setId(randomId);
        }
    }

}
