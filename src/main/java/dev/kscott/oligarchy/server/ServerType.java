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

    private final String eggId;

    ServerType(final String eggId) {
        this.eggId = eggId;
    }

    public String eggId() {
        return this.eggId;
    }

    /**
     * Returns the server type with the id.
     * <p>
     * If {@code eggId} is empty (null, ""), this will return {@link ServerType#GENERIC}.
     *
     * @param eggId the egg id
     * @return the type
     */
    public static ServerType findByEggId(final String eggId) {
        if (eggId.equals("")) return GENERIC;

        for (final ServerType type : values()) {
            if (type.eggId.equals(eggId)) {
                return type;
            }
        }

        return GENERIC;
    }
}
