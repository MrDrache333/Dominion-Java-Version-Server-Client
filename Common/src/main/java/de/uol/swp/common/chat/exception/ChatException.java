package de.uol.swp.common.chat.exception;

public class ChatException extends RuntimeException {

    private static final long serialVersionUID = 8266067722961836792L;

    /**
     * Definiert eine ChatException, die einen String bekommt und diese als Nachricht an RuntimeException weitergibt.
     *
     * @param message
     * @author Keno O.
     * @since Sprint2
     */
    public ChatException(String message) {
        super(message);
    }
}
