package dev.kscott.oligarchy.server;

import com.mongodb.lang.NonNull;

/**
 * Holds data for the server.
 */
public record ServerInfo(@NonNull String id, int port, @NonNull ServerType type) {
}
