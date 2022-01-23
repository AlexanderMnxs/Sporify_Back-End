package com.backend.sporify_backend.repositories;


import com.backend.sporify_backend.models.Playlist;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlaylistRepository extends MongoRepository<Playlist, String> {


}
