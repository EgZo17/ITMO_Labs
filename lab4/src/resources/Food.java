package resources;

import java.util.Objects;
import enums.FoodType;
import records.FoodInfo;

public class Food {
    private FoodInfo info;
    private int quantity;

    public Food(FoodInfo info, int quantity) {
        this.info = info;
        this.quantity = Math.max(0, quantity);
    }

    public String getName() {
        return info.name();
    }

    public FoodInfo getFoodInfo() {
        return info;
    }

    public int getQuantity() {
        return quantity;
    }

    public void consume(int amount) {
        if (amount <= 0 || amount > quantity) {
            throw new IllegalArgumentException("Неверное количество для потребления: " + amount);
        }
        quantity -= amount;
    }

    public void increaseQuantity(int amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Неверное количество для увеличения: " + amount);
        }
        quantity += amount;
    }

    public void cook() {
        if (this.info.type() == FoodType.MEAT) {
            int newNutritionalValue = (int)(this.info.nutritionalValue() * 1.1);
            this.info = new FoodInfo(
                this.info.name() + " (жареное)",
                this.info.type(),
                newNutritionalValue
            );
            System.out.println("Приготовлено: " + getName());
        }
    }

    public void cook(Food raisin) {
        if (this.info.type() == FoodType.MEAT && 
            raisin != null && 
            raisin.getName().toLowerCase().contains("изюм") &&
            raisin.getQuantity() >= this.getQuantity()) {
            int newNutritionalValue = (int)(this.info.nutritionalValue() * 1.25);
            this.info = new FoodInfo(
                this.info.name() + " (жареное с изюмом)",
                this.info.type(),
                newNutritionalValue
            );
            raisin.consume(this.getQuantity());
            System.out.println("Приготовили: " + getName());
        }
        else {
            if (this.info.type() == FoodType.MEAT &&
                raisin != null && 
                raisin.getName().toLowerCase().contains("изюм") &&
                raisin.getQuantity() < this.getQuantity()) {
                    System.out.println("Изюма оказалось недостаточно для приготовления: " + getName());
                }
            cook();
        }
    }

    @Override
    public String toString() {
        return info.name() + " (количество: " + quantity + ", питательность: " + info.nutritionalValue() + ")"; 
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Food food = (Food) o;
        return quantity == food.quantity && 
               Objects.equals(info, food.info);
    }

    @Override
    public int hashCode() {
        return Objects.hash(info, quantity);
    }
}
