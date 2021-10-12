package watch;

import protostream.com.google.gson.annotations.SerializedName;

public class TelegramMessage {
    @SerializedName("chat_id")
    private String chatId;
    private String text;
    public TelegramMessage() { }
    public TelegramMessage(String chatId, String text)
    { this.chatId = chatId; this.text = text; }
    public String getChatId() { return chatId; }
    public void setChatId(String chatId) { this.chatId = chatId; }
    public String getText() { return text; }
    public void setText(String text)
    { this.text = text; }


}
