package owlvernyte.springfood.controller.User;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import owlvernyte.springfood.entity.Product;
import owlvernyte.springfood.service.ProductService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.Random;

@Controller
public class BMIController {

    @Autowired
    private ProductService productService;
    private static String makeOpenAIRequest(String prompt, String apiKey) {
        String apiUrl = "https://api.openai.com/v1/engines/gpt-3.5-turbo-instruct/completions";
        String model = "gpt-3.5-turbo-instruct";

        int maxTokens = 500;

        try {
            URL url = new URL(apiUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Authorization", "Bearer " + apiKey);
            connection.setDoOutput(true);

            String data = String.format("{\"prompt\": \"%s\", \"max_tokens\": %d}", prompt, maxTokens);
            connection.getOutputStream().write(data.getBytes());

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();

                JsonObject jsonObject = new Gson().fromJson(response.toString(), JsonObject.class);
                JsonArray choices = jsonObject.getAsJsonArray("choices");
                JsonObject firstChoice = choices.get(0).getAsJsonObject();
                String generatedText = firstChoice.get("text").getAsString();

                return generatedText;


                //        String prompt = "With the following dishes list : Grilled Meat, Sprite, Vietnamese Hot Pot, Tiramisu Cake, Sting, Coca ,Beer tiger, Crème brûlée Cake, Baklava Cake, Cheese Cake, Panna cotta Cake, Chocolate lava cake, Sukiyak Hot Pot Japanese, Japanese Korean army stew hot pot, Chinese Sichuan hot pot, Thai Tom yum hot pot, Japanese Shabu shabu hot pot, Grilled fish, Bread, grilled cheese, Grilled potatoes, Grilled ribs, Grilled octopus. What food should I eat? according to BMI = " + bmi + " with gender: "+gender+" with age: "+age+". Reply in Vietnamese and just answer with the name of one item on the list I gave you(just a name translate of that dish into vietnamese) and no further explanation needed.reply like : Gợi ý cho bạn : + name one of dishes on the list .";
//        String response = makeOpenAIRequest(prompt, apiKey);
//        redirectAttributes.addFlashAttribute("response",response);


            } else {
                System.out.println("Lỗi - Mã phản hồi HTTP: " + responseCode);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    @GetMapping("/user/bmi")
    public  String bmiShow(){

        return "User/bmi";
    }


    @PostMapping("/user/calculate-bmi")
    public String calculateBMI(
            @RequestParam("weight") double weight,
            @RequestParam("height") double height

    , RedirectAttributes redirectAttributes) {
        double heightInMeters = height / 100;
        double bmi = weight / (heightInMeters * heightInMeters);
        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        String formattedBMI = decimalFormat.format(bmi);
        String apiKey = "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";
        Random random = new Random();
        String bmiCategory;
        if (bmi < 18.5) {
            bmiCategory = "cho thấy bạn đang bị thiếu cân";

            int[] numberArray = {3, 13, 14, 15,16,17}; // Dãy số cho sẵn
            int randomIndex = random.nextInt(numberArray.length);
            int randomNumber = numberArray[randomIndex];
            Product product =productService.getProductById(randomNumber) ;
            redirectAttributes.addFlashAttribute("product", product);
            redirectAttributes.addFlashAttribute("bmi",formattedBMI);
            redirectAttributes.addFlashAttribute("bmiCategory",bmiCategory);
        } else if (bmi >= 18.5 && bmi < 25) {

            bmiCategory = "cho thấy cơ thể bạn bình thường";
            int[] numberArray = {1, 21, 8, 9}; // Dãy số cho sẵn
            int randomIndex = random.nextInt(numberArray.length);
            int randomNumber = numberArray[randomIndex];
            Product product =productService.getProductById(randomNumber) ;
            redirectAttributes.addFlashAttribute("product", product);
            redirectAttributes.addFlashAttribute("bmi",formattedBMI);
            redirectAttributes.addFlashAttribute("bmiCategory",bmiCategory);
        } else if (bmi >= 25 && bmi < 30) {
            bmiCategory = "cho thấy bạn đang quá cân";
            int[] numberArray = {11, 10, 20}; // Dãy số cho sẵn
            int randomIndex = random.nextInt(numberArray.length);
            int randomNumber = numberArray[randomIndex];
            Product product =productService.getProductById(randomNumber) ;
            redirectAttributes.addFlashAttribute("product", product);
            redirectAttributes.addFlashAttribute("bmi",formattedBMI);
            redirectAttributes.addFlashAttribute("bmiCategory",bmiCategory);
        } else {
            bmiCategory = "cho thấy bạn đang bị béo phì";
            int[] numberArray = {4,8,9,10,11,12}; // Dãy số cho sẵn
            int randomIndex = random.nextInt(numberArray.length);
            int randomNumber = numberArray[randomIndex];
            Product product =productService.getProductById(randomNumber) ;
            redirectAttributes.addFlashAttribute("product", product);
            redirectAttributes.addFlashAttribute("bmi",formattedBMI);
            redirectAttributes.addFlashAttribute("bmiCategory",bmiCategory);
        }

        return "redirect:/user/bmi";
    }


}
