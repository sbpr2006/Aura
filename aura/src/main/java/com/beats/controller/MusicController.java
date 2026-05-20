package com.beats.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.beats.model.Users;
import com.beats.repository.MusicRepository;
import com.beats.services.MusicServices;
import com.beats.services.UserService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/usr")
public class MusicController {
	@Autowired(required=true)
	MusicServices musicServices;
	@Autowired
	MusicRepository musicRepo;
	@Autowired
	UserService userService;
	
	@GetMapping("/register")
	public String register() {
		return "register.html";
	}
	
    @PostMapping("/addUser")
    public String addUser(Users user, Model model, HttpSession session) {
        try {
            Users savedUser = musicRepo.save(user);
            session.setAttribute("loggedUser", savedUser);
            session.setAttribute("username", savedUser.getUsername());
            session.setMaxInactiveInterval(30 * 60);
            model.addAttribute("message", "Registration Successful!");
            model.addAttribute("messageType", "success");
            return "index";
        } catch (Exception e) {
            model.addAttribute("message",e.getMessage());
            model.addAttribute("messageType", "error");
            return "register";
        }
    }
    
    @GetMapping("/loginPage")
    public String loginPage() { return "login"; }

    @PostMapping("/login")
    public String loginUser(@RequestParam String username, @RequestParam String password,
                            HttpSession session, Model model) {
        try {
        	Users user =  userService.findByUsernameAndPassword(username,password);
        	System.out.println(user);
        	if(user != null &&
        	   user.getPassword().equals(password)) {
                session.setAttribute("loggedUser", user);
                session.setAttribute("username", user.getUsername());
                session.setAttribute("email", user.getEmail());
                session.setMaxInactiveInterval(30 * 60);
                return "index";
            }
            model.addAttribute("message", "Invalid Username or Password");
            model.addAttribute("messageType", "error");
            return "login.html";
        } catch (Exception e) {
            model.addAttribute("message", e.getMessage());
            model.addAttribute("messageType", "error");
            return "login.html";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }

}
