package exception;

public class TaskOverlapEception extends IllegalArgumentException {
    public TaskOverlapEception(String message) {
        super(message);
    }
}