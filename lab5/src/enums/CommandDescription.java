package enums;

public enum CommandDescription {
    HELP("", "Display the help for the available commands."),
    INFO("", "Print information about the collection."),
    SHOW("", "Print all elements of the collection in string representation."),
    ADD("", "Add a new element to the collection."),
    UPDATE("<(int) id>", "Update the value of a collection element whose id is equal to the specified value."),
    REMOVE_BY_ID("<(int) id>", "Remove an element from the collection by its id."),
    CLEAR("", "Clear the collection."),
    SAVE("", "Save the collection to a file."),
    EXECUTE_SCRIPT("<(String) file_name>", "Read and execute the script from the specified file."),
    EXIT("", "Exit the program (without saving to a file)."),
    REMOVE_AT("<(int) index>", "Delete an element at a specified position in the collection (index)."),
    ADD_IF_MIN("", "Add a new element to the collection if its value is less than the value of the smallest element in the collection."),
    REMOVE_LOWER("", "Remove all elements from the collection that are smaller than the specified element."),
    REMOVE_ANY_BY_AUTHOR("<(String) author>", "Remove one element from the collection whose author field value is equivalent to the specified value."),
    FILTER_BY_DIFFICULTY("<(String) difficulty>", "Display elements whose difficulty field value is equal to the specified value."),
    PRINT_FIELD_DESCENDING_AUTHOR("", "Display the author field values of all elements in descending order.");

    private final String signature;
    private final String description;
    
    CommandDescription(String signature, String description) {
        this.signature = signature;
        this.description = description;
    }
    
    public String getDescription() {
        return description;
    }

    public String getSignature() {
        return signature;
    }
}
