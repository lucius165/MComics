package vn.edu.poly.mcomics.object.variable;

/**
 * Created by lucius on 04/12/2016.
 */

public class FaceBookComment {
    private String time;
    private String userId;
    private String userName;
    private String userMessage;
    private String messageId;

    public FaceBookComment(String time, String userId, String userName, String userMessage, String messageId) {
        this.time = time;
        this.userId = userId;
        this.userName = userName;
        this.userMessage = userMessage;
        this.messageId = messageId;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserMessage() {
        return userMessage;
    }

    public void setUserMessage(String userMessage) {
        this.userMessage = userMessage;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }
}
