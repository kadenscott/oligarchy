package dev.kscott.oligarchy.server;

import com.mattmalec.pterodactyl4j.PteroBuilder;
import com.mattmalec.pterodactyl4j.application.entities.*;
import dev.kscott.oligarchy.server.type.GenericServer;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class ServerService {

    private final
    List<Node> nodes; // the available nodes
    private final
    PteroApplication api; // the ptero api
    private final
    Nest nest; // the egg nest
    private final Map<String, ApplicationEgg> eggMap; // key = egg id, value = egg
    private Map<ServerType, List<Server>> servers; // the list of servers

    public ServerService() {
        // Field init
        this.servers = new HashMap<>();
        this.nodes = new ArrayList<>();

        for (final ServerType type : ServerType.values()) {
            this.servers.put(type, new ArrayList<>());
        }

        this.api = PteroBuilder.createApplication("https://panel.kscott.dev", System.getenv("PANEL_TOKEN"));

        this.nest = this.api.retrieveNestById("5").execute();

        // Egg init
        this.eggMap = new HashMap<>();

        for (final ServerType type : ServerType.values()) {
            final String eggId = type.eggId();

            if (eggId.equals("")) continue;

            final ApplicationEgg egg = this.api.retrieveEggById(this.nest, eggId).execute();

            this.eggMap.put(eggId, egg);
        }

        updateServers();
    }

    /**
     * Returns a list of available nodes.
     *
     * @return the nodes
     */
    public List<Node> nodes() {
        return List.copyOf(this.nodes);
    }

    /**
     * Returns a map containing all available servers.
     *
     * @return the server map
     */
    public Map<ServerType, List<Server>> servers() {
        final Map<ServerType, List<Server>> copy = new HashMap<>();

        for (final ServerType type : ServerType.values()) {
            copy.put(type, List.copyOf(this.servers.get(type)));
        }

        return Map.copyOf(copy);
    }

    /**
     * Returns the egg with the provided id.
     *
     * @param eggId the egg id
     * @return the egg
     * @throws NullPointerException if there was no egg with the given id
     */
    public ApplicationEgg egg(final String eggId) {
        return Objects.requireNonNull(this.eggMap.get(eggId));
    }

    private void updateServers() {
        final Map<ServerType, List<Server>> newServers = new HashMap<>();

        for (final ServerType type : ServerType.values()) {
            newServers.put(type, new ArrayList<>());
        }

        final List<ApplicationServer> servers = this.api.retrieveServers().execute();

        for (final ApplicationServer server : servers) {
            final String serverEggId = server.getEgg().get().get().getId();

            final ServerType type = ServerType.findByEggId(serverEggId);
            final String name = server.getName();
            final String id = server.getId();
            final int port = server.getDefaultAllocation().get().get().getPortInt();

            final ServerInfo info = new ServerInfo(id, name, type, port);

            newServers.get(type).add(new GenericServer(info));
        }

        this.servers = newServers;

        logServers();
    }

    private void logServers() {
        System.out.println("------- Registered Servers -------");

        final Map<ServerType, Integer> countMap = new HashMap<>();

        for (final ServerType type : ServerType.values()) {
            final List<Server> serversOfType = this.servers.get(type);

            int count = 0;

            for (final Server server : serversOfType) {
                logServer(server);
                count++;
            }

            countMap.put(type, count);
        }

        for (final var entry : countMap.entrySet()) {
            System.out.println("Type " + entry.getKey() + " has " + entry.getValue() + " server" + (entry.getValue() == 1 ? "" : "s"));
        }

        System.out.println("----------------------------------");
    }

    private void logServer(final Server server) {
        final String id = server.info().id();
        final String name = server.info().name();
        final ServerType type = server.info().type();
        final int port = server.info().port();

        System.out.println("[" + id + "] " + name + ": " + type.name() + " on :" + port);
    }
}
