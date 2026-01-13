package exceptions;

public class TasksUndoneException extends RuntimeException {
    
    public TasksUndoneException(String message) {
        super(message);
    }

}
