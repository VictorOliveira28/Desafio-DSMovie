package com.devsuperior.dsmovie.services;

import static org.mockito.ArgumentMatchers.any;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.devsuperior.dsmovie.dto.MovieDTO;
import com.devsuperior.dsmovie.entities.MovieEntity;
import com.devsuperior.dsmovie.repositories.MovieRepository;
import com.devsuperior.dsmovie.services.exceptions.DatabaseException;
import com.devsuperior.dsmovie.services.exceptions.ResourceNotFoundException;
import com.devsuperior.dsmovie.tests.MovieFactory;

import jakarta.persistence.EntityNotFoundException;

@ExtendWith(SpringExtension.class)
public class MovieServiceTests {

	@InjectMocks
	private MovieService service;

	@Mock
	private MovieRepository repository;

	private PageImpl<MovieEntity> page;
	private Pageable pageable;
	
	private MovieEntity movie;
	private MovieDTO movieDTO;
	private String existingTitle;
	private Long existingId;
	private Long nonExistingId;
	private Long dependentId;
	

	@BeforeEach
	void setUp() throws Exception {

		movie = MovieFactory.createMovieEntity();
		movieDTO = MovieFactory.createMovieDTO();
		pageable = PageRequest.of(0, 10);
		page = new PageImpl<>(List.of(movie));
		
		existingTitle = movie.getTitle();
		existingId = 1L;
		nonExistingId = 2L;
		dependentId = 3L;		

		Mockito.when(repository.searchByTitle(existingTitle, pageable)).thenReturn(page);

		Mockito.when(repository.findById(existingId)).thenReturn(Optional.of(movie));
		Mockito.when(repository.findById(nonExistingId)).thenReturn(Optional.empty());
		
		Mockito.when(repository.save(any())).thenReturn(movie);
		
		Mockito.when(repository.getReferenceById(existingId)).thenReturn(movie);
		Mockito.when(repository.getReferenceById(nonExistingId)).thenThrow(EntityNotFoundException.class);
		
		Mockito.doThrow(ResourceNotFoundException.class).when(repository).deleteById(nonExistingId);
		Mockito.doThrow(DataIntegrityViolationException.class).when(repository).deleteById(dependentId);
		
		Mockito.when(repository.existsById(existingId)).thenReturn(true);
		Mockito.when(repository.existsById(nonExistingId)).thenReturn(false);
		Mockito.when(repository.existsById(dependentId)).thenReturn(true);
	}

	@Test
	public void findAllShouldReturnPagedMovieDTO() {

		Page<MovieDTO> result = service.findAll(movie.getTitle(), pageable);

		Assertions.assertNotNull(result);
		Mockito.verify(repository, Mockito.times(1)).searchByTitle(movie.getTitle(), pageable);

	}

	@Test
	public void findByIdShouldReturnMovieDTOWhenIdExists() {

		MovieDTO dto = service.findById(existingId);

		Assertions.assertNotNull(dto);
		Mockito.verify(repository, Mockito.times(1)).findById(existingId);

	}

	@Test
	public void findByIdShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {

		Assertions.assertThrows(ResourceNotFoundException.class, () -> {
			service.findById(nonExistingId);
		});

		Mockito.verify(repository, Mockito.times(1)).findById(nonExistingId);
	}

	
	  @Test
	  public void insertShouldReturnMovieDTO() { 
		  
		  MovieDTO result = service.insert(movieDTO);
		  
		  Assertions.assertNotNull(result);
		  Assertions.assertEquals(result.getId(), movie.getId());
		  Assertions.assertEquals(result.getTitle(), movie.getTitle());
		  
	  }
	  
	  @Test public void updateShouldReturnMovieDTOWhenIdExists() {
		  
		  MovieDTO result = service.update(existingId, movieDTO);
		  
		  Assertions.assertNotNull(result);
		  
		  Mockito.verify(repository, Mockito.times(1)).save(movie);
		  
	   }
	  
	  @Test public void updateShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {
		  
		  Assertions.assertThrows(ResourceNotFoundException.class, () -> {
			  
			  service.update(nonExistingId, movieDTO);
		  });
		  
		  Mockito.verify(repository, Mockito.times(0)).save(movie);
		  
	   }
	  
	  @Test 
	  public void deleteShouldDoNothingWhenIdExists() {
		  
		  Assertions.assertDoesNotThrow(() -> {
			  service.delete(existingId);
		  });
	   }
	  
	  @Test public void
	  deleteShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {
		  
		  Assertions.assertThrows(ResourceNotFoundException.class, () -> {
			  
			  service.delete(nonExistingId);
		  });
		  
	   }
	  
	  @Test 
	  public void deleteShouldThrowDatabaseExceptionWhenDependentId() {
		  
		  Assertions.assertThrows(DatabaseException.class, () -> {
			  
			  service.delete(dependentId);
		  });		  
		  
	   }
	 
}
