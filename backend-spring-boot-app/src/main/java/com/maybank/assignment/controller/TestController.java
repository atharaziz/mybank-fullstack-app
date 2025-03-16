package com.maybank.assignment.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller class for handling basic status check endpoint.
 * Provides an endpoint to check if the server is up and running.
 */
@RestController
@RequestMapping("/api/bank")
public class TestController {

    /**
     * Endpoint to check if the server is up and running.
     *
     * @return a simple status message indicating the server is running.
     */
    @GetMapping("/status")
    public String sayHello() {
        return "Server is up and running!";
    }
}

