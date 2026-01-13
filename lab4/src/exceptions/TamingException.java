package exceptions;

public class TamingException extends Exception {

    public TamingException(String message) {
        super(message);
    }
    
    @Override
    public String getMessage() {
        return "Ошибка приручения: " + super.getMessage();
    }
    
    @Override
    public String toString() {
        return "TamingException: " + getMessage();
    }
}
