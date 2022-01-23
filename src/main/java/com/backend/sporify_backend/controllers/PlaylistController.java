package com.backend.sporify_backend.controllers;

import com.backend.sporify_backend.models.*;
import com.backend.sporify_backend.payload.response.MessageResponse;
import com.backend.sporify_backend.repositories.PlaylistRepository;
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
public class PlaylistController {


    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PlaylistRepository playlistRepository;

    @Autowired
    private TrackRepository trackRepository;

    @PostMapping("/playlist")
    public ResponseEntity<?> createUserPlaylist(@RequestBody Playlist playlist,Authentication authentication){
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        String currentUserId = userDetails.getId();

        // find current authenticated user
        User user = userRepository.findById(currentUserId).orElseGet(User::new);
        // get current user's albums
        Set<Playlist> userPlaylists = user.getPlaylists();

        // if user's albums contains the album we want to insert
        // then show a message
        if(userPlaylists.contains(playlist)){
            // find album that we want to be deleted
            Playlist playlistFound = new Playlist();
            for(Playlist x : userPlaylists){
                if(x.getName().equals(playlist.getName())){
                    playlistFound = x;
                    break;
                }
            }

            return ResponseEntity.ok().body(new MessageResponse("Playlist with id " + playlistFound.getName() + " already exists!"));
        }

        // set tracks's owner
        playlist.setUserId(user);
        // create track and insert it inside collection
        playlistRepository.insert(playlist);
        userPlaylists.add(playlist);

        // update user's track list
        // with newly inserted track
        userRepository.save(user);

        return ResponseEntity.ok(new MessageResponse(
                "Successfully added!",
                playlist
        ));
    }

    @DeleteMapping("/playlist/{playlistId}")
    public ResponseEntity<?> deleteUserPlaylist(@PathVariable String playlistId, Authentication authentication){
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        String currentUserId = userDetails.getId();

        // find current authenticated user
        User user = userRepository.findById(currentUserId).orElseGet(User::new);
        // get current user's albums
        Set<Playlist> userPlaylists = user.getPlaylists();

        boolean playlistFound = false;
        Playlist playlistToBeDeleted = new Playlist();

        for(Playlist x: userPlaylists){
            if(x.getId().equals(playlistId)){
                playlistToBeDeleted = x;
                playlistFound = true;
                break;
            }
        }

        if(!playlistFound){
            return ResponseEntity.ok().body(new MessageResponse("Playlist with id " + playlistId + " cannot be found!"));
        }

        // delete playlist from collection
        playlistRepository.delete(playlistToBeDeleted);

        // update user's playlists list
        // with newly deleted playlist
        userRepository.save(user);


        return ResponseEntity.ok(new MessageResponse(
                "Playlist with id" +  playlistToBeDeleted.getId() + " has been successfully deleted "  + playlistToBeDeleted.getName() + "!",
                playlistToBeDeleted
        ));
    }

    @PostMapping("/playlist/{id}")
    public ResponseEntity<?> addTrackToPlaylist(@PathVariable String id,@RequestBody Track track, Authentication authentication){
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        String currentUserId = userDetails.getId();

        // find current authenticated user
        User user = userRepository.findById(currentUserId).orElseGet(User::new);
        // get current user's albums
        Set<Playlist> userPlaylists = user.getPlaylists();

        // if user's albums contains the album we want to insert
        // then show a message

        Playlist currentPlaylist = new Playlist();

        boolean playlistExists = false;
        for(Playlist x: userPlaylists){
            if(x.getId().equals(id)){
                currentPlaylist = x;
                playlistExists = true;
                break;
            }
        }

        if(!playlistExists){
            return ResponseEntity.ok().body(new MessageResponse("Playlist with id " + id + " cannot be found!"));
        }


        // check if track already exists in playlist
        if(currentPlaylist.getTracks().contains(track)){
            return ResponseEntity.ok().body(
                    new MessageResponse("Playlist with id " + id + " already contains " + track.getName() + "!"));
        }

        // set track's owner
        track.setUserId(user);

        // create track and insert it inside collection
        trackRepository.insert(track);

        currentPlaylist.getTracks().add(track);

        // update playlist
        playlistRepository.save(currentPlaylist);
        // update user's track list
        // with newly inserted track
        userRepository.save(user);

        return ResponseEntity.ok(new MessageResponse(
                "Successfully added!",
                track
        ));
    }

    @PostMapping("/playlist/track/{id}")
    public ResponseEntity<?> deleteTrackFromPlaylist(@PathVariable String id, @RequestBody Playlist playlist, Authentication authentication){
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        String currentUserId = userDetails.getId();

        // find current authenticated user
        User user = userRepository.findById(currentUserId).orElseGet(User::new);
        // get current user's albums
        Set<Playlist> userPlaylists = user.getPlaylists();

        // if user's albums contains the album we want to insert
        // then show a message
        Playlist currentPlaylist = new Playlist();

        boolean playlistExists = false;
        for(Playlist x: userPlaylists){
            if(x.getId().equals(playlist.getId())){
                currentPlaylist = x;
                playlistExists = true;
                break;
            }
        }

        if(!playlistExists){
            return ResponseEntity.ok().body(new MessageResponse("Playlist with id " + id + " cannot be found!"));
        }


        // check if playlist contains track
        boolean trackExists = false;
        Track trackTemp = new Track();
        for(Track x: currentPlaylist.getTracks()){
            if(x.getId().equals(id)){
                trackTemp = x;
                trackExists = true;
                break;
            }
        }

        if(!trackExists){
            return ResponseEntity.ok().body(
                    new MessageResponse("Track with id " + trackTemp.getId() + " cannot be found in playlist " + currentPlaylist.getName() +"!"));
        }

        // delete track from collection
        trackRepository.delete(trackTemp);

        // update playlist
        playlistRepository.save(currentPlaylist);
        // update user's track list
        // with newly inserted track
        userRepository.save(user);

        return ResponseEntity.ok(new MessageResponse(
                "Track with id" +  trackTemp.getId() + " has been successfully deleted from "  + currentPlaylist.getName() + "!",
                trackTemp
        ));
    }

    @GetMapping("/playlists")
    public Set<Playlist> getUserPlaylists(Authentication authentication){
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        String currentUseId = userDetails.getId();

        // finding user and returning his playlist set
        return userRepository.findById(currentUseId).orElseGet(User::new).getPlaylists();
    }

}
