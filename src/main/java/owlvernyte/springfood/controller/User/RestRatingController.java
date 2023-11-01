package owlvernyte.springfood.controller.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import owlvernyte.springfood.service.RatingService;

@RestController
public class RestRatingController {

    @Autowired
    private RatingService ratingService;

    @PostMapping("/user/api/ratings")
    public ResponseEntity<String> rateProduct(@RequestParam("productId") Long productId,
                                              @RequestParam("rating") int rating,
                                              @RequestParam("userId") Long userId) {
        try {
            ratingService.rateProduct(productId, rating, userId);
            return ResponseEntity.ok("Rating saved successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to save rating");
        }
    }
}