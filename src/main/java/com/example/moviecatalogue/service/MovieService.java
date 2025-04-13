package com.example.moviecatalogue.service;

import com.example.moviecatalogue.model.Movie;
import com.example.moviecatalogue.repository.FavoriteMovieRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class MovieService {

    @Value("${tmdb.api.key}")
    private String apiKey;

    @Value("${tmdb.api.url}")
    private String apiUrl;

    private final RestTemplate restTemplate;
    private final FavoriteMovieRepository favoriteRepo;

    public MovieService(RestTemplateBuilder builder, FavoriteMovieRepository favoriteRepo) {
        this.restTemplate = builder.build();
        this.favoriteRepo = favoriteRepo;
    }

    public List<Movie> getTrendingMovies() {
        String url = apiUrl + "/trending/movie/week?api_key=" + apiKey;
        ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
        List<Map<String, Object>> results = (List<Map<String, Object>>) response.getBody().get("results");

        return results.stream().map(this::mapToMovie).collect(Collectors.toList());
    }

    // ✅ New search method
    public List<Movie> searchMovies(String query) {
        String url = apiUrl + "/search/movie?api_key=" + apiKey + "&query=" + query;
        ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
        List<Map<String, Object>> results = (List<Map<String, Object>>) response.getBody().get("results");
        return results.stream().map(this::mapToMovie).collect(Collectors.toList());
    }

    public Movie getMovieDetails(Long id) {
        String url = apiUrl + "/movie/" + id + "?api_key=" + apiKey;
        Map<String, Object> data = restTemplate.getForObject(url, Map.class);
        return mapToMovie(data);
    }

    public void addFavorite(Movie movie) {
        favoriteRepo.save(movie);
    }

    public void removeFavorite(Long id) {
        favoriteRepo.deleteById(id);
    }

    public List<Movie> getAllFavorites() {
        return favoriteRepo.findAll();
    }

    public boolean isFavorite(Long id) {
        return favoriteRepo.existsById(id);
    }

    private Movie mapToMovie(Map<String, Object> data) {
        Movie movie = new Movie();
        movie.setId(((Number) data.get("id")).longValue());
        movie.setTitle((String) data.get("title"));
        movie.setOverview((String) data.get("overview"));
        movie.setReleaseDate((String) data.get("release_date"));
        movie.setPosterPath((String) data.get("poster_path"));
        movie.setVoteAverage(((Number) data.get("vote_average")).doubleValue());
        return movie;
    }
}
