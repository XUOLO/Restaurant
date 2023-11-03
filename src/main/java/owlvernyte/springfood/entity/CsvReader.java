package owlvernyte.springfood.entity;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
public class CsvReader {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public List<Integer> getMatchingProductsFromCsv(String csvFilePath,long currentUserId) {
        List<Integer> matchingProducts = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(csvFilePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                String userIdStr = data[0].trim();
                String recommendedProductIdsStr = data[1].trim();

                try {
                    long userId = Long.parseLong(userIdStr);
                    long recommendedProductIds = Long.parseLong(recommendedProductIdsStr);

                    if (userId == currentUserId) {
                        String sql = "SELECT id FROM product WHERE id = ?";
                        List<Integer> result = jdbcTemplate.queryForList(sql, Integer.class, recommendedProductIds);
                        if (!result.isEmpty()) {
                            matchingProducts.add((int) recommendedProductIds);
                        }
                    }
                } catch (NumberFormatException e) {
                    // Handle the case where userIdStr or recommendedProductIdsStr is not a valid integer
                    System.err.println("Invalid integer value: " + userIdStr + " or " + recommendedProductIdsStr);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return matchingProducts;
    }


    public boolean isMatchingUserFromCsv(String csvFilePath, long currentUserId) {
        try (BufferedReader br = new BufferedReader(new FileReader(csvFilePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                String userIdStr = data[0].trim();

                try {
                    long userId = Long.parseLong(userIdStr);

                    if (userId == currentUserId) {
                        return true;
                    }
                } catch (NumberFormatException e) {
                    // Handle the case where userIdStr is not a valid integer
                    System.err.println("Invalid integer value: " + userIdStr);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }

}