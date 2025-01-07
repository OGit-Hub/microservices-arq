package com.library.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.library.dto.BookDto;
import com.library.entity.BookEntity;
import com.library.exception.BookAlreadyRentedException;
import com.library.exception.BookDoesNotExistsException;
import com.library.exception.BookIsNotRentedException;
import com.library.repository.BookRepository;

@Service
public class LibraryService {
	
	@Autowired
	BookRepository repository;
	
	@Autowired
    private ModelMapper modelMapper;
	
	public List<BookDto> getAvailableBooks() {
		List<BookEntity> listEntity = repository.findAll();
		List<BookDto> listDto = listEntity.stream()
				.filter(bookFilter -> !bookFilter.isRented())
				.map(entity -> convertToDto(entity)).collect(Collectors.toList());
		return listDto;
		
	}
	
	public List<BookDto> searchByTitleOrAuthor(String title, String author) {
		List<BookEntity> listEntity = repository.findByTitleOrAuthor(title, author);
		List<BookDto> listDto = listEntity.stream()
				.map(entity -> convertToDto(entity)).collect(Collectors.toList());
		return listDto;
		
	}
	
	public BookDto addBook(BookDto book) {
		BookEntity entity = new BookEntity(book.getTitle(), book.getAuthor());
		return convertToDto(repository.save(entity));
		
	}
	
	public BookDto removeBook(BookDto book) {
		BookEntity entity = getBookById(book.getId());
		repository.deleteById(entity.getId());
		
		return convertToDto(entity);
	}
	
	public BookDto rentBook(BookDto book) {
		BookEntity entity = getBookById(book.getId());

		if(entity.isRented()) {
			throw new BookAlreadyRentedException(String.format("[%d] %s", book.getId(), book.getTitle()));
		}
		
		entity.setRented(true);
		
		return convertToDto(repository.save(entity));
		
	}
	
	public BookDto returnBook(BookDto book) {
		BookEntity entity = getBookById(book.getId());

		if(!entity.isRented()) {
			throw new BookIsNotRentedException(String.format("[%d] %s", book.getId(), book.getTitle()));
		}
		
		entity.setRented(false);
		entity.setClientId(0);
		
		return convertToDto(repository.save(entity));
		
	}
	
	private BookEntity getBookById(int id) {
		Optional<BookEntity> entity = repository.findById(id);
		return entity.orElseThrow(() -> new BookDoesNotExistsException(String.format("[%d]", id)));
	}
	
	private BookDto convertToDto(BookEntity entity) {
		BookDto dto = modelMapper.map(entity, BookDto.class);
	    return dto;
	}
	

}
