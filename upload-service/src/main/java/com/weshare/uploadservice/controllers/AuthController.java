package com.weshare.uploadservice.controllers;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class AuthController {


    @GetMapping("/auth/user")
    public Map<String,Object> getUser(@AuthenticationPrincipal OAuth2User principal){
        System.out.println("got userrrr "+principal);
        return Map.of(  "email", principal.getAttribute("email"),
                "name", principal.getAttribute("name"),
                "picture", principal.getAttribute("picture"));
    }
}
