package com.backend.sporify_backend.controllers;


import com.backend.sporify_backend.models.User;
import com.backend.sporify_backend.payload.request.UserPrefsRequest;
import com.backend.sporify_backend.payload.response.MessageResponse;
import com.backend.sporify_backend.repositories.TrackRepository;
import com.backend.sporify_backend.repositories.UserRepository;
import com.backend.sporify_backend.security.services.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * Ο controller που είναι υπεύθυνος για τα requests που γίνονται για να επιστρέφει τον χρήστη βάσει id
 * να δημιουργεί ένα favorite album και να το βάζει στο χρήστη που είναι συνδεδεμένος μέσω του token του
 * να επιστρέφει τα favorite albums του χρήστη που είναι συνδεδεμένος μέσω του token του
 * Το home path που τρέχει ο server είναι το http://localhost:8081/
 * Ο συγκεκριμένος controller είναι υπεύθυνος για τα request στο path http://localhost:8081/api/user
 *
 *
 * @PreAuthorize("isAuthenticated()") Απαιτεί απο την χρήστη να είναι authenticated δηλαδή loggedin
 */

@RestController
@RequestMapping("/api/user")
@PreAuthorize("isAuthenticated()")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TrackRepository trackRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    @GetMapping("/me")
    public User getUser(Authentication authentication){
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        String currentUserId = userDetails.getId();


        return userRepository.findById(currentUserId).orElseGet(User::new);
    }

    @PutMapping("/me")
    public ResponseEntity<?> updateUser(Authentication authentication, @RequestBody UserPrefsRequest userPrefs){
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        String currentUserId = userDetails.getId();

        User user = userRepository.findById(currentUserId).orElseGet(User::new);

        user.setGenre(userPrefs.getGenre());
        user.setMood(userPrefs.getMood());

        userRepository.save(user);

        return ResponseEntity.ok(new MessageResponse(
                "User prefs has been updated successfully",
                user
        ));
    }



}
