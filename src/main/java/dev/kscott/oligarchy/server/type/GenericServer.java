package dev.kscott.oligarchy.server.type;

import dev.kscott.oligarchy.server.Server;
import dev.kscott.oligarchy.server.ServerInfo;

import java.io.Serializable;

/**
 * A generic server.
 */
public class GenericServer implements Server {

    private final ServerInfo info;

    /**
     * Constructs {@code GenericServer}.
     *
     * @param info the server's info
     */
    public GenericServer(final ServerInfo info) {
        this.info = info;
    }

    public ServerInfo info() {
        return this.info;
    }

}
