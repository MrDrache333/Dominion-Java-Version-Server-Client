package de.uol.swp.common.lobby.message;

import java.util.UUID;

public class StartGameMessage extends AbstractLobbyMessage {

    private static final long serialVersionUID = 8407406031710024918L;

    /**
     * Die Spielstartnachricht bekommt den Lobbynamen und die LobbyID übergeben
     *
     * @param lobbyID Die LobbyID
     * @author Darian, Marvin
     * @since Sprint 3
     */
    public StartGameMessage(UUID lobbyID) {
        super(lobbyID, null);
    }
}