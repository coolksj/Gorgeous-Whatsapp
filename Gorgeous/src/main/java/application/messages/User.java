package application.messages;

import java.io.Serializable;

/**
 * Created by Dominic on 01-May-16.
 */
public class User implements Serializable {
    public String jid;
    public String name;
    public byte[] picture;
    public String status;
    public boolean online = false;
    public String last;

    @Override
    public boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }

        if (obj instanceof String) {
            return jid.equals(obj.toString());
        }

        User otherUser = (User) obj;
        return jid.equals(otherUser.jid);
    }

    @Override
    public int hashCode() {
        return jid.hashCode();
    }
}
