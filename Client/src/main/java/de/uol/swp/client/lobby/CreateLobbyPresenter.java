package de.uol.swp.client.lobby;

import com.google.common.eventbus.EventBus;
import de.uol.swp.client.AbstractPresenter;
import de.uol.swp.client.SceneManager;
import de.uol.swp.client.main.MainMenuPresenter;
import de.uol.swp.common.lobby.request.CreateLobbyRequest;
import de.uol.swp.common.user.User;
import de.uol.swp.common.user.UserDTO;
import de.uol.swp.common.user.UserService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.regex.Pattern;

public class CreateLobbyPresenter extends AbstractPresenter {

    /**
     * Die FXML Konstante.
     */
    public static final String fxml = "/fxml/CreateLobbyView.fxml";
    public static final String css = "css/CreateLobbyView.css";
    private static final Logger LOG = LogManager.getLogger(CreateLobbyPresenter.class);

    private User loggedInUser;
    private LobbyService lobbyService;
    private MainMenuPresenter mainMenuPresenter;
    private UserService userService;
    private EventBus eventBus;
    

    @FXML
    private Button cancelButton;
    @FXML
    private Button createButton;
    @FXML
    private TextField lobbynameField;
    @FXML
    private PasswordField passwordField;


    public CreateLobbyPresenter(User loggedInUser, LobbyService lobbyService, UserService userService, EventBus eventBus) {
        this.loggedInUser = loggedInUser;
        this.lobbyService = lobbyService;
        this.userService = userService;
        this.eventBus = eventBus;
    }


    /**
     * Sobald der Lobby erstellen Button gedrückt wird, öffnet sich ein Dialog. Hier wird man aufgefordert einen Namen für die Lobby anzugeben. Das Passwortfeld ist optional
     * auszufüllen. Bleibt das Passwortfeld leer, wird die Lobby offen. Wird ein Passwort angegegben, wird dieses gespeicherrt und die Lobby wird privat
     *
     * @param actionEvent
     * @author Paula
     * @since Sprint7
     */

    @FXML
    public void onCreateLobbyButtonPressed(ActionEvent actionEvent) {
        String lobbyName = lobbynameField.getText();
        if (Pattern.matches("([a-zA-Z]|[0-9])+(([a-zA-Z]|[0-9])+([a-zA-Z]|[0-9]| )*([a-zA-Z]|[0-9])+)*", lobbyName)) {
            CreateLobbyRequest msg = new CreateLobbyRequest(lobbyName, new UserDTO(loggedInUser.getUsername(), loggedInUser.getPassword(), loggedInUser.getEMail()), "");
            eventBus.post(msg);
            LOG.info("Request wurde gesendet.");
        } else {
            SceneManager.showAlert(Alert.AlertType.WARNING, "Bitte geben Sie einen gültigen Lobby Namen ein!\n\nDieser darf aus Buchstaben, Zahlen und Leerzeichen bestehen, aber nicht mit einem Leerzeichen beginnen oder enden", "Fehler");
            //lobbyName.requestFocus();
        }
        lobbynameField.clear();
    }

    /**
     *
     *
     * @param actionEvent
     * @author Paula
     * @since Sprint7
     */
    @FXML
    public void onCancelButtonPressed(ActionEvent actionEvent) {
        clearAll();

    }



    /**
     * Leert alle Felder (Benutzername, E-Mail und alle Passwortfelder)
     *
     * @author Paula
     * @since Sprint7
     */
    private void clearAll() {
        lobbynameField.clear();
        passwordField.clear();

    }


    }


