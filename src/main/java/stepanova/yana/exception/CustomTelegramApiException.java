package stepanova.yana.exception;

public class CustomTelegramApiException extends RuntimeException {
    public CustomTelegramApiException(String message) {
        super(message);
    }
}
