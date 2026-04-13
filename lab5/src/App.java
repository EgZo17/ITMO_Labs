package com.labwork;

import java.io.IOException;
import javax.xml.bind.JAXBException;
import com.labwork.utils.Client;

public class App {
    public static void main(String[] args) throws JAXBException, IOException {
        Client client = new Client();
        client.run();
    }
}
