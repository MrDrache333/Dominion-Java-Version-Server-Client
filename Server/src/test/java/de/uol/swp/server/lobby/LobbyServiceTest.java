package de.uol.swp.server.lobby;

import com.google.common.eventbus.DeadEvent;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import de.uol.swp.common.chat.message.NewChatMessage;
import de.uol.swp.common.lobby.Lobby;
import de.uol.swp.common.lobby.message.CreateLobbyMessage;
import de.uol.swp.common.lobby.message.UpdatedInGameMessage;
import de.uol.swp.common.lobby.message.UserJoinedLobbyMessage;
import de.uol.swp.common.lobby.message.UserLeftLobbyMessage;
import de.uol.swp.common.lobby.request.*;
import de.uol.swp.common.lobby.response.AllOnlineLobbiesResponse;
import de.uol.swp.common.user.User;
import de.uol.swp.common.user.UserDTO;
import de.uol.swp.common.user.request.LoginRequest;
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

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testklasse des LobbyService
 *
 * @author Julia
 * @since Sprint 3
 */
class LobbyServiceTest {

    static final User lobbyOwner = new UserDTO("Marco", "Marco", "Marco@Grawunder.com");
    static final User lobbyUser = new UserDTO("Testuser", "1234", "123@test.de");
    static final String defaultLobbyName = "Lobby";
    static final String defaultLobbyPassword = "Lobby";
    final EventBus bus = new EventBus();
    final UserManagement userManagement = new UserManagement(new MainMemoryBasedUserStore());
    final LobbyManagement lobbyManagement = new LobbyManagement();
    final AuthenticationService authenticationService = new AuthenticationService(bus, userManagement, lobbyManagement);
    final LobbyService lobbyService = new LobbyService(lobbyManagement, authenticationService, new ChatManagement(), bus);
    private final CountDownLatch lock = new CountDownLatch(1);
    private Object event;

    /**
     * Regelt den Umgang mit DeadEvents
     *
     * @author Julia
     * @since Sprint 3
     */
    @Subscribe
    void handle(DeadEvent e) {
        this.event = e.getEvent();
        System.out.print(e.getEvent());
        lock.countDown();
    }

    /**
     * Registriert den EventBus
     *
     * @author Julia
     * @since Sprint 3
     */
    @BeforeEach
    void registerBus() {
        event = null;
        bus.register(this);
    }

    /**
     * Deregistriert den EventBus
     *
     * @author Julia
     * @since Sprint 3
     */
    @AfterEach
    void deregisterBus() {
        bus.unregister(this);
    }

    /**
     * Prüft ob die Lobby angelegt wurde
     *
     * @author Julia
     * @since Sprint 3
     */
    @Test
    void onCreateLobbyRequestTest() throws InterruptedException {
        loginUsers();

        lobbyService.onCreateLobbyRequest(new CreateLobbyRequest(defaultLobbyName, (UserDTO) lobbyOwner, ""));

        lock.await(1000, TimeUnit.MILLISECONDS);

        assertTrue(event instanceof CreateLobbyMessage);
        CreateLobbyMessage message = (CreateLobbyMessage) event;
        assertEquals(defaultLobbyName, message.getLobbyName());
        assertEquals(lobbyOwner, message.getUser());

        //Test if lobby was created
        assertTrue(lobbyManagement.getLobby(message.getLobby().getLobbyID()).isPresent());
        assertEquals(lobbyOwner, lobbyManagement.getLobby(message.getLobby().getLobbyID()).get().getUsers().iterator().next());
    }

    /**
     * Prüft, ob der User erfolgreich der Lobby beigetreten ist
     *
     * @author Julia
     * @since Sprint 3
     */
    @Test
    void onLobbyJoinUserRequestTest() throws InterruptedException {
        loginUsers();

        final UUID lobbyID = lobbyManagement.createLobby(defaultLobbyName, defaultLobbyPassword, lobbyOwner);
        lobbyService.onLobbyJoinUserRequest(new LobbyJoinUserRequest(lobbyID, new UserDTO(lobbyUser.getUsername(), lobbyUser.getPassword(), lobbyUser.getEMail()), false));

        lock.await(1000, TimeUnit.MILLISECONDS);

        assertTrue(event instanceof UserJoinedLobbyMessage);
        UserJoinedLobbyMessage message = (UserJoinedLobbyMessage) event;

        assertEquals(lobbyID, message.getLobbyID());
        assertEquals(defaultLobbyName, message.getLobby().getName());
        assertEquals(lobbyUser, message.getUser());

        //Test if user joined lobby
        assertEquals(2, lobbyManagement.getLobby(lobbyID).get().getUsers().size());
    }

    /**
     * Prüft ob der User die Lobby erfolgreich verlassen hat
     *
     * @author Julia
     * @since Sprint 3
     */
    @Test
    void onLobbyLeaveUserRequestTest() throws InterruptedException {
        loginUsers();

        UUID lobbyID = lobbyManagement.createLobby(defaultLobbyName, defaultLobbyPassword, lobbyOwner);
        lobbyManagement.getLobby(lobbyID).get().joinUser(lobbyUser);
        lobbyService.onLobbyLeaveUserRequest(new LobbyLeaveUserRequest(lobbyID, new UserDTO(lobbyOwner.getUsername(), lobbyOwner.getPassword(), lobbyOwner.getEMail())));

        lock.await(1000, TimeUnit.MILLISECONDS);

        assertTrue(event instanceof UserLeftLobbyMessage);
        UserLeftLobbyMessage message = (UserLeftLobbyMessage) event;

        assertEquals(lobbyOwner, message.getUser());
        assertEquals(defaultLobbyName, message.getLobby().getName());
        assertEquals(lobbyID, message.getLobbyID());

        //Test if user left lobby
        Optional<Lobby> lobby = lobbyManagement.getLobby(lobbyID);
        assertTrue(lobby.isPresent());
        assertEquals(lobbyUser, lobby.get().getOwner());

        //Test if user left lobby and lobby was deleted
        lobbyService.onLobbyLeaveUserRequest(new LobbyLeaveUserRequest(lobbyID, new UserDTO(lobbyUser.getUsername(), lobbyUser.getPassword(), lobbyUser.getEMail())));
        lobby = lobbyManagement.getLobby(lobbyID);
        assertTrue(lobby.isEmpty());
    }

    /**
     * Prüft ob der User alle Lobbys verlassen hat
     *
     * @author Julia
     * @since Sprint 3
     */
    @Test
    void onLeaveAllLobbiesOnLogoutRequestTest() {
        loginUsers();

        UUID lobbyID = lobbyManagement.createLobby(defaultLobbyName, defaultLobbyPassword, lobbyOwner);
        UUID lobbyID2 = lobbyManagement.createLobby("Lobby2", "", lobbyOwner);
        lobbyManagement.getLobby(lobbyID).get().joinUser(lobbyUser);
        lobbyService.onLeaveAllLobbiesOnLogoutRequest(new LeaveAllLobbiesOnLogoutRequest(new UserDTO(lobbyOwner.getUsername(), lobbyOwner.getPassword(), lobbyOwner.getEMail())));

        //Test if user was removed from all lobbies
        Optional<Lobby> lobby = lobbyManagement.getLobby(lobbyID);
        assertTrue(lobby.isPresent());
        assertEquals(lobbyUser, lobby.get().getOwner());
        Optional<Lobby> lobby2 = lobbyManagement.getLobby(lobbyID2);
        assertTrue(lobby2.isEmpty());
    }

    /**
     * Überprüft diee LobbyListe
     *
     * @author Julia
     * @since Sprint 3
     */
    @Test
    void onRetrieveAllOnlineLobbiesRequestTest() throws InterruptedException {
        loginUsers();

        lobbyManagement.createLobby(defaultLobbyName, defaultLobbyPassword, lobbyOwner);
        lobbyManagement.createLobby("Lobby2", defaultLobbyPassword, lobbyOwner);
        lobbyService.onRetrieveAllOnlineLobbiesRequest(new RetrieveAllOnlineLobbiesRequest());

        lock.await(1000, TimeUnit.MILLISECONDS);

        assertTrue(event instanceof AllOnlineLobbiesResponse);
        AllOnlineLobbiesResponse response = (AllOnlineLobbiesResponse) event;

        //Test lobby list
        List<Lobby> lobbies = new ArrayList<>(response.getLobbies());
        assertEquals(2, lobbies.size());
        assertTrue((lobbies.get(0).getName().equals(defaultLobbyName) && lobbies.get(1).getName().equals("Lobby2"))
                || (lobbies.get(1).getName().equals(defaultLobbyName) && lobbies.get(0).getName().equals("Lobby2")));
    }

    /**
     * Prüft ob sich die Lobby nach einem Spiel noch im Spiel befindet.
     *
     * @author Julia
     * @since Sprint 6
     */
    @Test
    void onGameEndTest() throws InterruptedException {
        loginUsers();

        UUID lobbyID = lobbyManagement.createLobby(defaultLobbyName, defaultLobbyPassword, lobbyOwner);
        lobbyManagement.getLobby(lobbyID).get().setInGame(true);
        lobbyService.onGameEnd(new UpdateInGameRequest(lobbyID));

        lock.await(1000, TimeUnit.MILLISECONDS);

        assertTrue(event instanceof UpdatedInGameMessage);
        UpdatedInGameMessage message = (UpdatedInGameMessage) event;
        assertEquals(lobbyID, message.getLobbyID());
        assertFalse(lobbyManagement.getLobby(lobbyID).get().getInGame());
    }

    /**
     * Regelt den Umgang mit DeadEvents
     *
     * @author Anna
     * @since Sprint 7
     */
    @Test
    void onSendChosenCardsTest() throws InterruptedException {
        loginUsers();
        UUID lobbyID = lobbyManagement.createLobby(defaultLobbyName, defaultLobbyPassword, lobbyOwner);
        ArrayList<Short> chosenCards = new ArrayList<>();
        chosenCards.add((short) 5);
        chosenCards.add((short) 7);
        lobbyService.onSendChosenCardsRequest(new SendChosenCardsRequest(lobbyID, chosenCards));
        lock.await(500, TimeUnit.MILLISECONDS);
        assertTrue(event instanceof NewChatMessage);
    }

    /**
     * Hilfsmethode zum Einloggen der User
     *
     * @author Julia
     * @since Sprint 3
     */
    void loginUsers() {
        userManagement.createUser(lobbyUser);
        userManagement.createUser(lobbyOwner);

        authenticationService.onLoginRequest(new LoginRequest(lobbyOwner.getUsername(), lobbyOwner.getPassword()));
        authenticationService.onLoginRequest(new LoginRequest(lobbyUser.getUsername(), lobbyUser.getPassword()));
    }
}