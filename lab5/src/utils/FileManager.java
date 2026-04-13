package com.labwork.utils;

import com.labwork.collection.LabCollection;
import javax.xml.bind.*;
import java.io.*;


public class FileManager {
    private static String fileName;
    
    private FileManager() {}

    public static void initialize() {
        fileName = System.getenv("LAB_COLLECTION");
        if (fileName == null || fileName.trim().isEmpty()) {
            fileName = "LabCollection.xml";
            System.out.println("Environment variable 'LAB_COLLECTION' was not set. Using default file: " + fileName);
            isFileAccessible(fileName);
        } else if (!isFileAccessible(fileName)) {
            fileName = "LabCollection.xml";
            System.out.println("Environment variable 'LAB_COLLECTION' set but is not available. Using default file: " + fileName);
        } else {
            System.out.println("Using file from environment: " + fileName);
        }
    }

    private static boolean isFileAccessible(String fileName) {        
        File file = new File(fileName);
        
        if (file.exists() && file.isDirectory()) {
            System.err.println("Error: The path leads to a directory, not a file: " + fileName);
            return false;
        }
        
        File parentDir = file.getParentFile();
        if (parentDir != null && !parentDir.exists()) {
            if (!parentDir.mkdirs()) {
                System.out.println("Error: Failed to create a directory " + parentDir.getPath());
                return false;
            }
            System.out.println("A directory has been created: " + parentDir.getPath());
        }
        
        if (file.exists() && !file.canRead()) {
            System.out.println("Error: there are no permissions to read the file " + fileName);
            return false;
        }
        
        if (file.exists() && !file.canWrite()) {
            System.out.println("Error: there are no permission to write to the file " + fileName);
            return false;
        }
        
        if (!file.exists()) {
            try {
                boolean created = file.createNewFile();
                if (created) {
                    System.out.println("File has been created: " + fileName);
                }
                return created;
            } catch (IOException e) {
                System.out.println("Error: unable to create a file " + fileName + " - " + e.getMessage());
                return false;
            }
        }

        return true;
    }
    
    public static void saveCollection() throws JAXBException, IOException {
        LabCollection collection = LabCollection.getInstance();
        
        JAXBContext context = JAXBContext.newInstance(LabCollection.class);
        Marshaller marshaller = context.createMarshaller();
        
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
        
        try (FileWriter writer = new FileWriter(fileName)) {
            marshaller.marshal(collection, writer);
            System.out.println("The collection is saved to: " + fileName);
        } catch (IOException e) {
            System.out.println("Error when writing to a file: " + e.getMessage());
        }
    }
    
    public static void loadCollection() throws JAXBException, IOException {
        File file = new File(fileName);
        
        if (!file.exists()) {
            System.out.println("The file is not found: " + fileName);
            return;
        }
        
        if (file.length() == 0) {
            System.out.println("The file is empty: " + fileName);
            return;
        }
        
        StringBuilder xmlContent = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                xmlContent.append(line).append("\n");
            }
        } catch (IOException e) {
            System.out.println("Error when writing to a file: " + e.getMessage());
        }
        
        try {
            JAXBContext context = JAXBContext.newInstance(LabCollection.class);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            
            StringReader stringReader = new StringReader(xmlContent.toString());
            LabCollection loadedCollection = (LabCollection) unmarshaller.unmarshal(stringReader);
            
            LabCollection instance = LabCollection.getInstance();
            instance.setCollection(loadedCollection.getCollection());
            instance.setInitializationDate(loadedCollection.getInitializationDate());
            
            System.out.println("The collection was uploaded from: " + fileName);
            System.out.println("Uploaded elements from the collection: " + instance.getLength());
            
        } catch (JAXBException e) {
            System.out.println("Parsing XML error: " + e.getMessage());
        }
    }
}
