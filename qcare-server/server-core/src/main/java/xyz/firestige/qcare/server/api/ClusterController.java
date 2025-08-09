package xyz.firestige.qcare.server.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/cluster")
public class ClusterController {
    @GetMapping("/leader")
    public String leaderHost() {
        return "localhost:8080";
    }
}
