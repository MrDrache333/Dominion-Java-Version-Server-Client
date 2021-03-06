package de.uol.swp.common.user.message;

import de.uol.swp.common.message.AbstractServerMessage;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * A message containing all current logged in usernames
 *
 * @author Marco Grawunder
 */
public class UsersListMessage extends AbstractServerMessage {

    private static final long serialVersionUID = -7968574381977330152L;
    private final ArrayList<String> users;

    /**
     * Initialisiert eine neue UsersListMessage.
     *
     * @param users Liste von Nutzern
     */
    public UsersListMessage(List<String> users) {
        this.users = new ArrayList<>(users);
    }

    /**
     * Gibt die Nutzer zurück
     *
     * @return Die Nutzer
     */
    public ArrayList<String> getUsers() {
        return users;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UsersListMessage that = (UsersListMessage) o;
        return Objects.equals(users, that.users);
    }

    @Override
    public int hashCode() {
        return Objects.hash(users);
    }
}
