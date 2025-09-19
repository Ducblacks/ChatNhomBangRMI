package ChatRMI;

import java.io.Serializable;
import java.util.Date;

public class Message implements Serializable {
    private String sender;
    private String content;
    private Date timestamp;
    private MessageType type;

    public Message(String sender, String content, MessageType type) {
        this.sender = sender;
        this.content = content;
        this.timestamp = new Date();
        this.type = type;
    }

    public String getSender() { return sender; }
    public String getContent() { return content; }
    public Date getTimestamp() { return timestamp; }
    public MessageType getType() { return type; }

    @Override
    public String toString() {
        return "[" + timestamp + "] " + sender + ": " + content;
    }
}

enum MessageType {
    TEXT, JOIN, LEAVE, SYSTEM
}