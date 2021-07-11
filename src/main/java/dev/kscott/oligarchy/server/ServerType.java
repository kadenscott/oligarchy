package dev.kscott.oligarchy.server;

/**
 * The possible types of a server.
 */
public enum ServerType {
    /**
     * Represents a generic or unknown server type.
     * <p>
     * Example: test server, survival, etc.
     */
    GENERIC,

    /**
     * Represents the type of a lobby server.
     */
    LOBBY,

    /**
     * Represents the type of a Bonk server.
     */
    BONK
}
