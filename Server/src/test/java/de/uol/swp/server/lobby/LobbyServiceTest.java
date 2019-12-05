package de.uol.swp.server.lobby;

import com.google.common.eventbus.DeadEvent;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import de.uol.swp.common.lobby.Lobby;
import de.uol.swp.common.lobby.message.CreateLobbyMessage;
import de.uol.swp.common.lobby.message.UserJoinedLobbyMessage;
import de.uol.swp.common.lobby.message.UserLeftLobbyMessage;
import de.uol.swp.common.lobby.request.*;
import de.uol.swp.common.lobby.response.AllOnlineLobbiesResponse;
import de.uol.swp.common.user.User;
import de.uol.swp.common.user.dto.UserDTO;
import de.uol.swp.server.chat.ChatManagement;
import de.uol.swp.server.usermanagement.AuthenticationService;
import de.uol.swp.server.usermanagement.UserManagement;
import de.uol.swp.server.usermanagement.store.MainMemoryBasedUserStore;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LobbyServiceTest {

    private CountDownLatch lock = new CountDownLatch(1);
    private Object event;

    static final User lobbyOwner = new UserDTO("Marco", "Marco", "Marco@Grawunder.com");
    static final User lobbyUser = new UserDTO("Testuser", "1234", "123@test.de");
    static final String defaultLobbyName = "Lobby";

    final EventBus bus = new EventBus();
    final UserManagement userManagement = new UserManagement(new MainMemoryBasedUserStore());
    final LobbyManagement lobbyManagement = new LobbyManagement();
    final LobbyService lobbyService = new LobbyService(lobbyManagement, new AuthenticationService(bus, userManagement), new ChatManagement(), bus);

    @Subscribe
    void handle(DeadEvent e) {
        this.event = e.getEvent();
        System.out.print(e.getEvent());
        lock.countDown();
    }

    @BeforeEach
    void registerBus() {
        event = null;
        bus.register(this);
    }

    @AfterEach
    void deregisterBus() {
        bus.unregister(this);
    }

    @Test
    void onCreateLobbyRequestTest() throws InterruptedException {
        lobbyService.onCreateLobbyRequest(new CreateLobbyRequest(defaultLobbyName, lobbyOwner));

        lock.await(1000, TimeUnit.MILLISECONDS);

        assertTrue(event instanceof CreateLobbyMessage);
        CreateLobbyMessage message = (CreateLobbyMessage) event;
        assertEquals(defaultLobbyName, message.getName());
        assertEquals(lobbyOwner, message.getUser());

        //Test if lobby was created
        assertTrue(lobbyManagement.getLobby(defaultLobbyName).isPresent());
        assertEquals(lobbyOwner, lobbyManagement.getLobby(defaultLobbyName).get().getUsers().iterator().next());
    }

    @Test
    void onLobbyJoinUserRequestTest() throws InterruptedException {
        final UUID lobbyID = lobbyManagement.createLobby(defaultLobbyName, lobbyOwner);
        lobbyService.onLobbyJoinUserRequest(new LobbyJoinUserRequest(defaultLobbyName, lobbyUser, lobbyID));

        lock.await(1000, TimeUnit.MILLISECONDS);

        assertTrue(event instanceof UserJoinedLobbyMessage);
        UserJoinedLobbyMessage message = (UserJoinedLobbyMessage) event;

        assertEquals(lobbyID, message.getLobbyID());
        assertEquals(defaultLobbyName, message.getName());
        assertEquals(lobbyUser, message.getUser());

        //Test if user joined lobby
        assertEquals(2, lobbyManagement.getLobby(defaultLobbyName).get().getUsers().size());
    }

    @Test
    void onLobbyLeaveUserRequestTest() throws InterruptedException {
        final UUID lobbyID = lobbyManagement.createLobby(defaultLobbyName, lobbyOwner);
        lobbyManagement.getLobby(defaultLobbyName).get().joinUser(lobbyUser);
        lobbyService.onLobbyLeaveUserRequest(new LobbyLeaveUserRequest(defaultLobbyName, lobbyOwner, lobbyID));

        lock.await(1000, TimeUnit.MILLISECONDS);

        assertTrue(event instanceof UserLeftLobbyMessage);
        UserLeftLobbyMessage message = (UserLeftLobbyMessage) event;

        assertEquals(lobbyOwner, message.getUser());
        assertEquals(defaultLobbyName, message.getName());
        assertEquals(lobbyID, message.getLobbyID());

        //Test if user left lobby
        Optional<Lobby> lobby = lobbyManagement.getLobby(defaultLobbyName);
        assertTrue(lobby.isPresent());
        assertEquals(lobbyUser, lobby.get().getOwner());

        //Test if user left lobby and lobby was deleted
        lobbyService.onLobbyLeaveUserRequest(new LobbyLeaveUserRequest(defaultLobbyName, lobbyUser, lobbyID));
        lobby = lobbyManagement.getLobby(defaultLobbyName);
        assertTrue(lobby.isEmpty());
    }

    @Test
    void onLeaveAllLobbiesOnLogoutRequestTest() {
        lobbyManagement.createLobby(defaultLobbyName, lobbyOwner);
        lobbyManagement.createLobby("Lobby2", lobbyOwner);
        lobbyManagement.getLobby(defaultLobbyName).get().joinUser(lobbyUser);
        lobbyService.onLeaveAllLobbiesOnLogoutRequest(new LeaveAllLobbiesOnLogoutRequest(lobbyOwner));

        //Test if user was removed from all lobbies
        Optional<Lobby> lobby = lobbyManagement.getLobby(defaultLobbyName);
        assertTrue(lobby.isPresent());
        assertEquals(lobbyUser, lobby.get().getOwner());
        Optional<Lobby> lobby2 = lobbyManagement.getLobby("Lobby2");
        assertTrue(lobby2.isEmpty());
    }

    @Test
    void onRetrieveAllOnlineLobbiesRequestTest() throws InterruptedException {
        lobbyManagement.createLobby(defaultLobbyName, lobbyOwner);
        lobbyManagement.createLobby("Lobby2", lobbyOwner);
        lobbyService.onRetrieveAllOnlineLobbiesRequest(new RetrieveAllOnlineLobbiesRequest());

        lock.await(1000, TimeUnit.MILLISECONDS);

        assertTrue(event instanceof AllOnlineLobbiesResponse);
        AllOnlineLobbiesResponse response = (AllOnlineLobbiesResponse) event;

        //Test lobby list
        List<Lobby> lobbies = new ArrayList<>(response.getLobbies());
        assertEquals(2, lobbies.size());
        assertEquals(defaultLobbyName, lobbies.get(0).getName());
        assertEquals("Lobby2", lobbies.get(1).getName());
    }
}