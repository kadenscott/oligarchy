package dev.kscott.oligarchy.server;

import java.io.Serializable;

/**
 * Holds data for the server.
 */
public record ServerInfo(String id,
                         String identifier,
                         String name,
                         ServerType type,
                         int port) implements Serializable {
}
