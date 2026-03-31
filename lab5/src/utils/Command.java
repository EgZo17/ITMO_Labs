package utils;

public interface Command {
    void execute(String[] parameters);

    default boolean validate(String[] parameters) {
        if (parameters.length != 0) {
            System.out.println("Error: Invalid command signature. Enter \"help\" to see more.");
            return false;
        }
        return true;
    }
}
