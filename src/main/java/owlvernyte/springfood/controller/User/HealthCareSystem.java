package owlvernyte.springfood.controller.User;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class HealthCareSystem {
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

            } else {
                System.out.println("Lỗi - Mã phản hồi HTTP: " + responseCode);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    private static double calculateBMI() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Nhập cân nặng của bạn (kg): ");
        double weight = scanner.nextDouble();
        System.out.print("Nhập chiều cao của bạn (m): ");
        double height = scanner.nextDouble();
        double bmi = weight / (height * height);
        System.out.printf("Chỉ số BMI của bạn là: %.2f\n", bmi);
        return bmi;
    }

    private static void healthAdvice(String apiKey, double bmi) {
        String prompt = "Gợi ý món ăn cho tôi theo chỉ số BMI = " + bmi;
        String response = makeOpenAIRequest(prompt, apiKey);
        System.out.println("\nLời khuyên cải thiện: " + response);
    }

    private static void setGoal(String apiKey, double bmi) {
        String prompt = "Suggest a fitness goal based on BMI " + bmi;
        String response = makeOpenAIRequest(prompt, apiKey);
        System.out.println("\nFitness Goal: " + response);
    }

    private static void createDietPlan(String apiKey, double bmi) {
        String prompt = "Gợi ý thực đơn ăn kiêng 3 bữa dựa trên chỉ số BMI=" + bmi;
        String response = makeOpenAIRequest(prompt, apiKey);
        System.out.println("\nDiet Plan: " + response);
    }

    private static void createWorkoutPlan(String apiKey, double bmi) {
        String prompt = "Suggest a Workout plan based on BMI " + bmi;
        String response = makeOpenAIRequest(prompt, apiKey);
        System.out.println("\nWorkout Plan: " + response);
    }

    private static void createMeditationGuide(String apiKey, double bmi) {
        String prompt = "Suggest a Meditation Guide based on BMI " + bmi;
        String response = makeOpenAIRequest(prompt, apiKey);
        System.out.println("\nMeditation Guide: " + response);
    }

    public static void main(String[] args) {
        String apiKey = "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";
        double bmi = 0;

        while (true) {
            System.out.println("\n*****\tHealth Care System\t*****\n");
            System.out.println("1- Calculate BMI ");
            System.out.println("2- Get health advice ");
            System.out.println("3- Set a Goal ");
            System.out.println("4- Create a diet plan ");
            System.out.println("5- Create a Workout plan ");
            System.out.println("6- Suggest a Medidation Guide plan ");
            System.out.print("\nEnter your choice: ");

            Scanner scanner = new Scanner(System.in);
            int choice = scanner.nextInt();

            switch (choice) {
                case 1:
                    bmi = calculateBMI();
                    break;
                case 2:
                    System.out.println("\nGenerating Health Advice....");
                    healthAdvice(apiKey, bmi);
                    break;
                case 3:
                    System.out.println("\nSetting a Goal....");
                    setGoal(apiKey, bmi);
                    break;
                case 4:
                    System.out.println("\nCreating a diet plan....");
                    createDietPlan(apiKey, bmi);
                    break;
                case 5:
                    System.out.println("\nCreating a Workout plan....");
                    createWorkoutPlan(apiKey, bmi);
                    break;
                case 6:
                    System.out.println("\nCreating a Meditation Guide ....");
                    createMeditationGuide(apiKey, bmi);
                    break;
                default:
                    System.out.println("Wrong Input.");
            }
        }
    }
}