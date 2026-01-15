package exceptions;

public class HuntingException extends Exception {
    
    public HuntingException(String message) {
        super(message);
    }
    
    @Override
    public String getMessage() {
        return "Ошибка охоты: " + super.getMessage();
    }
    
    @Override
    public String toString() {
        return "HuntingException: " + getMessage();
    }
}
