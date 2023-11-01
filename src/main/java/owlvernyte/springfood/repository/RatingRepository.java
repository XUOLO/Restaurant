package owlvernyte.springfood.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import owlvernyte.springfood.entity.Product;
import owlvernyte.springfood.entity.Rating;

import java.util.List;


@Repository
public interface RatingRepository extends JpaRepository<Rating, Long> {
    List<Rating> findByProductId(Long productId);
    List<Rating> findByProduct(Product product);
    Rating findByProductIdAndUserId(Long productId, Long userId);



}
