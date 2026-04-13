package com.labwork.commands;

import java.io.IOException;
import javax.xml.bind.JAXBException;
import com.labwork.utils.Command;
import com.labwork.utils.FileManager;

public class Save implements Command {
    public Save() {}

    @Override
    public void execute(String[] parameters) {
        if (!validate(parameters)) {
            return;
        }
        try {
            FileManager.saveCollection();
        } catch (JAXBException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
