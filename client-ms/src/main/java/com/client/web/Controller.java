package com.client.web;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.client.domain.BookDto;
import com.client.feign.LibraryClient;

@RestController
@RequestMapping("/client/")

public class Controller {
	
	@Autowired
	LibraryClient libraryClient;
	
	@GetMapping("consultLibrary")
	public ResponseEntity<List<BookDto>> consultCurrentLibrary(){
		return libraryClient.getLibrary();
	}
	
	@PutMapping("/rentBook")
	public ResponseEntity<BookDto> rentBook(@RequestBody BookDto dto) {
		return libraryClient.rentBook(dto);
	}
	
	@PutMapping("/returnBook")
	public ResponseEntity<BookDto> returnBook(@RequestBody BookDto dto) {
		return libraryClient.returnBook(dto);
	}

}
