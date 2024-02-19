package com.devsuperior.dsmovie.services;

import static org.mockito.ArgumentMatchers.any;

import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.devsuperior.dsmovie.dto.MovieDTO;
import com.devsuperior.dsmovie.dto.ScoreDTO;
import com.devsuperior.dsmovie.entities.MovieEntity;
import com.devsuperior.dsmovie.entities.ScoreEntity;
import com.devsuperior.dsmovie.entities.UserEntity;
import com.devsuperior.dsmovie.repositories.MovieRepository;
import com.devsuperior.dsmovie.repositories.ScoreRepository;
import com.devsuperior.dsmovie.services.exceptions.ResourceNotFoundException;
import com.devsuperior.dsmovie.tests.MovieFactory;
import com.devsuperior.dsmovie.tests.ScoreFactory;
import com.devsuperior.dsmovie.tests.UserFactory;

@ExtendWith(SpringExtension.class)
public class ScoreServiceTests {

	@InjectMocks
	private ScoreService service;

	@Mock
	private UserService userService;

	@Mock
	private MovieRepository movieRepository;

	@Mock
	private ScoreRepository scoreRepository;

	private UserEntity user;
	private ScoreEntity scoreEntity;
	private MovieEntity movie;
	

	private ScoreDTO scoreDTO;
	private Long existingMovieId;
	private Long nonExistingMovieId;

	@BeforeEach
	void setUp() {
		
		user = UserFactory.createUserEntity();
		scoreEntity = ScoreFactory.createScoreEntity();
		scoreDTO = ScoreFactory.createScoreDTO();
		movie = MovieFactory.createMovieEntity();
		
		existingMovieId = 1L;
		nonExistingMovieId = 2L;
		
		ScoreEntity score = new ScoreEntity();
		score.setMovie(movie);
		score.setUser(user);
		score.setValue(5.2);
		
		movie.getScores().add(score);
	
		
		Mockito.when(userService.authenticated()).thenReturn(user);
		
		Mockito.when(movieRepository.findById(existingMovieId)).thenReturn(Optional.of(movie));
		Mockito.when(scoreRepository.saveAndFlush(any())).thenReturn(scoreEntity);
		Mockito.when(movieRepository.save(any())).thenReturn(movie);
		
		Mockito.when(movieRepository.findById(nonExistingMovieId)).thenReturn(Optional.empty());		
		
	}
		

	@Test
	public void saveScoreShouldReturnMovieDTO() {
		
		final MovieDTO result = service.saveScore(scoreDTO);
		 
		 Assertions.assertNotNull(result);		
	}

	
	  @Test public void
	  saveScoreShouldThrowResourceNotFoundExceptionWhenNonExistingMovieId() {
	  
		  MovieEntity movie = MovieFactory.createMovieEntity();
			movie.setId(nonExistingMovieId);
			UserEntity user = UserFactory.createUserEntity();
			ScoreEntity score = new ScoreEntity();
			
			score.setMovie(movie);
			score.setUser(user);
			score.setValue(4.5);
			movie.getScores().add(score);
			
			scoreDTO = new ScoreDTO(score);
			
			Assertions.assertThrows(ResourceNotFoundException.class, () -> {
				
				@SuppressWarnings("unused")
				MovieDTO result = service.saveScore(scoreDTO);
				
			});		
	  
	  }
	 
}
