package de.uol.swp.client.lobby;

import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;
import de.uol.swp.common.lobby.Lobby;
import de.uol.swp.common.lobby.LobbyUser;
import de.uol.swp.common.lobby.request.*;
import de.uol.swp.common.message.RequestMessage;
import de.uol.swp.common.user.dto.UserDTO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * The type Lobby service.
 */
public class LobbyService implements de.uol.swp.common.lobby.LobbyService {

    private static final Logger LOG = LogManager.getLogger(LobbyService.class);
    private final EventBus bus;

    /**
     * Instantiates a new Lobby service.
     *
     * @param bus the bus
     */
    @Inject
    public LobbyService(EventBus bus) {
        this.bus = bus;
    }

    /**
     * erstellt ein LobbyJoinUserRequest und postet es auf den Eventbus
     *
     * @param lobbyName
     * @param user
     * @param lobbyID
     * @author Julia, Paula
     * @since Sprint3
     */
    @Override
    public void joinLobby(String lobbyName, UserDTO user, UUID lobbyID) {
        LobbyJoinUserRequest request = new LobbyJoinUserRequest(lobbyName, user, lobbyID);
        bus.post(request);
    }

    /**
     * erstellt eine LobbyLeaveUserRequest, postet es auf den EventBus
     *
     * @param lobbyName
     * @param user
     * @param lobbyID
     * @author Julia, Paula
     * @since Sprint3
     */
    @Override
    public void leaveLobby(String lobbyName, UserDTO user, UUID lobbyID) {
        LobbyLeaveUserRequest request = new LobbyLeaveUserRequest(lobbyName, user, lobbyID);
        bus.post(request);
    }

    /**
     * erstellt eine LeaveAllLobbiesOnLogoutRequest und postet es auf den EventBus
     *
     * @param user
     * @author Julia, Paula
     * @since Sprint3
     */
    @Override
    public void leaveAllLobbiesOnLogout(UserDTO user) {
        LeaveAllLobbiesOnLogoutRequest request = new LeaveAllLobbiesOnLogoutRequest(user);
        bus.post(request);
    }

    /**
     * erstellt ein RetrieveAllOnlineLobbiesRequest und postet es auf den Eventbus
     *
     * @author Julia
     * @since Sprint2
     */
    @Override
    public List<Lobby> retrieveAllLobbies() {
        RetrieveAllOnlineLobbiesRequest request = new RetrieveAllOnlineLobbiesRequest();
        bus.post(request);
        return null;
    }

    /**
     * Alternative Requesterstellung über lobbyID statt Name.
     *
     * @param lobbyID Lobby ID
     * @author Marvin
     * @since Sprint3
     */
    @Override
    public ArrayList<LobbyUser> retrieveAllUsersInLobby(UUID lobbyID) {
        RequestMessage request = new RetrieveAllOnlineUsersInLobbyRequest(lobbyID);
        bus.post(request);
        return null;
    }

    @Override
    public void setLobbyUserStatus(String LobbyName, UserDTO user, boolean Status) {
        RequestMessage request = new UpdateLobbyReadyStatusRequest(LobbyName, user, Status);
        bus.post(request);
    }
}