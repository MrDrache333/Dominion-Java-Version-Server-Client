package de.uol.swp.common.user.response;

import de.uol.swp.common.message.AbstractResponseMessage;
import de.uol.swp.common.user.User;

import java.util.Objects;

/**
 * Eine Nachricht die die Sitzung beinhaltet (in der Regel für einen neu eingeloggten User)
 *
 * @author Marco
 * @since Start
 */
public class LoginSuccessfulResponse extends AbstractResponseMessage {

    private static final long serialVersionUID = -9107206137706636541L;

    private final User user;

    public LoginSuccessfulResponse(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LoginSuccessfulResponse that = (LoginSuccessfulResponse) o;
        return Objects.equals(user, that.user);
    }

    @Override
    public int hashCode() {
        return Objects.hash(user);
    }

}
