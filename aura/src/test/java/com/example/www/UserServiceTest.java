package com.example.www;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.beats.AuraApplication;
import com.beats.model.Users;
import com.beats.services.UserService;

@SpringBootTest(classes = AuraApplication.class)
@ActiveProfiles("test")
@Transactional
class UserServiceTest {

    @Autowired
    private UserService userService;

    @Test
    void testSaveUser() {
        Users user = Users.builder()
                .username("testuser")
                .email("testuser@example.com")
                .password("password123")
                .name("Test User")
                .bio("My bio description")
                .build();

        Users saved = userService.saveUser(user);

        assertNotNull(saved.getUserId());
        assertEquals("testuser", saved.getUsername());
        assertEquals("testuser@example.com", saved.getEmail());
        assertEquals("password123", saved.getPassword());
        assertEquals("Test User", saved.getName());
        assertEquals("My bio description", saved.getBio());
    }

    @Test
    void testFindByUsernameAndPassword() {
        Users user = Users.builder()
                .username("loginuser")
                .email("loginuser@example.com")
                .password("secretPass")
                .name("Login User")
                .build();

        userService.saveUser(user);

        // Positive Match
        Users found = userService.findByUsernameAndPassword("loginuser", "secretPass");
        assertNotNull(found);
        assertEquals("loginuser@example.com", found.getEmail());

        // Negative Match: wrong password
        Users wrongPass = userService.findByUsernameAndPassword("loginuser", "wrongPass");
        assertNull(wrongPass);

        // Negative Match: wrong username
        Users wrongUser = userService.findByUsernameAndPassword("nonexistent", "secretPass");
        assertNull(wrongUser);
    }
}
