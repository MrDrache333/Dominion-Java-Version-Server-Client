package de.uol.swp.server.lobby;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import de.uol.swp.common.lobby.Lobby;
import de.uol.swp.common.lobby.exception.LobbyNotFoundExceptionMessage;
import de.uol.swp.common.lobby.message.*;
import de.uol.swp.common.message.ServerMessage;
import de.uol.swp.server.AbstractService;
import de.uol.swp.server.usermanagement.AuthenticationService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.util.Optional;

public class LobbyService extends AbstractService {
    private static final Logger LOG = LogManager.getLogger(LobbyService.class);

    private final LobbyManagement lobbyManagement;
    private final AuthenticationService authenticationService;

    @Inject
    public LobbyService(LobbyManagement lobbyManagement, AuthenticationService authenticationService, EventBus eventBus) {
        super(eventBus);
        this.lobbyManagement = lobbyManagement;
        this.authenticationService = authenticationService;
    }

    @Subscribe
    public void onCreateLobbyRequest(CreateLobbyRequest msg) {
        lobbyManagement.createLobby(msg.getName(), msg.getOwner());
        ServerMessage returnMessage = new CreateLobbyMessage(msg.getName(), msg.getUser());
        post(returnMessage);
        LOG.info("onCreateLobbyRequest wird auf dem Server aufgerufen.");
    }

    @Subscribe
    public void onLobbyJoinUserRequest(LobbyJoinUserRequest lobbyJoinUserRequest) {
        Optional<Lobby> lobby = lobbyManagement.getLobby(lobbyJoinUserRequest.getName());
        LOG.info("LobbyJoinUserRequest empfangen");
        ServerMessage returnMessage;
        ;
        if (lobby.isPresent()) {
            lobby.get().joinUser(lobbyJoinUserRequest.getUser());
            returnMessage = new UserJoinedLobbyMessage(lobbyJoinUserRequest.getName(), lobbyJoinUserRequest.getUser());
            LOG.info("Lobby vorhanden, User gejoined, UserJoinedLobbyMessage gesendet");
        } else {
            returnMessage = new LobbyNotFoundExceptionMessage("Lobby " + lobbyJoinUserRequest.getName() + " not Found!");
            LOG.info("Lobby nicht vorhanden; LobbyNotFoundExceptionMessage gesendet");
        }
        post(returnMessage);
    }


    @Subscribe
    public void onLobbyLeaveUserRequest(LobbyLeaveUserRequest lobbyLeaveUserRequest) {
        Optional<Lobby> lobby = lobbyManagement.getLobby(lobbyLeaveUserRequest.getName());

        if (lobby.isPresent()) {
            lobby.get().leaveUser(lobbyLeaveUserRequest.getUser());
            sendToAll(lobbyLeaveUserRequest.getName(), new UserLeftLobbyMessage(lobbyLeaveUserRequest.getName(), lobbyLeaveUserRequest.getUser()));
        }
        // TODO: error handling not existing lobby
    }


    public void sendToAll(String lobbyName, ServerMessage message) {
        Optional<Lobby> lobby = lobbyManagement.getLobby(lobbyName);

        if (lobby.isPresent()) {
            message.setReceiver(authenticationService.getSessions(lobby.get().getUsers()));
            post(message);
        }

        // TODO: error handling not existing lobby
    }

}
