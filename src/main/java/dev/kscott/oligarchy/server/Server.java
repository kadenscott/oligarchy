package dev.kscott.oligarchy.server;

import com.mongodb.lang.NonNull;

/**
 * Represents a server.
 */
public interface Server {

    @NonNull ServerInfo info();

}
