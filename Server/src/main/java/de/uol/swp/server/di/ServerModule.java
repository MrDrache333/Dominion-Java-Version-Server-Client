package de.uol.swp.server.di;

import com.google.common.eventbus.EventBus;
import com.google.inject.AbstractModule;
import de.uol.swp.server.chat.ChatManagement;
import de.uol.swp.server.game.GameManagement;
import de.uol.swp.server.lobby.LobbyManagement;
import de.uol.swp.server.usermanagement.AuthenticationService;
import de.uol.swp.server.usermanagement.UserManagement;
import de.uol.swp.server.usermanagement.store.DatabaseBasedUserStore;
import de.uol.swp.server.usermanagement.store.MainMemoryBasedUserStore;
import de.uol.swp.server.usermanagement.store.UserStore;

@SuppressWarnings("UnstableApiUsage")
public class ServerModule extends AbstractModule {

    private final EventBus bus = new EventBus();
    private final UserStore store = setUserStore();
    private final UserManagement userManagement = new UserManagement(store);
    private final ChatManagement chatManagement = new ChatManagement();
    private final LobbyManagement lobbyManagement = new LobbyManagement();
    private final AuthenticationService authenticationService = new AuthenticationService(bus, userManagement, lobbyManagement);
    private final GameManagement gameManagement = new GameManagement(chatManagement, lobbyManagement);

    //Hier kann zwischen MainMemoryBasedUserStrore oder DatabaseBasedUserStore gewählt werden.
    boolean isDatabaseBasedUserStore = false;


    /**
     * Alle Usermanagements und Eventbusse bekommen die gleichen Instanzen
     *
     * @author Marco
     * @since Start
     */
    @Override
    protected void configure() {


        // Alle UserManagements und EventBusse müssen die selbe Instanz sein!
        bind(UserManagement.class).toInstance(userManagement);
        bind(ChatManagement.class).toInstance(chatManagement);
        bind(LobbyManagement.class).toInstance(lobbyManagement);
        bind(ChatManagement.class).toInstance(chatManagement);
        bind(GameManagement.class).toInstance(gameManagement);
        bind(ChatManagement.class).toInstance(chatManagement);
        bind(UserStore.class).toInstance(store);
        bind(EventBus.class).toInstance(bus);
        bind(AuthenticationService.class).toInstance(authenticationService);
    }
    //Gibt zurück, ob der aktuelle UserStore ein DatabaseBasedUserStore ist
    public boolean isDatabaseBasedUserStore () {
        return isDatabaseBasedUserStore;
    }

    //Gibt einen neuen UserStore zurück
    private UserStore setUserStore () {
        if (isDatabaseBasedUserStore)
            return new DatabaseBasedUserStore();
        else
            return new MainMemoryBasedUserStore();
    }
}



