package dev.kscott.oligarchy.server;

import com.mattmalec.pterodactyl4j.DataType;
import com.mattmalec.pterodactyl4j.EnvironmentValue;
import com.mattmalec.pterodactyl4j.PowerAction;
import com.mattmalec.pterodactyl4j.PteroBuilder;
import com.mattmalec.pterodactyl4j.application.entities.*;
import com.mattmalec.pterodactyl4j.client.entities.ClientServer;
import com.mattmalec.pterodactyl4j.client.entities.PteroClient;
import dev.kscott.oligarchy.server.type.GenericServer;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import reactor.util.annotation.NonNull;

import java.util.*;

@Component
public class ServerService {

    private final PteroApplication api; // the ptero api
    private final PteroClient client;
    private final Random random;
    private List<Node> nodes; // the available nodes
    private Nest nest; // the egg nest
    private Map<String, ApplicationEgg> eggMap; // key = egg id, value = egg
    private final List<Server> startingServers; // contains list of servers that are waiting to be started
    private ApplicationUser owner;
    private Map<ServerType, List<Server>> servers; // the list of servers

    /**
     * Constructs {@code ServerService}.
     */
    public ServerService() {
        // Field init
        this.servers = new HashMap<>();
        this.startingServers = new ArrayList<>();
        this.nodes = new ArrayList<>();
        this.random = new Random();

        for (final ServerType type : ServerType.values()) {
            this.servers.put(type, new ArrayList<>());
        }

        this.api = PteroBuilder.createApplication("https://panel.kscott.dev", System.getProperty("token"));
        this.client = PteroBuilder.createClient("https://panel.kscott.dev", System.getProperty("client"));

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

    /**
     * Creates a server and returns it.
     *
     * @param type the type
     * @return the server
     */
    public Server create(final ServerType type) {
        final ApplicationEgg egg = this.egg(type.eggId());
        final String name = type.name() + this.random.nextInt(9999);

        final ApplicationServer server = this.api.createServer()
                .setAllocation(allocation(type))
                .setDockerImage(egg.getDockerImage())
                .setEgg(egg)
                .setOwner(this.owner)
                .setSwap(0, DataType.MB)
                .setDisk(1000, DataType.MB)
                .setMemory(3000, DataType.MB)
                .setDatabases(0)
                .startOnCompletion(true)
                .setBackups(0)
                .setEnvironment(Map.of(
                        "SERVER_VERSION", EnvironmentValue.ofString("1.3.3.7"),
                        "SERVER_NAME", EnvironmentValue.ofString(name)
                ))
                .setName(name)
                .setStartupCommand(egg.getStartupCommand())
                .execute();

        final GenericServer newServer = new GenericServer(new ServerInfo(server.getId(),
                server.getIdentifier(),
                server.getName(),
                type,
                server.getDefaultAllocation().get().get().getPortInt()));
        this.startingServers.add(newServer);
        return newServer;
    }

    private Allocation allocation(final ServerType type) {
        final boolean isProxy = type == ServerType.PROXY;

        if (isProxy) {
            for (final Node node : this.nodes()) {
                for (final Allocation allocation : node.getAllocations().get().get()) {
                    if (!allocation.isAssigned() && allocation.getAlias().equals("External")) {
                        return allocation;
                    }
                }
            }
        }

        for (final Node node : this.nodes()) {
            for (final Allocation allocation : node.getAllocations().get().get()) {

                if (!allocation.isAssigned()) {
                    return allocation;
                }
            }
        }

        throw new NullPointerException();
    }

    @Scheduled(fixedRate = 10000)
    public void updateServers() {

        this.nest = this.api.retrieveNestById("5").execute();
        this.owner = this.api.retrieveUserById("1").execute();
        this.nodes = this.api.retrieveNodes().execute();

        // Egg init
        this.eggMap = new HashMap<>();

        for (final ServerType type : ServerType.values()) {
            final String eggId = type.eggId();

            if (eggId.equals("")) continue;

            final ApplicationEgg egg = this.api.retrieveEggById(this.nest, eggId).execute();

            this.eggMap.put(eggId, egg);
        }

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

            final ServerInfo info = new ServerInfo(id, server.getIdentifier(), name, type, port);

            newServers.get(type).add(new GenericServer(info));
        }

        this.servers = newServers;

        final Iterator<Server> iterator = this.startingServers.iterator();
        while (iterator.hasNext()) {
            final Server server = iterator.next();

            final ClientServer clientServer = this.client.retrieveServerByIdentifier(server.info().identifier()).execute();

            if (!clientServer.isInstalling()) {
                this.servers.get(server.info().type()).add(server);

                this.client.setPower(clientServer, PowerAction.START).execute();

                iterator.remove();

            }
        }

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
