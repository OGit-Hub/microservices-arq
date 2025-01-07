package com.library.web;

import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.github.javafaker.Faker;
import com.library.dto.BookDto;
import com.library.service.LibraryService;

import jakarta.annotation.PostConstruct;

@RestController
@RequestMapping("/library/")
public class Controller {
	
	private static Logger logger = LogManager.getLogger(Controller.class);
	
	@Autowired
	private LibraryService service;
	
	
	@PostConstruct
	private void initLibrary(){
		Faker faker = Faker.instance();
		IntStream.range(0, 10).forEach(value -> service.addBook(new BookDto(faker.book().title(), faker.book().author())));
	}
	
	@GetMapping()
	public ResponseEntity<List<BookDto>> getLibrary() {
		logger.info("[GET Request] -- Return current available library");
		return ResponseEntity.ok(service.getAvailableBooks());
	}
	
	@GetMapping("/search")
	public ResponseEntity<List<BookDto>> searchBook(@RequestParam Optional<String> title, @RequestParam Optional<String> author) {
		
		if(!title.isPresent() && !author.isPresent()) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		
		return ResponseEntity.ok(service.searchByTitleOrAuthor(title.orElseGet(() -> null), author.orElseGet(() -> null)));
	}
	
	@PostMapping("/addBook")
	public ResponseEntity<BookDto> addBook(@RequestBody BookDto dto) {
		return ResponseEntity.ok(service.addBook(dto));
	}
	
	
	@DeleteMapping("/removeBook")
	public ResponseEntity<BookDto> removeBook(@RequestBody BookDto dto) {
		return ResponseEntity.ok(service.removeBook(dto));
	}
	
	@PutMapping("/rentBook")
	public ResponseEntity<BookDto> rentBook(@RequestBody BookDto dto) {
		return ResponseEntity.ok(service.rentBook(dto));
	}
	
	@PutMapping("/returnBook")
	public ResponseEntity<BookDto> returnBook(@RequestBody BookDto dto) {
		return ResponseEntity.ok(service.returnBook(dto));
	}
	

}
