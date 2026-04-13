package com.labwork.commands;

import com.labwork.collection.LabCollection;
import com.labwork.data.LabWork;
import com.labwork.utils.Command;

/**
 * Команда для вывода информации о коллекции: тип, дата инициализации, количество элементов.
 */

public class Info implements Command {
    private LabCollection labCollection = LabCollection.getInstance();

    public Info() {}

    @Override
    public void execute(String[] parameters) {
        if (!validate(parameters)) {
            return;
        }
        System.out.printf("Collection type: %s%nStored data type: %s%nCollection size: %d%nCreation date: %s%n",
                labCollection.getCollection().getClass().getName(), LabWork.class,
                labCollection.getLength(), labCollection.getInitializationDate());
    }
}
