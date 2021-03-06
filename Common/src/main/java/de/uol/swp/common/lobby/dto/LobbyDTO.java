package de.uol.swp.common.lobby.dto;

import de.uol.swp.common.lobby.Lobby;
import de.uol.swp.common.user.User;

import java.io.Serializable;
import java.util.*;

/**
 * Die LobbyDTO
 *
 * @author Marco
 * @since Start
 */
public class LobbyDTO implements Lobby, Serializable {

    private static final long serialVersionUID = 998701048176852816L;
    private final TreeMap<String, Boolean> readyStatus = new TreeMap<>();
    private final String name;
    private final String lobbyPassword;
    private final UUID lobbyID;
    private int players;
    private int maxPlayer;
    private boolean inGame;
    private Set<User> users = new TreeSet<>();
    private ArrayList<Short> chosenCards;
    private User owner;

    /**
     * Insatnziiert eine neue LobbyDTO.
     *
     * @param name          der Name
     * @param creator       der Ersteller
     * @param lobbyID       die LobbyID, um Lobbys mit gleichem Namen unterscheiden zu können - Serverseitig
     * @param lobbyPassword das Lobbypasswort
     * @author Marco, Paula, Julia, Timo, Rike, Fenja, Anna
     * @since Start
     */
    public LobbyDTO(String name, User creator, UUID lobbyID, String lobbyPassword) {
        this.name = name;
        this.owner = creator;
        this.users.add(creator);
        this.readyStatus.put(creator.getUsername(), false);
        this.lobbyID = lobbyID;
        this.players = 1;
        this.lobbyPassword = lobbyPassword;
        this.maxPlayer = 4;
        this.inGame = false;
        chosenCards = new ArrayList<>();
    }

    /**
     * Insatnziiert eine LobbyDTO für die Lobbytable im MainMenu
     *
     * @param name          der Name der Lobby
     * @param creator       der Ersteller
     * @param lobbyID       die LobbyId, um Lobbys mit gleichem Namen unterscheiden zu können - Serverseitig
     * @param lobbyPassword das Lobbypasswort
     * @param players       die Spieler
     * @param maxPlayer     die maximale Spieleranzahl
     * @param inGame        ob gerade ein Spiel in der Lobby gespielt wird
     * @author Marco, Julia, Rike, Timo, Fenja, Anna
     * @since Start
     */
    public LobbyDTO(String name, User creator, UUID lobbyID, String lobbyPassword, Set<User> users, int players, int maxPlayer, boolean inGame) {
        this.name = name;
        this.owner = creator;
        this.readyStatus.put(creator.getUsername(), false);
        this.users = users;
        this.lobbyID = lobbyID;
        this.players = players;
        this.lobbyPassword = lobbyPassword;
        this.maxPlayer = maxPlayer;
        this.inGame = inGame;
        chosenCards = new ArrayList<>();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void joinUser(User user) {
        if (users.size() < maxPlayer) {
            this.users.add(user);
            this.readyStatus.put(user.getUsername(), false);
            players++;
        }
    }

    @Override
    public void leaveUser(User user) {
        if (users.contains(user)) {
            this.users.remove(user);
            this.readyStatus.remove(user.getUsername());
            players--;
            if (this.owner.equals(user) && users.size() > 0) {
                updateOwner(users.iterator().next());
            }
        }
    }

    @Override
    public void updateOwner(User user) {
        if (!this.users.contains(user)) {
            throw new IllegalArgumentException("User " + user.getUsername() + "not found. Owner must be member of lobby!");
        }
        this.owner = user;
    }

    @Override
    public User getOwner() {
        return owner;
    }

    @Override
    public Set<User> getUsers() {
        return new TreeSet<>(users);
    }

    @Override
    public UUID getLobbyID() {
        return lobbyID;
    }

    @Override
    public int getPlayers() {
        return players;
    }

    @Override
    public void setReadyStatus(User user, boolean status) {
        if (readyStatus.containsKey(user.getUsername())) {
            readyStatus.replace(user.getUsername(), status);
        }
    }

    @Override
    public boolean getReadyStatus(User user) {
        if (!readyStatus.containsKey(user.getUsername())) return false;
        return readyStatus.get(user.getUsername());
    }

    @Override
    public String getLobbyPassword() {
        return lobbyPassword;
    }

    /**
     * Gibt den readyStatus wieder
     *
     * @return der jeweilige readyStatus.
     * @author Marco
     * @since Start
     */
    public TreeMap<String, Boolean> getEveryReadyStatus() {
        return readyStatus;
    }

    @Override
    public Integer getMaxPlayer() {
        return this.maxPlayer;
    }

    /**
     * Setzt den Max Player Wert bzw. gibt ihn zurück.
     *
     * @param maxPlayer die maximale Spieleranzahl die gesetzt werden soll
     * @author Timo, Rike
     * @since Sprint 3
     */
    @Override
    public void setMaxPlayer(Integer maxPlayer) {
        this.maxPlayer = maxPlayer;
    }

    @Override
    public boolean getInGame() {
        return inGame;
    }

    @Override
    public void setInGame(boolean inGame) {
        this.inGame = inGame;
    }

    @Override
    public ArrayList<Short> getChosenCards() {
        return chosenCards;
    }

    @Override
    public void setChosenCards(ArrayList<Short> chosenCards) {
        this.chosenCards = chosenCards;
    }

    @Override
    public boolean onlyBotsLeft(UUID lobbyID) {
        for (User user : getUsers()) {
            if (!user.getIsBot()) {
                return false;
            }
        }
        return true;
    }
}