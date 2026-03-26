package com.streaming.movieplatform.repository;

import com.streaming.movieplatform.entity.Country;
import com.streaming.movieplatform.entity.Genre;
import com.streaming.movieplatform.entity.Movie;
import com.streaming.movieplatform.enums.AccessLevel;
import com.streaming.movieplatform.enums.MovieType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface MovieRepository extends JpaRepository<Movie, Long> {

    @EntityGraph(attributePaths = {"genres", "actors", "directors", "country", "episodes"})
    Optional<Movie> findWithDetailsById(Long id);

    @EntityGraph(attributePaths = {"genres", "country"})
    @Query("""
            select distinct m from Movie m
            left join m.genres g
            where m.active = true
              and (:keyword is null or lower(m.title) like lower(concat('%', :keyword, '%'))
                   or lower(m.originalTitle) like lower(concat('%', :keyword, '%')))
              and (:genreId is null or g.id = :genreId)
              and (:countryId is null or m.country.id = :countryId)
              and (:releaseYear is null or m.releaseYear = :releaseYear)
              and (:accessLevel is null or m.accessLevel = :accessLevel)
              and (:movieType is null or m.movieType = :movieType)
            """)
    Page<Movie> searchMovies(@Param("keyword") String keyword,
                             @Param("genreId") Long genreId,
                             @Param("countryId") Long countryId,
                             @Param("releaseYear") Integer releaseYear,
                             @Param("accessLevel") AccessLevel accessLevel,
                             @Param("movieType") MovieType movieType,
                             Pageable pageable);

    @EntityGraph(attributePaths = {"genres", "country"})
    List<Movie> findTop8ByFeaturedTrueAndActiveTrueOrderByCreatedAtDesc();

    @EntityGraph(attributePaths = {"genres", "country"})
    List<Movie> findTop12ByActiveTrueOrderByCreatedAtDesc();

    @EntityGraph(attributePaths = {"genres", "country"})
    List<Movie> findTop12ByActiveTrueOrderByViewCountDesc();

    @EntityGraph(attributePaths = {"genres", "country"})
    List<Movie> findTop12ByAccessLevelAndActiveTrueOrderByCreatedAtDesc(AccessLevel accessLevel);

    @EntityGraph(attributePaths = {"genres", "country"})
    @Query("""
            select distinct m from Movie m
            left join m.genres g
            where m.active = true and m.id <> :movieId and (m.country = :country or g in :genres)
            order by m.averageRating desc, m.viewCount desc
            """)
    List<Movie> findRelatedMovies(@Param("movieId") Long movieId,
                                  @Param("country") Country country,
                                  @Param("genres") Collection<Genre> genres,
                                  Pageable pageable);

    Optional<Movie> findBySlug(String slug);
}
