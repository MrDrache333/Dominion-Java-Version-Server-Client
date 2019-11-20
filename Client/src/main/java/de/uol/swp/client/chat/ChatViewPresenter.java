package de.uol.swp.client.chat;

import de.uol.swp.client.AbstractPresenter;
import de.uol.swp.common.chat.ChatMessage;
import de.uol.swp.common.chat.ChatService;
import de.uol.swp.common.chat.message.NewChatMessage;
import de.uol.swp.common.user.User;
import de.uol.swp.common.user.UserService;
import de.uol.swp.server.chat.Chat;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * The type Chat view presenter.
 */
public class ChatViewPresenter extends AbstractPresenter {
    /**
     * The constant fxml.
     */
    public static final String fxml = "/fxml/ChatView.fxml";

    private static final Logger LOG = LogManager.getLogger(ChatViewPresenter.class);

    @FXML
    private TextField chatTextField;
    @FXML
    private ListView messageView;

    //Liste mit formatierten Chatnachrichten
    private static ObservableList<HBox> chatMessages;

    //Services
    private static ChatService chatService;
    private static UserService userService;
    private static Chat chat;

    //Erhalten der Chat-ID
    String chatID = chat.getChatId();

    /**
     * Instantiates a new Chat view presenter.
     */
    public ChatViewPresenter(){}

    //--------------------------------------
    // FXML
    //--------------------------------------

    /**
     * On send chat button pressed.
     */
    @FXML
    public void onSendChatButtonPressed() {
        String message;

        message = chatTextField.getText();

        if (message != "") {

            ChatMessage newChatMessage = new ChatMessage(loggedInUser, message);

            LOG.debug("Sending message as User: "+loggedInUser.getUsername());
            LOG.debug("new Message to send: "+ message);

            chatTextField.clear();
            chatService.sendMessage(chatID, newChatMessage);
        }
    }

    //--------------------------------------
    // STATIC METHODS
    //--------------------------------------

    /**
     * On new chat message.
     *
     * @param msg the msg
     */
    public static void onNewChatMessage(NewChatMessage msg) {
        Platform.runLater(() -> {
            chatMessages.add(chatMessagetoHBox(msg.getMessage()));
        });
    }

    /**
     * Creates a HBox with Labels from a given ChatMessage
     *
     * @param msg the ChatMessage
     * @return a HBox with Labels
     */
    private static HBox chatMessagetoHBox(ChatMessage msg) {
        String plainMessage = msg.getMessage();
        String[] messagePiece = plainMessage.split(" ");
        String formatedMessage = "";
        int piece = 0;
        pieces:
        for (int i = 0; i <= plainMessage.length() / 40; i++) {
            while (formatedMessage.length() + messagePiece[piece].length() <= 40 * (i + 1) + i * 6) {
                formatedMessage += messagePiece[piece];
                piece++;
                if (piece == messagePiece.length) break pieces;
                formatedMessage += " ";
            }
            formatedMessage += " \n  ";
        }

        Label sender = new Label(msg.getSender().getUsername());
        Label message = new Label("  " + formatedMessage + "  ");
        sender.setStyle("-fx-text-fill: dimgrey; -fx-font-size: 10");
        message.setStyle("-fx-background-radius: " + (formatedMessage.contains("\n") ? "10" : "90") + ";-fx-background-color: dodgerblue;-fx-text-fill: white; -fx-font-size: 13");

        HBox box = new HBox();
        if (msg.getSender().getUsername().equals(loggedInUser.getUsername())) {
            sender.setText("Du");
            sender.setAlignment(Pos.BOTTOM_RIGHT);
            message.setAlignment(Pos.TOP_RIGHT);
            box.getChildren().add(message);
            box.getChildren().add(sender);
            box.alignmentProperty().setValue(Pos.BOTTOM_RIGHT);

        } else {
            sender.setAlignment(Pos.BOTTOM_LEFT);
            message.setAlignment(Pos.TOP_LEFT);
            box.getChildren().add(sender);
            box.getChildren().add(message);
            box.alignmentProperty().setValue(Pos.BOTTOM_LEFT);
        }
        box.setSpacing(5);
        return box;
    }

    /**
     * Update chat.
     *
     * @param chatMessageList the chat message list
     */
    public static void updateChat(List<ChatMessage> chatMessageList) {
        Platform.runLater(() -> {
            chatMessageList.forEach(msg -> chatMessages.add(chatMessagetoHBox(msg)));
        });
    }

    //--------------------------------------
    // METHODS
    //--------------------------------------

    /**
     * Setlogged in user.
     *
     * @param user the user
     */
    public static void setloggedInUser(User user) {
        loggedInUser = user;
    }

    /**
     * Sets new userService.
     *
     * @param newUserService New value of userService.
     */
    public static void setUserService(UserService newUserService) {
        userService = newUserService;
    }


    //--------------------------------------
    // GETTER UND SETTER
    //--------------------------------------

    /**
     * Sets new chatService.
     *
     * @param newChatService New value of chatService.
     */
    public static void setChatService(ChatService newChatService) {
        chatService = newChatService;
    }

    /**
     * Sets logged in user.
     *
     * @param user the user
     */
    public static void setLoggedInUser(User user) {
        loggedInUser = user;
    }

    /**
     * Initialize.
     */
    @FXML
    public void initialize() {
        updateChatMessages(new ArrayList<>());
        messageView.setItems(chatMessages);
    }

    private void updateChatMessages(List<ChatMessage> chatMessageList) {
        // Attention: This must be done on the FX Thread!
        Platform.runLater(() -> {
            if (chatMessages == null) {
                chatMessages = FXCollections.observableArrayList();
                messageView.setItems(chatMessages);
            }
            chatMessages.clear();
            chatMessageList.forEach(msg -> chatMessages.add(chatMessagetoHBox(msg)));
        });
    }


}
