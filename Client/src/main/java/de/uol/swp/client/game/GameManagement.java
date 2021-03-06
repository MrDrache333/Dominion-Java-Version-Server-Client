package de.uol.swp.client.game;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Injector;
import de.uol.swp.client.AbstractPresenter;
import de.uol.swp.client.ClientApp;
import de.uol.swp.client.chat.ChatService;
import de.uol.swp.client.chat.ChatViewPresenter;
import de.uol.swp.client.lobby.LobbyPresenter;
import de.uol.swp.client.lobby.LobbyService;
import de.uol.swp.client.main.PrimaryPresenter;
import de.uol.swp.client.sound.SoundMediaPlayer;
import de.uol.swp.common.game.messages.GameOverMessage;
import de.uol.swp.common.game.messages.UserGaveUpMessage;
import de.uol.swp.common.lobby.message.UserLeftAllLobbiesMessage;
import de.uol.swp.common.lobby.message.UserLeftLobbyMessage;
import de.uol.swp.common.user.User;
import de.uol.swp.common.user.UserDTO;
import de.uol.swp.common.user.UserService;
import de.uol.swp.common.user.message.UpdatedUserMessage;
import de.uol.swp.common.user.message.UserDroppedMessage;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Das GameManagement
 *
 * @author Keno O.
 * @since Sprint 3
 */
@SuppressWarnings("UnstableApiUsage, unused")
public class GameManagement {

    static final Logger LOG = LogManager.getLogger(GameManagement.class);
    static final String styleSheet = "css/global.css";

    private final LobbyPresenter lobbyPresenter;
    private final GameViewPresenter gameViewPresenter;
    private final ChatViewPresenter chatViewPresenter;
    private final UUID id;    //Die Lobby, Chat and GameID
    private User loggedInUser;  //Der aktuell angemeldete Benutzer
    private final UserDTO gameOwner;
    private final String lobbyName;

    private Pane gamePane;
    private Pane lobbyPane;
    private Scene gameOverScene;

    private final Tab primaryTab;
    private final PrimaryPresenter primaryPresenter;

    private final Injector injector;
    private final EventBus eventBus;

    private final Stage gameOverStage;

    private final GameService gameService;


    /**
     * Instanziiert ein GameManagement. Dafür werden nötige Controller initialisiert und auf dem Eventbus registriert
     *
     * @param eventBus     der Eventbus
     * @param id           die ID der Lobby, des Spiels und des internen Chats
     * @param lobbyName    der Lobbyname
     * @param loggedInUser der aktuelle Benutzer
     * @param chatService  der ChatService
     * @param lobbyService der LobbyService
     * @param userService  der UserService
     * @param injector     der Injector
     * @author Keno O., Darian
     * @since Sprint 3
     */
    public GameManagement(EventBus eventBus, UUID id, String lobbyName, User loggedInUser, ChatService chatService, LobbyService lobbyService, UserService userService, Injector injector, UserDTO gameOwner, GameService gameService, PrimaryPresenter primaryPresenter) {
        this.id = id;
        this.loggedInUser = loggedInUser;
        this.injector = injector;
        this.primaryTab = new Tab();
        this.gameOverStage = new Stage();
        this.lobbyName = lobbyName;
        this.eventBus = eventBus;
        this.gameOwner = gameOwner;
        this.gameService = gameService;
        this.primaryPresenter = primaryPresenter;

        this.chatViewPresenter = new ChatViewPresenter(lobbyName, id, loggedInUser, ChatViewPresenter.THEME.Light, chatService, injector, this);
        this.gameViewPresenter = new GameViewPresenter(loggedInUser, id, chatService, chatViewPresenter, lobbyService, userService, injector, this, gameService);
        this.lobbyPresenter = new LobbyPresenter(loggedInUser, lobbyName, id, chatService, chatViewPresenter, lobbyService, userService, injector, gameOwner, this, eventBus);

        this.primaryTab.setId(id.toString());

        eventBus.register(chatViewPresenter);
        eventBus.register(lobbyPresenter);
    }

    /**
     * Methode fängt Servernachricht ab und sofern, die Anfrage des Users erfolgreich war, wird das Gamefenster geschlossen.
     *
     * @param msg enthält die Informationen die benötigt werden um das Gamefenster zu schließen.
     * @author Haschem, Ferit
     * @since Sprint 5
     */
    @Subscribe
    private void userGaveUp(UserGaveUpMessage msg) {
        if (msg.getLobbyID().equals(id) && msg.getUserGivedUp() && msg.getTheUser().equals(loggedInUser)) {
            primaryPresenter.closeTab(msg.getLobbyID(), true);
            LOG.debug("Game mit folgender ID geschlossen: " + id);
        } else {
            // TODO: Fehlerbehandlung später implementieren.
        }
    }

    @Subscribe
    private void userLeftLobby(UserLeftLobbyMessage msg) {
        if (msg.getUser().getUsername().equals(loggedInUser.getUsername()) && msg.getLobbyID().equals(id)) {
            primaryPresenter.closeTab(id, true);
        }
    }

    @Subscribe
    private void userLeftAllLobbies(UserLeftAllLobbiesMessage msg) {
        if (msg.getUser().getUsername().equals(loggedInUser.getUsername())) {
            primaryPresenter.closeAllTabs();
        }
    }

    @Subscribe
    private void userDroppedAccount(UserDroppedMessage msg) {
        if (msg.getUser().getUsername().equals(loggedInUser.getUsername())) {
            primaryPresenter.closeAllTabs();
        }
    }
/*
    @Subscribe
    private void lobbyCreated(CreateLobbyMessage msg) {
        if (msg.getUser.getUsername().equals(loggedInUser.getUsername())) {
            primaryPresenter.showTab(msg.getChatID());
        }
    }
*/

    /**
     * Aktualisiert den loggedInUser, wenn dieser seine Daten geändert hat
     *
     * @param message die UpdatedUserMessage
     * @author Julia
     * @since Sprint 4
     */
    @Subscribe
    public void updatedUser(UpdatedUserMessage message) {
        if (loggedInUser.getUsername().equals(message.getOldUser().getUsername())) {
            loggedInUser = message.getUser();
        }
    }

    /**
     * Ruft die showGameOverView-Methode auf, wenn das Spiel vorbei ist.
     *
     * @param message die GameOverMessage
     * @author Anna
     * @since Sprint 6
     */
    @Subscribe
    public void onGameOverMessage(GameOverMessage message) {
        if (message.getGameID().equals(id)) {
            showGameOverView(loggedInUser, message.getWinners(), message.getResults());
        }
    }

    /**
     * Überprüft ob sich die aktuelle Stage im Vordergrund befindet
     *
     * @return true wenn sie im Vordergrund ist, sonst false
     * @author Keno O.
     * @since Sprint 3
     */
    public boolean hasFocus() {
        return primaryPresenter.getFocusedTab().equals(id.toString());
    }

    /**
     * Methode zum Anzeigen der LobbyView
     *
     * @author Keno O.
     * @since Sprint 3
     */
    public void showLobbyView() {
        initLobbyView();
        showView(lobbyPane, lobbyName);
    }

    /**
     * Methode zum Anzeigen der GameView
     *
     * @author Keno O.
     * @since Sprint 3
     */
    public void showGameView() {
        initGameView();
        eventBus.register(gameViewPresenter);
        showView(gamePane, lobbyName);
        gameViewPresenter.getInGameUserList(this.id);
        gameViewPresenter.setUsableMoney(0);
    }

    /**
     * Methode zum Anzeigen der GameOverView.
     * Status der Spieler wird auf "nicht bereit" gesetzt.
     *
     * @param loggedInUser der aktuelle Nutzer
     * @param winners      der/die Gewinner des Spiels
     * @author Anna
     * @since Sprint 6
     */
    public void showGameOverView(User loggedInUser, List<String> winners, Map<String, Integer> res) {
        if (loggedInUser.getUsername().equals(this.loggedInUser.getUsername())) {
            UserDTO loggedInUserDTO = new UserDTO(loggedInUser.getUsername(), loggedInUser.getPassword(), loggedInUser.getEMail());
            lobbyPresenter.getLobbyService().setLobbyUserStatus(id, loggedInUserDTO, false);
            lobbyPresenter.setButtonReady(loggedInUserDTO);
            initGameOverView(this.loggedInUser, winners, res);
            Platform.runLater(() -> {
                gameOverStage.setScene(gameOverScene);
                gameOverStage.setTitle("Spielergebnis");
                gameOverStage.setResizable(false);
                Stage primaryStage = ClientApp.getSceneManager().getPrimaryStage();
                gameOverStage.setX(primaryStage.getX() + primaryStage.getWidth() / 2 - gameOverStage.getScene().getWidth() / 2);
                gameOverStage.setY(primaryStage.getY() + primaryStage.getHeight() / 2 - gameOverStage.getScene().getHeight() / 2);
                gameOverStage.show();
                gameOverStage.toFront();
                gameOverStage.setOnCloseRequest(windowEvent -> closeGameOverViewAndLeaveLobby());
            });
        }
    }

    /**
     * Methode zum Schließen der GameOverStage.
     *
     * @author Anna
     * @since Sprint 6
     */
    public void closeGameOverView() {
        Platform.runLater(gameOverStage::close);
    }

    /**
     * Methode zum Schließen der GameOverStage und verlassen der Lobby
     *
     * @author Anna
     * @since Sprint 6
     */
    public void closeGameOverViewAndLeaveLobby() {
        Platform.runLater(gameOverStage::close);
        primaryPresenter.closeTab(id, true);
    }

    /**
     * Initialisieren der GameView
     *
     * @author Keno O., Fenja
     * @since Sprint 7
     */
    private void initGameView() {
        if (gamePane == null) {
            gamePane = initPresenter(gameViewPresenter, GameViewPresenter.fxml);
            gamePane.getStylesheets().add(styleSheet);
        }
    }

    /**
     * LobbyView wird initialisiert und deklariert.
     * Neue Szene für die neue Lobby wird erstellt und gespeichert
     *
     * @author Keno O., Fenja
     * @since Sprint 7
     */
    private void initLobbyView() {
        if (lobbyPane == null) {
            lobbyPane = initPresenter(lobbyPresenter, LobbyPresenter.fxml);
            lobbyPane.getStylesheets().add(styleSheet);
        }
    }

    /**
     * GameOverView wird initialisiert und deklariert.
     * Neue Szene für das Fenster mit dem Spielergebnis wird erstellt und gespeichert
     *
     * @param user    der User, dem das Fenster angezeigt wird
     * @param winners der/die Gewinner des Spiels
     * @author Anna
     * @since Sprint 6
     */
    private void initGameOverView(User user, List<String> winners, Map<String, Integer> res) {
        Parent rootPane = initPresenter(new GameOverViewPresenter(this, user, winners, res), GameOverViewPresenter.fxml);
        gameOverScene = new Scene(rootPane, 420, 280);
        gameOverScene.getStylesheets().add(styleSheet);
    }

    /**
     * initPresenter für Lobbies, setzt den jeweiligen lobbyPresenter als Controller
     *
     * @param presenter der Presenter
     * @param fxml      die zum Presenter gehörige fxml
     * @return die rootPane
     * @author Keno O., Fenja
     * @since Sprint 7
     */
    private Pane initPresenter(AbstractPresenter presenter, String fxml) {
        Pane rootPane;
        FXMLLoader loader = injector.getInstance(FXMLLoader.class);
        try {
            URL url = getClass().getResource(fxml);
            LOG.debug("Lade " + url);
            loader.setLocation(url);
            loader.setController(presenter);
            rootPane = loader.load();
        } catch (Exception e) {
            throw new RuntimeException("View konnte nicht geladen werden!" + e.getMessage(), e);
        }
        return rootPane;
    }

    /**
     * Setzt die Szene der primaryStage auf die übergebene Szene und aktualisiert den Titel der Stage
     *
     * @param pane die Szene
     * @param title der Titel der Stage
     * @author Julia, Keno O., Marvin, Fenja
     * @since Sprint 7
     */
    private void showView(final Pane pane, final String title) {
        Platform.runLater(() -> {
            primaryTab.setText(title + (pane == gamePane ? "-Spiel" : "-Lobby"));
            primaryTab.setContent(pane);
            new SoundMediaPlayer(SoundMediaPlayer.Sound.Window_Opened, SoundMediaPlayer.Type.Sound).play();
        });
    }

    public GameService getGameService() {
        return gameService;
    }

    /**
     * Getter für die ID des Spiels bzw. der Lobby.
     *
     * @return die ID
     * @author Anna
     * @since Sprint 6
     */
    public UUID getID() {
        return this.id;
    }

    /**
     * Getter für den LobbyService.
     *
     * @return der LobbyService
     * @author Anna
     * @since Sprint 6
     */
    public LobbyService getLobbyService() {
        return this.lobbyPresenter.getLobbyService();
    }

    public Tab getPrimaryTab() {
        return primaryTab;
    }

    /**
     * Gets the Lobby Name.
     *
     * @return lobbyName der Lobbyname
     * @author Keno Oelrichs Garcia
     * @since Sprint 4
     */
    public String getLobbyName() {
        return lobbyName;
    }

    /**
     * Gibt den eingeloggten User zurück
     *
     * @return loggedInUser der eingloggte User
     * @author Marvin, Timo
     * @since Sprint 7
     */
    public User getLoggedInUser() {
        return loggedInUser;
    }
}
