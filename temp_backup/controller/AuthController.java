package com.healthcare.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import java.util.HashMap;
import java.util.Map;

@Controller
public class AuthController {

    @GetMapping("/login")
    public ModelAndView login() {
        ModelAndView mv = new ModelAndView("login");
        mv.addObject("message", "Please log in to continue");
        return mv;
    }

    @PostMapping("/login")
    public String processLogin() {
        // For now, redirect to home page
        return "redirect:/";
    }

    @GetMapping("/register")
    public ModelAndView register() {
        ModelAndView mv = new ModelAndView("register");
        return mv;
    }

    @GetMapping("/logout")
    public String logout() {
        return "redirect:/";
    }
    
    @PostMapping("/logout")
    public String processLogout() {
        return "redirect:/";
    }
    

}