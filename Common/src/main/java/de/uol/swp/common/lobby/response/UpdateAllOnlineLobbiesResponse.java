package de.uol.swp.common.lobby.response;

import de.uol.swp.common.lobby.Lobby;
import de.uol.swp.common.lobby.dto.LobbyDTO;
import de.uol.swp.common.message.AbstractResponseMessage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

public class UpdateAllOnlineLobbiesResponse extends AbstractResponseMessage {

    final private ArrayList<LobbyDTO> lobbies = new ArrayList<>();
    private int members;

    public UpdateAllOnlineLobbiesResponse(){
        // needed for serialization
    }

    public UpdateAllOnlineLobbiesResponse(Collection<Lobby> lobbies, String name, boolean value) {
        for (Lobby lobby : lobbies) {
            this.members = lobby.getPlayers();
            if(lobby.getName().equals(name) && value) {
                this.lobbies.add(new LobbyDTO(lobby.getName(), lobby.getOwner(), lobby.getLobbyID(), ++members));
            }
            else if(lobby.getName().equals(name) && !value) {
                this.lobbies.add(new LobbyDTO(lobby.getName(), lobby.getOwner(), lobby.getLobbyID(), --members));
            }
            else {
                this.lobbies.add(new LobbyDTO(lobby.getName(), lobby.getOwner(), lobby.getLobbyID(), members));
            }
        }
    }

    public List<LobbyDTO> getLobbies() {
        return lobbies;
    }
}
