package de.uol.swp.server.lobby;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import de.uol.swp.common.lobby.Lobby;
import de.uol.swp.common.lobby.LobbyUser;
import de.uol.swp.common.lobby.dto.LobbyDTO;
import de.uol.swp.common.lobby.message.*;
import de.uol.swp.common.lobby.request.*;
import de.uol.swp.common.lobby.response.AllOnlineLobbiesResponse;
import de.uol.swp.common.lobby.response.AllOnlineUsersInLobbyResponse;
import de.uol.swp.common.message.ResponseMessage;
import de.uol.swp.common.message.ServerMessage;
import de.uol.swp.common.user.User;
import de.uol.swp.common.user.UserDTO;
import de.uol.swp.server.AbstractService;
import de.uol.swp.server.chat.ChatManagement;
import de.uol.swp.server.message.StartGameInternalMessage;
import de.uol.swp.server.usermanagement.AuthenticationService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Die Klasse LobbyService, welche eine Lobby erstellt
 *
 * @author KenoO
 * @since Sprint2
 */
public class LobbyService extends AbstractService {
    private static final Logger LOG = LogManager.getLogger(LobbyService.class);

    private final LobbyManagement lobbyManagement;
    private final ChatManagement chatManagement;
    private final AuthenticationService authenticationService;

    /**
     * Instanziiert einen neuen Lobby Service
     *
     * @param lobbyManagement       das Lobby management
     * @param authenticationService den Authetifizierenden Service
     * @param chatManagement        den Chat Mannagement
     * @param eventBus              den Eventbus
     * @author KenoO
     * @since Sprint 2
     */
    @Inject
    public LobbyService(LobbyManagement lobbyManagement, AuthenticationService authenticationService, ChatManagement chatManagement, EventBus eventBus) {
        super(eventBus);
        this.lobbyManagement = lobbyManagement;
        this.authenticationService = authenticationService;
        this.chatManagement = chatManagement;
    }

    //--------------------------------------
    // EVENTBUS
    //--------------------------------------

    /**
     * LobbyManagement auf dem Server wird aufgerufen und übergibt LobbyNamen, LobbyPassword und den Besitzer.
     * Wenn dies erfolgt ist, folgt eine returnMessage an den Client die LobbyView anzuzeigen.
     *
     * @param msg enthält die Message vom Client mit den benötigten Daten, um die Lobby zu erstellen.
     * @author Paula, Haschem, Ferit, Rike, Marvin
     * @version 0.1
     * @since Sprint2
     */
    @Subscribe
    public void onCreateLobbyRequest(CreateLobbyRequest msg) {
        UUID chatID = lobbyManagement.createLobby(msg.getLobbyName(), msg.getLobbyPassword(), new LobbyUser(msg.getOwner()));
        chatManagement.createChat(chatID.toString());
        LOG.info("Der Chat mir der UUID " + chatID + " wurde erfolgreich erstellt");
        Optional<Lobby> lobby = lobbyManagement.getLobby(chatID);
        ServerMessage returnMessage = new CreateLobbyMessage(msg.getLobbyName(), msg.getLobbyPassword(), msg.getUser(), chatID, (LobbyDTO) lobby.get());
        post(returnMessage);
        LOG.info("onCreateLobbyRequest wird auf dem Server aufgerufen.");
    }

    /**
     * LobbyManagement auf dem Server wird aufgerufen und übergibt den Namen des Nutzers.
     * Wenn dies erfolgt ist, folgt eine UserJoinedLobbyMessage an den Client, um den User zur Lobby hinzuzufügen
     *
     * @param msg the msg
     * @author Paula, Julia, Marvin
     * @since Sprint3
     */
    @Subscribe
    public void onLobbyJoinUserRequest(LobbyJoinUserRequest msg) {
        Optional<Lobby> lobby = lobbyManagement.getLobby(msg.getLobbyID());
        if (lobby.isPresent() && !lobby.get().getUsers().contains(msg.getUser()) && lobby.get().getPlayers() < 4) {
            LOG.info("User " + msg.getUser().getUsername() + " is joining lobby " + lobby.get().getName());
            lobby.get().joinUser(new LobbyUser(msg.getUser()));
            ServerMessage returnMessage = new UserJoinedLobbyMessage(msg.getLobbyID(), msg.getUser(), (UserDTO) lobby.get().getOwner(), (LobbyDTO) lobby.get());
            post(returnMessage);
        } else {
            LOG.error("Joining lobby with ID " + msg.getLobbyID() + " failed");
        }
    }

    /**
     * LobbyManagement wird aufgerufen und übergibt Namen der Lobby und User.
     * UserLeftLobbyMessage wird an Client gesendet
     *
     * @param msg the msg
     * @author Julia, Paula, Darian, Marvin
     * @since Sprint3
     */
    @Subscribe
    public void onLobbyLeaveUserRequest(LobbyLeaveUserRequest msg) {
        User oldOwner = lobbyManagement.getLobbyOwner(msg.getLobbyID());
        //Falls der Besitzer der Lobby aus der Lobby geht wird dieser aktualisiert
        if (lobbyManagement.leaveLobby(msg.getLobbyID(), msg.getUser())) {
            Optional<Lobby> lobby = lobbyManagement.getLobby(msg.getLobbyID());
            LOG.info("User " + msg.getUser().getUsername() + " is leaving lobby " + msg.getLobbyID());
            ServerMessage returnMessage;
            if (lobby.isPresent()) {
                if (!oldOwner.getUsername().equals(lobby.get().getOwner().getUsername())) {
                    lobbyManagement.getLobby(msg.getLobbyID()).get().setReadyStatus(lobby.get().getOwner(), false);
                }
                returnMessage = new UserLeftLobbyMessage(msg.getLobbyID(), msg.getUser(), (UserDTO) lobby.get().getOwner(), (LobbyDTO) lobby.get());
            } else {
                returnMessage = new UserLeftLobbyMessage(msg.getLobbyID(), msg.getUser(), null, null);
            }
            post(returnMessage);

        } else {
            LOG.error("Leaving lobby with ID" + msg.getLobbyID() + " failed");
        }
    }

    /**
     * Lobbys, in denen User drinnen ist, werden verlassen
     *
     * @param msg the msg
     * @author Paula, Julia, Marvin
     * @since Sprint3
     */
    @Subscribe
    public void onLeaveAllLobbiesOnLogoutRequest(LeaveAllLobbiesOnLogoutRequest msg) {
        List<LobbyDTO> toLeave = new ArrayList<>();
        lobbyManagement.getLobbies().forEach(lobby -> {
            List<User> users = new ArrayList<>(lobby.getUsers());
            if (users.contains(msg.getUser())) {
                toLeave.add((LobbyDTO) lobby);
            }
        });
        LOG.info("User " + msg.getUser().getUsername() + " is leaving all lobbies");
        toLeave.forEach(lobby -> lobbyManagement.leaveLobby(lobby.getLobbyID(), msg.getUser()));
        toLeave.clear();
        lobbyManagement.getLobbies().forEach(lobby -> toLeave.add((LobbyDTO) lobby));
        ServerMessage returnMessage = new UserLeftAllLobbiesMessage(msg.getUser(), toLeave);
        post(returnMessage);
    }

    /**
     * Der Status eines Spielers wird abgefragt, ob der geupdatet wurde
     *
     * @param request den geupdateteten Status des Users
     * @author Keno Oelrichs Garcia
     * @since Sprint3
     */
    @Subscribe
    public void onUpdateLobbyReadyStatusRequest(UpdateLobbyReadyStatusRequest request) {
        Optional<Lobby> lobby = lobbyManagement.getLobby(request.getLobbyID());

        if (lobby.isPresent()) {
            lobby.get().setReadyStatus(request.getUser(), request.isReady());
            ServerMessage msg = new UpdatedLobbyReadyStatusMessage(lobby.get().getLobbyID(), request.getUser(), lobby.get().getReadyStatus(request.getUser()));
            sendToAll(request.getLobbyID(), msg);
            LOG.debug("Sending Updated Status of User " + request.getUser().getUsername() + " to " + request.isReady() + " in Lobby: " + lobby.get().getLobbyID());
            allPlayersReady(lobby.get());
        } else
            LOG.debug("LOBBY NOT FOUND! ID: " + request.getLobbyID());
    }

    /**
     * Ruft alle Online User in der Lobby ab
     *
     * @param request die RetrieveAllOnlineUsersInLobbyRequest
     * @author Marvin
     * @since Sprint3
     */
    @Subscribe
    public void onRetrieveAllOnlineUsersInLobbyRequest(RetrieveAllOnlineUsersInLobbyRequest request) {
        Optional<Lobby> lobby = lobbyManagement.getLobby(request.getLobbyID());
        if (lobby.isPresent()) {
            ResponseMessage msg = new AllOnlineUsersInLobbyResponse(lobby.get().getLobbyID(), lobby.get().getUsers(), lobby.get().getEveryReadyStatus());
            msg.initWithMessage(request);
            post(msg);
        } else {
            LOG.debug("LOBBY NOT FOUND! ID: " + request.getLobbyID());
        }
    }

    /**
     * Erstellt eine AllOnlineLobbiesResponse mit allen Lobbies im LobbyManagement und schickt diese ab
     *
     * @param msg the msg
     * @author Julia
     * @since Sprint2
     */
    @Subscribe
    public void onRetrieveAllOnlineLobbiesRequest(RetrieveAllOnlineLobbiesRequest msg) {
        AllOnlineLobbiesResponse response = new AllOnlineLobbiesResponse(lobbyManagement.getLobbies());
        response.initWithMessage(msg);
        post(response);
    }

    /**
     * Lobbies werden aktualisiert nachdem ein User seine Daten geändert hat
     *
     * @param msg
     * @author Julis
     * @since Sprint4
     */
    @Subscribe
    public void onUpdateLobbiesRequest(UpdateLobbiesRequest msg) {
        lobbyManagement.updateLobbies(msg.getUpdatedUser(), msg.getOldUser());
    }


    /**
     * Bei der Anfrage einen Spieler aus einer Lobby zu entfernen, wird dieser entfernt.
     *
     * @param msg the request message
     * @author Darian, Marvin
     */
    @Subscribe
    public void onKickUserRequest(KickUserRequest msg) {
        if (lobbyManagement.kickUser(msg.getLobbyID(), msg.getUserToKick(), msg.getUser())) {
            LOG.info("User " + msg.getUser().getUsername() + " is kicked from lobby with ID" + msg.getLobbyID());
            ServerMessage returnMessage = new KickUserMessage(msg.getLobbyID(), msg.getUserToKick(), (LobbyDTO) lobbyManagement.getLobby(msg.getLobbyID()).get());
            post(returnMessage);
        } else {
            LOG.error("Kicking " + msg.getUserToKick() + " from Lobby with ID " + msg.getLobbyID() + " has failed");
        }
    }

    /**
     * Definiert, was bei einem onSetMaxPlayerRequest passieren soll.
     *
     * @author Timo, Rike, Marvin
     * @since Sprint 3
     */
    @Subscribe
    public void onSetMaxPlayerRequest(SetMaxPlayerRequest msg) {
        boolean setMaxPlayerSet = lobbyManagement.setMaxPlayer(msg.getLobbyID(), msg.getUser(), msg.getMaxPlayerValue());
        LobbyDTO lobby = (LobbyDTO) lobbyManagement.getLobby(msg.getLobbyID()).get();
        SetMaxPlayerMessage returnMessage = new SetMaxPlayerMessage(msg.getMaxPlayerValue(), msg.getLobbyID(), setMaxPlayerSet, lobbyManagement.getLobbyOwner(msg.getLobbyID()), lobby);
        post(returnMessage);

    }

    //--------------------------------------
    // Help Methods
    //--------------------------------------

    /**
     * Hilfsmethode, die die Nachricht an alle Spieler in der Lobby sendet
     *
     * @param lobbyID die LobbyID
     * @param message die Nachricht
     * @author KenoO, Paula, Marvin
     * @since Sprint 2
     */
    public void sendToAll(UUID lobbyID, ServerMessage message) {
        Optional<Lobby> lobby = lobbyManagement.getLobby(lobbyID);
        if (lobby.isPresent()) {
            message.setReceiver(authenticationService.getSessions(lobby.get().getUsers()));
            post(message);
        }
        // TODO: error handling not existing lobby
    }


    /**
     * Überprüft ob alle Spieler bereit sind
     * Spiel startet wenn alle in der Lobby vorhandenen Spieler Bereit sind
     *
     * @param lobby the lobby
     * @author Darian, Keno, Marvin
     * @since Sprint 4
     */
    private void allPlayersReady(Lobby lobby) {
        if (lobby.getPlayers() == 1) return;
        //Prüfen, ob jeder Spieler in der Lobby fertig ist
        for (User user : lobby.getUsers()) {
            if (!lobby.getReadyStatus(user)) return;
        }
        //Lobby starten
        LOG.debug("Game starts in Lobby: " + lobby.getName());
        StartGameMessage msg = new StartGameMessage(lobby.getLobbyID());
        sendToAll(lobby.getLobbyID(), msg);
        // Sendet eine interne-Nachricht, welche die Erstellung des Games initiiert.
        StartGameInternalMessage internalMessage = new StartGameInternalMessage(lobby.getLobbyID());
        post(internalMessage);
    }
}
