package com.farmorai.backend.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping
public class LoginController {

    @ResponseBody
    @PostMapping("/login")
    public String login(@RequestBody String email) {
        return "login";
    }

    @ResponseBody
    @GetMapping("/loginfailed")
    public String loginError() {
        return "loginfailed";
    }

    @ResponseBody
    @GetMapping("/logout")
    public String logout() {
        return "logout";
    }
}
