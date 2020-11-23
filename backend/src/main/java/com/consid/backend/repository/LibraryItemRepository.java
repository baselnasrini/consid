package com.consid.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.consid.backend.model.LibraryItem;

public interface LibraryItemRepository extends JpaRepository<LibraryItem,Integer> {

}
