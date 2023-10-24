package owlvernyte.springfood.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import owlvernyte.springfood.entity.Order;
import owlvernyte.springfood.entity.Product;
import owlvernyte.springfood.repository.ProductRepository;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {


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

}
