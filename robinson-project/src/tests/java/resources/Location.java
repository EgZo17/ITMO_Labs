package resources;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import abstracts.Animal;
import data.LocalPosition;
import records.LocationInfo;

public class Location {
    private LocationInfo info;
    private List<Animal> animals;
    private List<Food> resources;
    
    public Location(LocationInfo info) {
        this.info = info;
        this.animals = new ArrayList<>();
        this.resources = new ArrayList<>();
    }
    
    public void addAnimal(Animal animal) {
        animals.add(animal);
    }
    
    public void addResource(Food food) {
        resources.add(food);
    }
    
    public List<Animal> getAnimals() {
        return new ArrayList<>(animals);
    }
    
    public List<Food> getResources() {
        return new ArrayList<>(resources);
    }
    
    public void removeAnimal(Animal animal) {
        animals.remove(animal);
    }
    
    public void removeResource(Food food) {
        resources.remove(food);
    }
    
    public LocationInfo getInfo() {
        return info;
    }
    
    public boolean contains(LocalPosition position) {
        return info.isPositionValid(position);
    }
    
    public List<Animal> findAnimalsNear(LocalPosition point, double radius) {
        List<Animal> nearbyAnimals = new ArrayList<>();
        for (Animal animal : animals) {
            if (animal.getLocalPosition().distanceTo(point) <= radius) {
                nearbyAnimals.add(animal);
            }
        }
        return nearbyAnimals;
    }
    
    @Override
    public String toString() {
        return info.toString() + "\nЖивотные: " + animals.size() + 
               ", Ресурсы: " + resources.size();
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Location location = (Location) o;
        return Objects.equals(info, location.info) && 
               Objects.equals(animals, location.animals) && 
               Objects.equals(resources, location.resources);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(info, animals, resources);
    }
}
