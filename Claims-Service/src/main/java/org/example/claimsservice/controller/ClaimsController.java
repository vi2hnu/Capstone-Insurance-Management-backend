package org.example.claimsservice.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/claim")
public class ClaimsController {
    /*
    1) make claim
    2) get all claim
    3) details of a claim
     */

    @PostMapping("/add")
    public String postMethodName(@RequestBody String entity) {

        return entity;
    }
    
}
