package de.uol.swp.common.lobby.request;

import de.uol.swp.common.user.UserDTO;

import java.util.UUID;

/**
 * Request zum Beitreten einer Lobby
 *
 * @author Julia, Paula
 * @since Sprint3
 */
public class LobbyJoinUserRequest extends AbstractLobbyRequest {

    private static final long serialVersionUID = 5835542658263297422L;
    String lobbyPassword;

    Boolean isBot;

    /**
     * Instanziiert ein LobbyJoinUserRequest
     *
     * @param lobbyID die ID der Lobby
     * @param user    der User, der der Lobby beitreten will
     * @author Julia, Paula, Marvin
     * @since Sprint3
     */
    public LobbyJoinUserRequest(UUID lobbyID, UserDTO user, Boolean isBot) {
        super(lobbyID, user);
        this.isBot = isBot;
    }

    /**
     * Konstruktor für die Serialisierung
     *
     * @author Julia, Paula
     * @since Sprint3
     */


    public LobbyJoinUserRequest() {
    }

    public Boolean getBot() {
        return isBot;
    }


    /**
     * Getter für das Lobbypasswort.
     *
     * @return das Lobby-Passwort.
     */
    public String getLobbyPassword() {
        return lobbyPassword;
    }


}
