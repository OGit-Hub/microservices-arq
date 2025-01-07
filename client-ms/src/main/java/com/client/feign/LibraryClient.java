package com.client.feign;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.client.domain.BookDto;


@FeignClient("library-ms")
public interface LibraryClient {
	
	@GetMapping("/library/")
	public ResponseEntity<List<BookDto>> getLibrary();
	
	@PutMapping("/library/rentBook")
	public ResponseEntity<BookDto> rentBook(@RequestBody BookDto dto);
	
	@PutMapping("/library/returnBook")
	public ResponseEntity<BookDto> returnBook(@RequestBody BookDto dto);

}
