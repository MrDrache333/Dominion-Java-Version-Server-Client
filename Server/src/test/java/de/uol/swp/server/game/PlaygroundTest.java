package de.uol.swp.server.game;

import com.google.common.eventbus.DeadEvent;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import de.uol.swp.common.game.exception.GamePhaseException;
import de.uol.swp.common.game.request.GameGiveUpRequest;
import de.uol.swp.common.user.User;
import de.uol.swp.common.user.UserDTO;
import de.uol.swp.server.chat.ChatManagement;
import de.uol.swp.server.game.phase.Phase;
import de.uol.swp.server.lobby.LobbyManagement;
import de.uol.swp.server.message.StartGameInternalMessage;
import de.uol.swp.server.usermanagement.AuthenticationService;
import de.uol.swp.server.usermanagement.UserManagement;
import de.uol.swp.server.usermanagement.store.MainMemoryBasedUserStore;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;
import java.util.concurrent.CountDownLatch;

import static org.junit.jupiter.api.Assertions.*;

public class PlaygroundTest {

    static final User defaultOwner = new UserDTO("test1", "test1", "test1@test.de");
    static final User secondPlayer = new UserDTO("test2", "test2", "test2@test2.de");
    static final User thirdPlayer = new UserDTO("test3", "test3", "test3@test3.de");
    static final EventBus bus = new EventBus();
    static final ChatManagement chatManagement = new ChatManagement();
    static final LobbyManagement lobbyManagement = new LobbyManagement();
    static final GameManagement gameManagement = new GameManagement(chatManagement, lobbyManagement);
    static final AuthenticationService authenticationService = new AuthenticationService(bus, new UserManagement(new MainMemoryBasedUserStore()));
    static final GameService gameService = new GameService(bus, gameManagement, authenticationService);

    static UUID gameID;
    private final CountDownLatch lock = new CountDownLatch(1);
    private Object event;

    static void init() {
        gameID = lobbyManagement.createLobby("Test", "", defaultOwner);
        chatManagement.createChat(gameID.toString());
        lobbyManagement.getLobby(gameID).get().joinUser(secondPlayer);
        lobbyManagement.getLobby(gameID).get().joinUser(thirdPlayer);
        bus.post(new StartGameInternalMessage(gameID));
    }

    /**
     * Bei Auftreten eines DeadEvents wird dieses ausgegeben und der CountDownLatch wird um eins verringert
     *
     * @param e das DeadEvent
     * @author Marco
     * @since Start
     */
    @Subscribe
    void handle(DeadEvent e) {
        this.event = e.getEvent();
        System.out.print(e.getEvent());
        lock.countDown();
    }

    /**
     * Setzt vor jedem Test das aktuelle Event auf null und registriert diese Testklasse auf dem Eventbus
     *
     * @author Marco
     * @since Start
     */
    @BeforeEach
    void registerBus() {
        init();
        event = null;
        bus.register(this);
    }

    /**
     * Meldet diese Testklasse nach jedem Test vom Eventbus ab
     *
     * @author Marco
     * @since Start
     */
    @AfterEach
    void deregisterBus() {
        bus.unregister(this);
    }

    /**
     * Testet ob actual- und nextPlayer korrekt aktualisiert werden
     *
     * @author Julia
     * @since Sprint5
     */
    @Test
    void testNewTurn() {
        Playground playground = gameManagement.getGame(gameID).get().getPlayground();
        int actual = playground.getPlayers().indexOf(playground.getActualPlayer());
        int next = playground.getPlayers().indexOf(playground.getNextPlayer());
        //Player an erster Position der Liste beginnt
        assertEquals(0, actual);
        assertEquals(1, next);

        playground.setActualPhase(Phase.Type.Clearphase);
        playground.newTurn();
        actual = playground.getPlayers().indexOf(playground.getActualPlayer());
        next = playground.getPlayers().indexOf(playground.getNextPlayer());
        assertEquals(1, actual);
        assertEquals(2, next);

        playground.setActualPhase(Phase.Type.Clearphase);
        playground.newTurn();
        actual = playground.getPlayers().indexOf(playground.getActualPlayer());
        next = playground.getPlayers().indexOf(playground.getNextPlayer());
        assertEquals(2, actual);
        assertEquals(0, next);
    }

    /**
     * Testet die checkForActionCard - Methode
     *
     * @author Julia
     * @since Sprint5
     */
    @Test
    void testCheckForActionCard() {
        //Bei Spielbeginn hat der Spieler keine Aktionskarten auf der Hand
        assertFalse(gameManagement.getGame(gameID).get().getPlayground().checkForActionCard());

        //TODO: weitere Fälle testen, wenn weitere Funktion (Kauf von Aktionskarten) implementiert wurde
    }

    /**
     * Testet ob Aktions- und Kaufphase übersprungen werden können
     *
     * @author Julia
     * @since Sprint5
     */
    @Test
    void testSkipCurrentPhase() {
        Playground playground = gameManagement.getGame(gameID).get().getPlayground();
        playground.setActualPhase(Phase.Type.ActionPhase);

        playground.skipCurrentPhase();
        assertEquals(Phase.Type.Buyphase, playground.getActualPhase());

        playground.skipCurrentPhase();
        assertEquals(Phase.Type.Clearphase, playground.getActualPhase());
        assertThrows(GamePhaseException.class, () -> playground.skipCurrentPhase());
    }

    /**
     * Testet, ob Requests ankommen und alles richtig durchlaufen wird und ein Spieler entfernt worden ist.
     *
     * @author Ferit
     * @since Sprint6
     */
    @Test
    void playerGaveUpTest() {
        UUID spielID = gameID;
        GameGiveUpRequest testRequest = new GameGiveUpRequest((UserDTO) secondPlayer, spielID);
        bus.post(testRequest);
        assertEquals(2, gameManagement.getGame(gameID).get().getPlayground().getPlayers().size());
    }

    /**
     * Testet, ob der spezifizierte Spieler der Aufgeben will, nach Aufgabe noch im Game befindet.
     *
     * @author Ferit
     * @since Sprint6
     */
    @Test
    void specificPlayerGaveUpTest() {
        UUID spielID = gameID;
        GameGiveUpRequest testRequest = new GameGiveUpRequest((UserDTO) secondPlayer, spielID);
        bus.post(testRequest);
        assertTrue(!gameManagement.getGame(gameID).get().getPlayground().getPlayers().contains(secondPlayer.getUsername()));
    }
}
