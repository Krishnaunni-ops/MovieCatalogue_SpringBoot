package com.example.moviecatalogue.controller;

import com.example.moviecatalogue.model.Movie;
import com.example.moviecatalogue.service.MovieService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class MovieController {

    private final MovieService movieService;

    public MovieController(MovieService movieService) {
        this.movieService = movieService;
    }

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("movies", movieService.getTrendingMovies());
        return "index";
    }

    @GetMapping("/search")
    public String searchMovies(@RequestParam("query") String query, Model model) {
        List<Movie> searchResults = movieService.searchMovies(query);
        model.addAttribute("movies", searchResults);
        return "index";
    }

    @GetMapping("/movie/{id}")
    public String movieDetails(@PathVariable Long id, Model model) {
        Movie movie = movieService.getMovieDetails(id);
        model.addAttribute("movie", movie);
        model.addAttribute("isFavorite", movieService.isFavorite(id));
        return "detail";
    }

    @PostMapping("/favorite/add")
    public String addFavorite(@ModelAttribute Movie movie) {
        movieService.addFavorite(movie);
        return "redirect:/favorites";
    }

    @GetMapping("/favorites")
    public String viewFavorites(Model model) {
        model.addAttribute("favorites", movieService.getAllFavorites());
        return "favorites";
    }

    @PostMapping("/favorite/remove/{id}")
    public String removeFavorite(@PathVariable Long id) {
        movieService.removeFavorite(id);
        return "redirect:/favorites";
    }
}