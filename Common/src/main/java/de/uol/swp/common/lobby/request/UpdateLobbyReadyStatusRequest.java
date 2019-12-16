package de.uol.swp.common.lobby.request;

import de.uol.swp.common.user.dto.UserDTO;

/**
 * The type Update lobby ready status request.
 */
public class UpdateLobbyReadyStatusRequest extends AbstractLobbyRequest {

    private boolean Ready;

    /**
     * Instantiates a new Update lobby ready status request.
     *
     * @param lobby the lobby
     * @param user  the user
     * @param ready the Status
     */
    public UpdateLobbyReadyStatusRequest(String lobby, UserDTO user, boolean ready) {
        super(lobby, user);
        this.Ready = ready;
    }

    /**
     * Is status boolean.
     *
     * @return the boolean
     */
    public boolean isReady() {
        return Ready;
    }
}
