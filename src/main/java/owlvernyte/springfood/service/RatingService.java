package owlvernyte.springfood.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import owlvernyte.springfood.entity.Product;
import owlvernyte.springfood.entity.Rating;
import owlvernyte.springfood.entity.User;
import owlvernyte.springfood.repository.RatingRepository;

@Service
public class RatingService {

    @Autowired
    private RatingRepository ratingRepository;

    public void rateProduct(Long productId, int rating, Long userId) {
        Rating existingRating = ratingRepository.findByProductIdAndUserId(productId, userId);
        if (existingRating != null) {
            // Update existing rating
            existingRating.setRatingValue(rating);
            ratingRepository.save(existingRating);
        } else {
            // Create new rating
            Rating newRating = new Rating();
            Product product = new Product();
            product.setId(productId);
            newRating.setProduct(product);

            User user = new User();
            user.setId(userId);
            newRating.setUser(user);
            ratingRepository.save(newRating);
        }
    }
}