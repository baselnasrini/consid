package com.consid.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.consid.backend.model.Category;

public interface CategoryRepository extends JpaRepository<Category,Integer>{

}
