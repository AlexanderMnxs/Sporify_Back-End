package com.backend.sporify_backend.models;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Reference;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Objects;
import java.util.Set;

@Data
@Document(value = "playlists")
@NoArgsConstructor
public class Playlist {

    @Id
    private String id;

    private String name;

    @Reference
    private User userId;

    @Reference
    private Set<Track> tracks;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Playlist playlist = (Playlist) o;
        return name.equals(playlist.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
