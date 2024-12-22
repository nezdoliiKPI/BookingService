package com.example.lab2.controller.pageController;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/pages")
public class PageController {

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/main-page")
    public String mainPage() {
        return "user/mainPage";
    }

    @GetMapping("/booking-list")
    public String bookingList() {
        return "user/bookingList";
    }

    @GetMapping("/post-list")
    public String postList() {
        return "user/postList";
    }

    @GetMapping("/profile")
    public String profile() {
        return "profile";
    }

    @GetMapping("/admin/profile")
    public String adminProfile() {
        return "admin/profile";
    }

    @GetMapping("/post")
    public String post() {
        return "user/post";
    }

    @GetMapping("/booked-post")
    public String bookedPost() {
        return "user/bookedPost";
    }

    @GetMapping("/post-form")
    public String postForm() {
        return "user/postForm";
    }

    @GetMapping("/look-post")
    public String lookPost() {
        return "user/lookPost";
    }

    @GetMapping("/edit-post")
    public String editPost() {
        return "user/editPost";
    }

    @GetMapping("/admin/posts")
    public String adminMainPage() {
        return "admin/mainPage";
    }

    @GetMapping("/admin/reviews")
    public String reviewsPage() {
        return "admin/reviews";
    }

    @GetMapping("/admin/post")
    public String adminLookPost() {
        return "admin/lookPost";
    }
}
