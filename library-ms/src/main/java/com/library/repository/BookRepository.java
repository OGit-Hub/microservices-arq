package com.library.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.library.entity.BookEntity;


@Repository
public interface BookRepository extends JpaRepository<BookEntity, Integer> {
	
	@Query("SELECT b FROM BookEntity b WHERE isRented = false " +
	         "and (:title is null or b.title = :title) " +
	         "and (:author is null or b.author = :author) ")
	public List<BookEntity> findByTitleOrAuthor(@Param("title") String title, @Param("author") String author);
}
