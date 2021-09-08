package gov.gtas.job.wrapper;
import gov.gtas.summary.MessageSummaryList;
import org.springframework.messaging.Message;

public class MessageWrapper {
    Message<?> message;
    String fileName;
    Boolean fromMessageInfo = false;

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

    public Boolean getFromMessageInfo() {
        return fromMessageInfo;
    }

    public void setFromMessageInfo(Boolean fromMessageInfo) {
        this.fromMessageInfo = fromMessageInfo;
    }
}
