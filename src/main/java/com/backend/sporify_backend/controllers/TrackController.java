package com.backend.sporify_backend.controllers;


import com.backend.sporify_backend.models.Track;
import com.backend.sporify_backend.models.User;
import com.backend.sporify_backend.payload.response.MessageResponse;
import com.backend.sporify_backend.repositories.TrackRepository;
import com.backend.sporify_backend.repositories.UserRepository;
import com.backend.sporify_backend.security.services.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Set;


@RestController
@RequestMapping("/api/user")
@PreAuthorize("isAuthenticated()")
public class TrackController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TrackRepository trackRepository;

    @PostMapping("/track")
    public ResponseEntity<?> toggleFavoriteTrack(@RequestBody Track track, Authentication authentication){
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        String currentUserId = userDetails.getId();

        // find current authenticated user
        User user = userRepository.findById(currentUserId).orElseGet(User::new);
        // get current user's albums
        Set<Track> userTracks = user.getTracks();

        // if user's track contains the track we want to insert
        // then show a message
        if(userTracks.contains(track)){
            // find track that we want to be deleted
//            Track trackFound = trackRepository.findByNameAndArtist(track.getName(), track.getArtist()).orElseGet(Track::new);
            Track trackFound = new Track();
            for(Track x : userTracks){
                if(x.getName().equals(track.getName())){
                    trackFound = x;
                    break;
                }
            }

            // delete from albums collection
            trackRepository.delete(trackFound);

            // remove it from user's set
            userTracks.remove(trackFound);

            // update user's track list
            // with newly deleted track
            userRepository.save(user);

            return ResponseEntity.ok().body(new MessageResponse("Track with id " + trackFound.getId() + " has been successfully deleted!"));
        }

        // set tracks's owner
        track.setUserId(user);
        // create track and insert it inside collection
        trackRepository.insert(track);
        userTracks.add(track);

        // update user's track list
        // with newly inserted track
        userRepository.save(user);

        return ResponseEntity.ok(new MessageResponse(
                "Successfully added!",
                track
        ));
    }

    @GetMapping("/tracks")
    public Set<Track> getFavoriteTracks(Authentication authentication){
        // gets current userId from token from the authorization header
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        String currentUseId = userDetails.getId();

        // finding user and returning his track set
        return userRepository.findById(currentUseId).orElseGet(User::new).getTracks();
    }
}
