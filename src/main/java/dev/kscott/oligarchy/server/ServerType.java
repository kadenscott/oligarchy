package dev.kscott.oligarchy.server;

import com.mongodb.lang.NonNull;

/**
 * The possible types of a server.
 */
public enum ServerType {
    /**
     * Represents a generic or unknown server type.
     * <p>
     * Example: test server, survival, etc.
     */
    GENERIC(""),

    /**
     * Represents the type of a lobby server.
     */
    LOBBY("17"),

    /**
     * Represents the type of a Bonk server.
     */
    BONK(""),

    /**
     * Represents the type of a proxy server.
     */
    PROXY("16");

    private final @NonNull String eggId;

    ServerType(final @NonNull String eggId) {
        this.eggId = eggId;
    }
}
