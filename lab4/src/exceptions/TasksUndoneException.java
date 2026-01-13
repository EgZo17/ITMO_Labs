package exceptions;

public class TasksUndoneException extends RuntimeException {
    
    public TasksUndoneException(String message) {
        super(message);
    }

    @Override
    public String getMessage() {
        return "Ошибка выполнения целей: " + super.getMessage();
    }
    
    @Override
    public String toString() {
        return "TasksUndoneException: " + getMessage();
    }
}
