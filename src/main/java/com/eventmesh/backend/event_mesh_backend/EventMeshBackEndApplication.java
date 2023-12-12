package com.eventmesh.backend.event_mesh_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;

/**Main application */
@SpringBootApplication
public class EventMeshBackEndApplication {

    public static void main(String[] args) {
        SpringApplication.run(EventMeshBackEndApplication.class, args);
    }

}
