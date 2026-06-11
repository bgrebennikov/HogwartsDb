package ru.hogwarts.school.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/info")
public class InfoController
{
    private static final Logger logger = LoggerFactory.getLogger(InfoController.class);

    @Value("${server.port:8080}")
    private String serverPort;

    @GetMapping("/port")
    public String getServerPort()
    {
        logger.info("was invoked method for getting application running port");
        logger.debug("Current active application port is: {}", serverPort);

        return serverPort;
    }
}
