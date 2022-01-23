package com.backend.sporify_backend.repositories;

import com.backend.sporify_backend.models.Album;
import com.backend.sporify_backend.models.Artist;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ArtistRepository  extends MongoRepository<Artist, String> {
    Optional<Artist> findArtistById(String id);
    Optional<Artist> findArtistByName(String name);

}
