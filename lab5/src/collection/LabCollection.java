package collection;

import java.time.LocalDate;
import java.util.Collections;
import java.util.LinkedList;
import data.LabWork;

public class LabCollection {
    private static final LabCollection INSTANCE = new LabCollection();
    private int maxUsedId = 0;
    private LocalDate initializationDate = LocalDate.now(); //TODO COMPLETE
    private LinkedList<LabWork> collection = new LinkedList<LabWork>();

    private LabCollection() {}

    private void updateMaxUsedId() {
        maxUsedId = 0;
        for (int i = 0; i < collection.size(); i++) {
            if (collection.get(i).getId() > maxUsedId) {
                maxUsedId = collection.get(i).getId();
            }
        }
    }

    public static LabCollection getInstance() {
        return INSTANCE;
    }

    public LinkedList<LabWork> getCollection() {
        return collection;
    }

    public void setCollection(LinkedList<LabWork> collection) {
        this.collection = collection;
        updateMaxUsedId();
        //TODO MAKE INIT DATE UPDATING
    }

    public void initializeSorting() {
        Collections.sort(collection);
    }

    public void addElement(LabWork labWork) {
        collection.add(labWork);
        Collections.sort(collection);
        updateMaxUsedId();
    }

    public LabWork getElementById(int id) {
        for (int i = 0; i < collection.size(); i++) {
            if (collection.get(i).getId() == id) {
                return collection.get(i);
            }
        }
        return null;
    }

    public boolean delElement(LabWork labWork) {
        if (collection.contains(labWork)) {
            collection.remove(labWork);
            updateMaxUsedId();
            return true;
        }
        return false;
    }

    public boolean delElementById(int id) {
        for (int i = 0; i < collection.size(); i++) {
            if (collection.get(i).getId() == id) {
                collection.remove(collection.get(i));
                updateMaxUsedId();
                return true;
            }
        }
        return false;
    }

    public boolean delElementByIndex(int index) {
        if (collection.size() <= index) {
            return false;
        }
        collection.remove(index);
        updateMaxUsedId();
        return true;
    }

    public LabWork getMinElement() {
        return collection.getFirst();
    }

    public int getNextId() {
        return maxUsedId + 1;
    }

    public void clear() {
        collection.clear();
        updateMaxUsedId();
    }

    public int getLength() {
        return collection.size();
    }

    public LocalDate getInitializationDate() {
        return initializationDate;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        String stopLine = "~".repeat(50);
        sb.append(stopLine);
        if (!collection.isEmpty()) {
            for (LabWork labWork : collection) {
                sb.append(labWork);
            }
        }
        else {
            sb.append("\nNothing is here yet!\n");
        }
        sb.append(stopLine);
        sb.append("\n");
        return sb.toString();
    }
}
