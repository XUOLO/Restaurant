package owlvernyte.springfood.controller.User;

import org.springframework.stereotype.Component;
import owlvernyte.springfood.entity.Order;
import owlvernyte.springfood.entity.Product;
import owlvernyte.springfood.entity.User;
import owlvernyte.springfood.service.UserService;

import java.util.*;
@Component
public class FoodSuggestion {
    private final UserService userService;

    public FoodSuggestion(UserService userService) {
        this.userService = userService;
    }

    public List<Product> suggestFoodBasedOnOrderHistory(Long userId) {
        List<Product> suggestedFoods = new ArrayList<>();

        // Step 1: Query user's order history and sort by order date
        User user = userService.viewById(userId);
        List<Order> orderHistory = user.getOrders();
        orderHistory.sort(Comparator.comparing(Order::getOrderDate).reversed());

        // Step 2: Get all products from the orders in the order history
        Set<Product> orderedProducts = new HashSet<>();
        for (Order order : orderHistory) {
            List<Product> products = order.getProducts();
            orderedProducts.addAll(products);
        }

        // Step 3: Generate food suggestions based on ordered products
        for (Product product : orderedProducts) {
            // Add product to suggested foods
            suggestedFoods.add(product);
        }

        return suggestedFoods;
    }
}