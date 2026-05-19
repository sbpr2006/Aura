package com.beats.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.beats.model.Users;
import com.beats.repository.MusicRepository;
import com.beats.services.MusicServices;

@RestController
@RequestMapping("/usr")
public class MusicController {
	@Autowired
	MusicServices musicService;
	MusicRepository musicRepo;
	
	@GetMapping("/register")
	public String register() {
		return "register.html";
	}
	
    @PostMapping("/addUser")
    public String addUser(Users user, Model model) {
    	try {
        	musicRepo.save(user);
            model.addAttribute("message", "Registration Successful!");
            model.addAttribute("messageType", "success");
            return "login";
       }catch(Exception e) {
	        model.addAttribute("message", "An error occurred. Please try again.");
	        model.addAttribute("messageType", "error");
	        return "register";
       }
    }
}
