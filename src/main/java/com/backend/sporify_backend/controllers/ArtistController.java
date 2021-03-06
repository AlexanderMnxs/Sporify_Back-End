package com.backend.sporify_backend.controllers;


import com.backend.sporify_backend.models.Artist;
import com.backend.sporify_backend.models.Track;
import com.backend.sporify_backend.models.User;
import com.backend.sporify_backend.payload.response.MessageResponse;
import com.backend.sporify_backend.repositories.ArtistRepository;
import com.backend.sporify_backend.repositories.UserRepository;
import com.backend.sporify_backend.security.services.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

@RestController
@RequestMapping("/api/user")
@PreAuthorize("isAuthenticated()")
public class ArtistController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ArtistRepository artistRepository;

    @PostMapping("/artist")
    public ResponseEntity<?> toggleFavoriteArtist(@RequestBody Artist artist, Authentication authentication){
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        String currentUseId = userDetails.getId();

        // find current authenticated user
        User user = userRepository.findById(currentUseId).orElseGet(User::new);
        // get current user's albums
        Set<Artist> userArtists = user.getArtists();

        // if user's artists contains the artist we want to insert
        // then show a message
        if(userArtists.contains(artist)){
            // find track that we want to be deleted
//            Artist artistFound = artistRepository.findArtistByName(artist.getName()).orElseGet(Artist::new);
            Artist artistFound = new Artist();
            for(Artist x : userArtists){
                if(x.getName().equals(artist.getName())){
                    artistFound = x;
                    break;
                }
            }

            // delete from albums collection
            artistRepository.delete(artistFound);

            // remove it from user's set
            userArtists.remove(artistFound);

            // update user's track list
            // with newly deleted track
            userRepository.save(user);

            return ResponseEntity.ok().body(new MessageResponse("Artist with id " + artistFound.getId() + " has been successfully deleted!"));
        }

        // set track's owner
        artist.setUserId(user);
        // create track and insert it inside collection
        artistRepository.insert(artist);
        userArtists.add(artist);

        // update user's track list
        // with newly inserted track
        userRepository.save(user);


        return ResponseEntity.ok(new MessageResponse(
                "Successfully added!",
                artist
        ));

    }

    @GetMapping("/artists")
    public Set<Artist> getFavoriteArtists(Authentication authentication){
        // gets current userId from token from the authorization header
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        String currentUseId = userDetails.getId();

        // finding user and returning his track set
        return userRepository.findById(currentUseId).orElseGet(User::new).getArtists();
    }

    @GetMapping("/artistsMood")
    public ResponseEntity<?> getTracksByMood(Authentication authentication){
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        String currentUserId = userDetails.getId();

        User user = userRepository.findById(currentUserId).orElseGet(User::new);

        Set<Artist> userTracks = user.getArtists();

        List<Artist> tracksByMood = new ArrayList<>();
        for(Artist x: userTracks){
            if(x.getMood().toLowerCase(Locale.ROOT).equals(user.getMood())){
                tracksByMood.add(x);
            }
        }

        return ResponseEntity.ok(tracksByMood);
    }
}
