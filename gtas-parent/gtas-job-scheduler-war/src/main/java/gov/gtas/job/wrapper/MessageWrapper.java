package gov.gtas.job.wrapper;
import org.springframework.messaging.Message;

public class MessageWrapper {
    Message<?> message;
    String fileName;

    public MessageWrapper(Message<?> message, String fileName){
        this.message = message;
        this.fileName = fileName;
    }

    public Message<?> getMessage() {
        return message;
    }

    public void setMessage(Message<?> message) {
        this.message = message;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}
