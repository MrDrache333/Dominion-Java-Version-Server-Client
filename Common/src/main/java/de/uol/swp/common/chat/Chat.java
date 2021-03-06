package de.uol.swp.common.chat;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * The interface Chat.
 */
public interface Chat extends Serializable {

    /**
     * Gets Messages.
     *
     * @return Value of Messages.
     */
    ArrayList<ChatMessage> getMessages();

    /**
     * Sets new Messages.
     *
     * @param Messages New value of Messages.
     */
    void setMessages(ArrayList<ChatMessage> Messages);

    /**
     * Gets ChatId.
     *
     * @return Value of ChatId.
     */
    String getChatId();
}
