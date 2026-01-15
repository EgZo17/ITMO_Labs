import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.lang.Thread;
import abstracts.Animal;
import animals.*;
import characters.Robinson;
import enums.FoodType;
import exceptions.HuntingException;
import exceptions.TamingException;
import exceptions.TasksUndoneException;
import generators.*;
import interfaces.Huntable;
import interfaces.Tameable;
import records.*;
import resources.*;

public class Main {
    private Robinson robinson;
    private List<Location> islandLocations;
    private static final Random random = new Random();
    
    public Main() {
        islandLocations = new ArrayList<>();
    }
    
    public static void main(String[] args) throws HuntingException, TamingException, InterruptedException {
        System.out.println("Начало нового дня.");
        
        Main main = new Main();
        main.initializeIsland();
        main.createRobinson();
        main.runSimulation();
    }

    private static void fillLocationWithAnimals(Location location) {
        int goatsQuantity = random.nextInt(4);
        int turtlesQuantity = random.nextInt(4);
        int foxesQuantity = random.nextInt(4);
        int pigeonsQuantity = random.nextInt(4);
        int parrotsQuantity = random.nextInt(4);

        for (int i = 0; i < goatsQuantity; i++) {
            location.addAnimal(new Goat(AnimalNameGenerator.getRandomGoatName(), LocalPositionGenerator.getRandomPosition(location)));
        }
        for (int i = 0; i < turtlesQuantity; i++) {
            location.addAnimal(new Turtle(AnimalNameGenerator.getRandomTurtleName(),
                                            LocalPositionGenerator.getRandomPosition(location), random.nextInt(11)));
        }
        for (int i = 0; i < foxesQuantity; i++) {
            location.addAnimal(new Fox(AnimalNameGenerator.getRandomFoxName(),
                                            LocalPositionGenerator.getRandomPosition(location), random.nextBoolean()));
        }
        for (int i = 0; i < pigeonsQuantity; i++) {
            location.addAnimal(new Pigeon(AnimalNameGenerator.getRandomPigeonName(),
                                            LocalPositionGenerator.getRandomPosition(location), random.nextBoolean()));
        }
        for (int i = 0; i < parrotsQuantity; i++) {
            location.addAnimal(new Parrot(AnimalNameGenerator.getRandomParrotName(), LocalPositionGenerator.getRandomPosition(location)));
        }
    }
    
    private void initializeIsland() {

        // Создание локаций острова
        LocationInfo beachInfo = new LocationInfo("Пляж", "Песчаный пляж с пальмами", 100, 50);
        LocationInfo meadowInfo = new LocationInfo("Луга", "Зеленые луга с цветами", 200, 150);
        LocationInfo forestInfo = new LocationInfo("Лес", "Красивые рощи с попугаями", 150, 200);
        
        Location beach = new Location(beachInfo);
        Location meadow = new Location(meadowInfo);
        Location forest = new Location(forestInfo);
        
        // Добавление животных на локации
        fillLocationWithAnimals(beach);
        fillLocationWithAnimals(meadow);
        fillLocationWithAnimals(forest);
        
        // Добавление ресурсов
        beach.addResource(new Food(new FoodInfo("Изюм", FoodType.FRUIT, 30), (random.nextInt(5) + 1)));
        meadow.addResource(new Food(new FoodInfo("Изюм", FoodType.FRUIT, 30), (random.nextInt(5) + 1)));
        forest.addResource(new Food(new FoodInfo("Изюм", FoodType.FRUIT, 30), (random.nextInt(5) + 1)));
        
        // Добавление готовых локаций
        islandLocations.add(beach);
        islandLocations.add(meadow);
        islandLocations.add(forest);
    }
    
    private void createRobinson() {
        robinson = new Robinson("Робинзон Крузо", islandLocations.get(random.nextInt(islandLocations.size())));
    }
    
    private void runSimulation() throws HuntingException, TamingException, InterruptedException {
        
        // 1.
        System.out.println(robinson.getName() + " проснулся.");
        Thread.sleep(1000);

        // 2.
        Location newLocation = robinson.getCurrentLocation();
        while (newLocation == robinson.getCurrentLocation()) {
            newLocation = islandLocations.get(random.nextInt(islandLocations.size()));
        }
        robinson.moveToLocation(newLocation);
        System.out.println(robinson.getCurrentLocation().getInfo());
        Thread.sleep(1000);

        // 3.
        List<Animal> locationAnimals = robinson.explore();
        Thread.sleep(1000);

        // 4-6.
        Animal animal;
        while ((robinson.getTamedAnimals().isEmpty() || robinson.getFoodSupply().isEmpty()) && !(locationAnimals.isEmpty())) {
            animal = locationAnimals.get(random.nextInt(locationAnimals.size()));
            if (animal instanceof Huntable) {
                if (robinson.tryHunt(animal)) {
                    locationAnimals.remove(animal);
                }
                Thread.sleep(1000);
            }
            if (animal instanceof Tameable) {
                if (robinson.tryTame(animal)) {
                    locationAnimals.remove(animal);
                }
                Thread.sleep(1000);
            }
            if (!(robinson.getCurrentLocation().getResources().isEmpty())) {
                Food raisin = robinson.getCurrentLocation().getResources().get(0);
                robinson.collectFood(raisin);
                robinson.getCurrentLocation().removeResource(raisin);
                Thread.sleep(1000);
            }
            if (robinson.getHunger() > 70) {
                robinson.eat();
                Thread.sleep(1000);
            }
            if (locationAnimals.isEmpty()) {
                if (islandLocations.get(0).getAnimals().isEmpty() &&
                        islandLocations.get(1).getAnimals().isEmpty() &&
                        islandLocations.get(2).getAnimals().isEmpty()) {
                    throw new TasksUndoneException(robinson.getName() + " не смог выполнить все дела на сегодня.");
                }
                else {
                    newLocation = robinson.getCurrentLocation();
                    while (newLocation == robinson.getCurrentLocation() || newLocation.getAnimals().isEmpty()) {
                        newLocation = islandLocations.get(random.nextInt(islandLocations.size()));
                    }
                    robinson.moveToLocation(newLocation);
                    Thread.sleep(1000);
                    System.out.println(robinson.getCurrentLocation().getInfo());
                    Thread.sleep(1000);
                    locationAnimals = robinson.explore();
                    Thread.sleep(1000);
                }
            }
        }

        // 7.
        Parrot friend = (Parrot) robinson.getTamedAnimals().get(0);
        List<String> someWords = Arrays.asList(robinson.getName(), friend.getName(), "Робинзон", "Попугай", "Изюм", "Дур-рак");
        friend.learnWord(someWords.get(random.nextInt(someWords.size())));
        Thread.sleep(1000);
        friend.speak();
        Thread.sleep(1000);

        // 8-9.
        List<Food> meatToCook = new ArrayList<>();
        Food food;
        Food raisin = null;
        Food meat;
        for (int i = 0; i < robinson.getFoodSupply().size(); i++) {
            food = robinson.getFoodSupply().get(i);
            if (!(food.getName().toLowerCase().contains("изюм"))) {
                meatToCook.add(food);
            }
            else {
                raisin = food;
            }
        }
        if (!(meatToCook.isEmpty())) {
            meat = meatToCook.get(random.nextInt(meatToCook.size()));
            if (raisin != null) {
                robinson.cookFoodWithRaisin(meat, raisin);
                Thread.sleep(1000);
                robinson.eat(meat);
                Thread.sleep(1000);
            }
            else {
                robinson.cookFood(meat);
                Thread.sleep(1000);
                robinson.eat(meat);
                Thread.sleep(1000);
            }
        }
        else {
            robinson.eat(raisin);
            Thread.sleep(1000);
        }

        // 10.
        robinson.goToSleep();
    }
}
