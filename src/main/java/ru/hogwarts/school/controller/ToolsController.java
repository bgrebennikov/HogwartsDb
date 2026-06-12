package ru.hogwarts.school.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.stream.IntStream;

@RestController
@RequestMapping("/tools")
public class ToolsController {

    private static final Logger log = LoggerFactory.getLogger(ToolsController.class);

    @GetMapping("/sum")
    public Integer getCalculatedSum() {
        long startTime = System.currentTimeMillis();

        int sum = IntStream.rangeClosed(1, 1_000_000)
                .parallel()
                .sum();

        long endTime = System.currentTimeMillis();

        log.debug("Sum calculated in {} ms. Result: {}", (endTime - startTime), sum);

        return sum;
    }

}
