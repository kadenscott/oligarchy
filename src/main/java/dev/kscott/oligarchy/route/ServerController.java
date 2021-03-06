package dev.kscott.oligarchy.route;

import dev.kscott.oligarchy.server.Server;
import dev.kscott.oligarchy.server.ServerInfo;
import dev.kscott.oligarchy.server.ServerService;
import dev.kscott.oligarchy.server.ServerType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.util.annotation.NonNull;

import javax.security.auth.message.ServerAuth;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class ServerController {

    @Autowired
    private ServerService serverService;

    @GetMapping("/servers")
    public Map<ServerType, List<ServerInfo>> serverListing() {
        final Map<ServerType, List<ServerInfo>> response = new HashMap<>();
        final Map<ServerType, List<Server>> servers = this.serverService.servers();

        for (final ServerType type : ServerType.values()) {
            response.put(type, new ArrayList<>());
        }

        for (final List<Server> serversOfType : servers.values()) {
            for (final Server server : serversOfType) {
                response.get(server.info().type()).add(server.info());
            }
        }

        return response;
    }

    @GetMapping("/server/create")
    public ServerInfo createServer(@RequestParam String serverType) {
        return this.serverService.create(ServerType.valueOf(serverType)).info();
    }

    @GetMapping("/servers/update")
    public String updateServers() {
        this.serverService.updateServers();
        return "Success";
    }

}
