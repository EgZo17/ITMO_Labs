package exceptions;

public class StarvationDeathException extends RuntimeException {
    
    public StarvationDeathException(String message) {
        super(message);
    }

    @Override
    public String getMessage() {
        return "Ошибка смерти: " + super.getMessage();
    }
    
    @Override
    public String toString() {
        return "StarvationDeathException: " + getMessage();
    }
}
