package owlvernyte.springfood.entity;

import jakarta.persistence.Table;

@Table(name = "meal")
public class MealDB {

    private  int mealId;
    private String name;
    private String thumbMeal;

    public int getMealId() {
        return mealId;
    }

    public void setMealId(int mealId) {
        this.mealId = mealId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getThumbMeal() {
        return thumbMeal;
    }

    public void setThumbMeal(String thumbMeal) {
        this.thumbMeal = thumbMeal;
    }
}
