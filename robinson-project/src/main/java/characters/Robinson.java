package characters;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.lang.Math;
import abstracts.Animal;
import abstracts.IslandEntity;
import data.LocalPosition;
import enums.FoodType;
import exceptions.HuntingException;
import exceptions.StarvationDeathException;
import exceptions.TamingException;
import interfaces.Huntable;
import interfaces.Tameable;
import records.FoodInfo;
import resources.Food;
import resources.Location;

public class Robinson extends IslandEntity {
    private int hunger;
    private Location currentLocation;
    private List<Animal> tamedAnimals;
    private List<Food> foodSupply;
    
    public Robinson(String name, Location startingLocation) {
        super(name, new LocalPosition(0, 0));
        this.hunger = 0;
        this.currentLocation = startingLocation;
        this.tamedAnimals = new ArrayList<>();
        this.foodSupply = new ArrayList<>();
    }
    
    public boolean moveToLocation(Location newLocation) {
        if (newLocation != null) {
            this.currentLocation = newLocation;
            this.localPosition.setX(0);
            this.localPosition.setY(0);
            System.out.println(name + " перешел в локацию: " + newLocation.getInfo().name());
            hunger += 10;
            checkStarvation();
            return true;
        }
        return false;
    }

    @Override
    public void moveTo(LocalPosition newPosition) {
        if (this.currentLocation.contains(newPosition)) {
            this.localPosition.setX(newPosition.getX());
            this.localPosition.setY(newPosition.getY());
            hunger += 5;
            checkStarvation();
        }
        else {
            System.out.println(name + " не может туда пойти!");
        }
    }

    @Override
    public void moveBy(int dx, int dy) {
        LocalPosition newPosition = new LocalPosition(this.localPosition.getX() + dx, this.localPosition.getY() + dy);
        if (this.currentLocation.contains(newPosition)) {
            this.setLocalPosition(newPosition);
            hunger += 5;
            checkStarvation();
        }
        else {
            System.out.println(name + " не может туда пойти!");
        }
    }
    
    public Location getCurrentLocation() {
        return currentLocation;
    }
    
    public void eat() {
        if (!foodSupply.isEmpty()) {
            Food food = foodSupply.get(0);
            if (food.getQuantity() > 0) {
                food.consume(1);
                hunger = Math.max(0, hunger - food.getFoodInfo().nutritionalValue());
                System.out.println(name + " съел " + food.getName() + ". Голод: " + hunger);
                if (food.getQuantity() == 0) {
                    foodSupply.remove(food);
                }
            }
        } else {
            System.out.println("Нет еды в запасе!");
        }
    }

    public void eat(Food food) {
        if (food.getQuantity() > 0) {
            food.consume(1);
            hunger = Math.max(0, hunger - food.getFoodInfo().nutritionalValue());
            System.out.println(name + " съел " + food.getName() + ". Голод: " + hunger);
            }
        else {
            throw new RuntimeException("Количество переданной еды не натурально!");
        }
    }
    
    public boolean tryHunt(Animal animal) throws HuntingException {
        if (animal == null) {
            throw new HuntingException("добыча не определена");
        }

        if (!animal.isAlive()) {
            throw new HuntingException("добыча уже мертва");
        }

        if(!(animal instanceof Huntable)) {
            throw new HuntingException("на это животное нельзя охотиться");
        }

        double attempt = Math.random();
        hunger += 10;
        if (attempt >= 0.6) {
            Huntable huntable = (Huntable) animal;
            System.out.println(name + " подстрелил " + animal.getName());
            
            Food meat = new Food(
                new FoodInfo(huntable.getMeatName(), FoodType.MEAT, huntable.getMeatNutrition()),
                1
            );
            collectFood(meat);
            
            animal.die();
            currentLocation.removeAnimal(animal);
            checkStarvation();
            return true;
        }
        else {
            System.out.println(name + " не смог поймать " + animal.getName());
            checkStarvation();
            return false;
        }
    }
    
    public boolean tryTame(Animal animal) throws TamingException {
        if (animal == null) {
            throw new TamingException("животное не определено");
        }

        if (!animal.isAlive()) {
            throw new TamingException("животное мертво");
        }

        if(!(animal instanceof Tameable)) {
            throw new TamingException("это животное нельзя приручить");
        }

        Tameable tameable = (Tameable) animal;
        double attempt = Math.random();
        hunger += 10;
        if (attempt >= 0.8) {
            tameable.tame();
            tamedAnimals.add(animal);
            System.out.println(name + " приручил " + animal.getName());
            currentLocation.removeAnimal(animal);
            checkStarvation();
            return true;
        }
        else {
            System.out.println(name + " не смог приручить " + animal.getName());
            checkStarvation();
            return false;
        }
    }
    
    public List<Animal> explore() {
        List<Animal> foundAnimals = currentLocation.getAnimals();
        System.out.println(name + " исследует локацию. Найдено животных: " + foundAnimals.size());
        hunger += 5;
        checkStarvation();
        return foundAnimals;
    }
    
    public void collectFood(Food food) {
        foodSupply.add(food);
        hunger += 5;
        checkStarvation();
        System.out.println(name + " собрал: " + food);
    }
    
    public String getHungerStatus() {
        if (hunger <= 10) return "Сытый";
        else if (hunger <= 30) return "Слегка голоден";
        else if (hunger <= 50) return "Голоден";
        else return "Очень голоден";
    }
    
    public void printFoodSupply() {
        System.out.println("Запас еды " + name + ":");
        for (Food food : foodSupply) {
            System.out.println("  - " + food);
        }
    }

    public List<Animal> getTamedAnimals() {
        return tamedAnimals;
    }

    public List<Food> getFoodSupply() {
        return foodSupply;
    }
    
    public int getHunger() {
        return hunger;
    }
    
    public void checkStarvation() {
        if (hunger >= 100) {
            die();
            System.out.println(name + " умер от голода!");
            throw new StarvationDeathException(name + " погиб.");
        }
    }
    
    public void cookFood(Food food) {
        food.cook();
        hunger += 5;
        checkStarvation();
    }
    
    public void cookFoodWithRaisin(Food meat, Food raisin) {
        if (meat.getFoodInfo().type() == FoodType.MEAT) {
            meat.cook(raisin);
            hunger += 5;
            checkStarvation();
        }
    }
    
    public void goToSleep() {
        System.out.println(name + " ложится спать.");
        System.out.println("Конец дня на острове.");
        System.out.println("Итоги дня:");
        System.out.println("- Прирученных животных: " + tamedAnimals.size());
        System.out.println("- Запас еды: " + foodSupply.size() + " видов");
        System.out.println("- Уровень голода: " + hunger + " (" + getHungerStatus() + ")");
        System.out.println("- Текущая локация: " + currentLocation.getInfo().name());
        System.out.println("Сладких снов, " + name + "!");
    }
    
    @Override
    public String getDescription() {
        return "Робинзон Крузо по имени " + name;
    }
    
    @Override
    public String toString() {
        return getDescription() + " в локации " + currentLocation.getInfo().name() + 
               ", голод: " + hunger + ", прирученных животных: " + tamedAnimals.size();
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Robinson robinson = (Robinson) o;
        return hunger == robinson.hunger && 
               Objects.equals(currentLocation, robinson.currentLocation) && 
               Objects.equals(tamedAnimals, robinson.tamedAnimals) && 
               Objects.equals(foodSupply, robinson.foodSupply);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), hunger, currentLocation, tamedAnimals, foodSupply);
    }
}
