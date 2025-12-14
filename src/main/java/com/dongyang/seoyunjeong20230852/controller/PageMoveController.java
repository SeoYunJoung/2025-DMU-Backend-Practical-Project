package com.dongyang.seoyunjeong20230852.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageMoveController {

    @GetMapping("/")
    public String home() {
        return "homePage";
    }

    @GetMapping("/login")
    public String loginPage() {
        return "join/loginPage";
    }

    @GetMapping("/signup")
    public String signupPage() {
        return "join/signUpPage";
    }

    @GetMapping("/profile")
    public String profilePage() {
        return "profilePage";
    }

    @GetMapping("/gallery")
    public String galleryPage() {
        return "galleryPage";
    }

    @GetMapping("/calendar")
    public String calendarPage() {
        return "calendarPage";
    }

    @GetMapping("/guestbook")
    public String guestbookPage() {
        return "guestbookPage";
    }
}
