package de.uol.swp.server.lobby;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import de.uol.swp.common.lobby.Lobby;
import de.uol.swp.common.lobby.message.*;
import de.uol.swp.common.lobby.request.*;
import de.uol.swp.common.lobby.response.AllLobbyUsersResponse;
import de.uol.swp.common.lobby.response.AllOnlineLobbiesResponse;
import de.uol.swp.common.lobby.response.AllOnlineUsersInLobbyResponse;
import de.uol.swp.common.message.ResponseMessage;
import de.uol.swp.common.message.ServerMessage;
import de.uol.swp.common.user.User;
import de.uol.swp.server.AbstractService;
import de.uol.swp.server.chat.ChatManagement;
import de.uol.swp.server.usermanagement.AuthenticationService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Optional;
import java.util.UUID;

/**
 * The type Lobby service.
 */
public class LobbyService extends AbstractService {
    private static final Logger LOG = LogManager.getLogger(LobbyService.class);

    private final LobbyManagement lobbyManagement;
    private final ChatManagement chatManagement;
    private final AuthenticationService authenticationService;

    /**
     * Instantiates a new Lobby service.
     *
     * @param lobbyManagement       the lobby management
     * @param authenticationService the authentication service
     * @param chatManagement        the chat management
     * @param eventBus              the event bus
     */
    @Inject
    public LobbyService(LobbyManagement lobbyManagement, AuthenticationService authenticationService, ChatManagement chatManagement, EventBus eventBus) {
        super(eventBus);
        this.lobbyManagement = lobbyManagement;
        this.authenticationService = authenticationService;
        this.chatManagement = chatManagement;
    }

    /**
     * lobbyManagment auf dem Server wird aufgerufen und übergibt LobbyNamen und den Besitzer.
     * Wenn dies erfolgt ist, folgt eine returnMessage an den Client die LobbyView anzuzeigen.
     *
     * @param msg enthält die Message vom Client mit den benötigten Daten um die Lobby zu erstellen.
     * @author Paula, Haschem, Ferit
     * @version 0.1
     * @since Sprint2
     */
    @Subscribe
    public void onCreateLobbyRequest(CreateLobbyRequest msg) {

        UUID chatID = lobbyManagement.createLobby(msg.getLobbyName(), msg.getOwner());

        chatManagement.createChat(chatID.toString());
        LOG.info("Der Chat mir der UUID " + chatID + " wurde erfolgreich erstellt");

        ServerMessage returnMessage = new CreateLobbyMessage(msg.getLobbyName(), msg.getUser(), chatID);
        post(returnMessage);
        LOG.info("onCreateLobbyRequest wird auf dem Server aufgerufen.");
    }

    /**
     * On lobby join user request.
     *
     * @param lobbyJoinUserRequest the lobby join user request
     * @Version 1.0
     * @since Sprint2
     */
    @Subscribe
    public void onLobbyJoinUserRequest(LobbyJoinUserRequest lobbyJoinUserRequest) {
        Optional<Lobby> lobby = lobbyManagement.getLobby(lobbyJoinUserRequest.getLobbyName());

        if (lobby.isPresent()) {
            lobby.get().joinUser(lobbyJoinUserRequest.getUser());
            sendToAll(lobbyJoinUserRequest.getLobbyName(), new UserJoinedLobbyMessage(lobbyJoinUserRequest.getLobbyName(), lobbyJoinUserRequest.getUser(), lobbyJoinUserRequest.getLobbyID()));
        }
        // TODO: error handling not existing lobby
    }

    /**
     * On lobby leave user request.
     *
     * @param lobbyLeaveUserRequest the lobby leave user request
     * @Version 1.0
     * @since Sprint2
     */
    @Subscribe
    public void onLobbyLeaveUserRequest(LobbyLeaveUserRequest lobbyLeaveUserRequest) {
        Optional<Lobby> lobby = lobbyManagement.getLobby(lobbyLeaveUserRequest.getLobbyName());

        if (lobby.isPresent()) {
            lobby.get().leaveUser(lobbyLeaveUserRequest.getUser());
            sendToAll(lobbyLeaveUserRequest.getLobbyName(), new UserLeftLobbyMessage(lobbyLeaveUserRequest.getLobbyName(), lobbyLeaveUserRequest.getUser(), lobbyLeaveUserRequest.getLobbyID()));
        }
        // TODO: error handling not existing lobby
    }

    /**
     * Nachricht wird auf den Bus gelegt
     *
     * @param lobbyName der Lobby-Name
     * @param message   die Nachricht, die übergeben werden soll
     */
    private void sendToAll(String lobbyName, ServerMessage message) {
        Optional<Lobby> lobby = lobbyManagement.getLobby(lobbyName);

        if (lobby.isPresent()) {
            //message.setReceiver(authenticationService.getSessions(lobby.get().getUsers()));
            post(message);
        }

        // TODO: error handling not existing lobby
    }

    /**
     * On update lobby ready status reqest.
     *
     * @param request the request
     * @author Keno Oelrichs Garcia
     * @Version 1.0
     * @since Sprint3
     */
    @Subscribe
    public void onUpdateLobbyReadyStatusRequest(UpdateLobbyReadyStatusRequest request) {
        Optional<Lobby> lobby = lobbyManagement.getLobby(request.getLobbyName());

        if (lobby.isPresent()) {
            lobby.get().setReadyStatus(request.getUser(), request.isReady());
            ServerMessage msg = new UpdatedLobbyReadyStatusMessage(lobby.get().getLobbyID(), lobby.get().getName(), request.getUser(), lobby.get().getReadyStatus(request.getUser()));
            sendToAll(lobby.get().getName(), msg);
            LOG.debug("Sending Updated Status of User " + request.getUser().getUsername() + " to " + request.isReady() + " in Lobby: " + lobby.get().getLobbyID());
            allPlayersReady(lobby);
        } else
            LOG.debug("Lobby " + request.getLobbyName() + " NOT FOUND!");
    }

    @Subscribe
    public void onRetrieveAllOnlineUsersInLobbyRequest(RetrieveAllOnlineUsersInLobbyRequest request) {
        Optional<Lobby> lobby = lobbyManagement.getLobby(request.getLobbyId());

        if (lobby.isPresent()) {
            ResponseMessage msg = new AllOnlineUsersInLobbyResponse(lobby.get().getName(), lobby.get().getLobbyUsers());
            msg.initWithMessage(request);
            post(msg);
        }
    }

    /**
     * erstellt eine Response-Message und schickt diese ab
     *
     * @author Julia
     */
    @Subscribe
    public void onRetrieveAllOnlineLobbiesRequest(RetrieveAllOnlineLobbiesRequest msg) {
        AllOnlineLobbiesResponse response = new AllOnlineLobbiesResponse(lobbyManagement.getLobbies());
        response.initWithMessage(msg);
        post(response);
    }

    /**
     * Gibt, falls die Lobby existiert, Mitglieder, Name und Lobby in einer AllLobbyUsersResponse zurück.
     *
     * @author Marvin
     */

    @Subscribe
    public void onRetrieveAllLobbyUsersRequest(RetrieveAllLobbyUsersRequest msg) {
        Optional<Lobby> lobby = lobbyManagement.getLobby(msg.getName());
        if (lobby.isPresent()) {
            AllLobbyUsersResponse response = new AllLobbyUsersResponse(lobby.get().getUsers(), lobby.get().getName(), lobby.get());
            response.initWithMessage(msg);
            post(response);
        }
    }

    /**
     * überprüft ob alle Spieler bereit sind
     * Spiel startet wenn 4 Spieler Bereit sind
     *
     * @param lobby the lobby
     */
    private void allPlayersReady(Optional<Lobby> lobby){
        int counter = 0;
        for (User user: lobby.get().getLobbyUsers()) {
            counter++;
            if (!lobby.get().getReadyStatus(user)) return;
            //TODO Change Counter to 4; FOr Testing leave 1
            if (counter == 1) {
                LOG.debug("Game starts in Lobby: " + lobby.get().getName());
                StartGameMessage msg = new StartGameMessage(lobby.get().getName(), lobby.get().getLobbyID());
                sendToAll(lobby.get().getName(), msg);
            }
        }
    }
}
