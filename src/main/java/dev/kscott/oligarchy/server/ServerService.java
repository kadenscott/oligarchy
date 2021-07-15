package dev.kscott.oligarchy.server;

import com.mattmalec.pterodactyl4j.PteroBuilder;
import com.mattmalec.pterodactyl4j.application.entities.Node;
import com.mattmalec.pterodactyl4j.application.entities.PteroApplication;
import com.mongodb.lang.NonNull;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class ServerService {

    private final @NonNull List<Node> nodes; // the available nodes
    private final @NonNull EnumMap<ServerType, List<Server>> servers; // the list of servers
    private final @NonNull PteroApplication api; // the ptero api

    public ServerService() {
        this.servers = new EnumMap<>(new HashMap<>());
        this.nodes = new ArrayList<>();

        for (final ServerType type : ServerType.values()) {
            this.servers.put(type, new ArrayList<>());
        }

        this.api = PteroBuilder.createApplication("https://panel.kscott.dev", System.getProperty("panelToken"));


    }

    /**
     * Returns a list of available nodes.
     *
     * @return the nodes
     */
    public @NonNull List<Node> nodes() {
        return List.copyOf(this.nodes);
    }

    /**
     * Returns a map containing all available servers.
     *
     * @return the server map
     */
    public @NonNull Map<ServerType, List<Server>> servers() {
        final Map<ServerType, List<Server>> copy = new EnumMap<>(new HashMap<>());

        for (final ServerType type : ServerType.values()) {
            copy.put(type, List.copyOf(this.servers.get(type)));
        }

        return Map.copyOf(copy);
    }
}
