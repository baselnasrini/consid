package com.consid.backend.controller;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.consid.backend.model.Category;
import com.consid.backend.model.LibraryItem;
import com.consid.backend.repository.CategoryRepository;

@RestController
@RequestMapping("/api/category")
public class CategoryController {

	@Autowired
	private CategoryRepository categoryRepo;

	@GetMapping
	public ResponseEntity<List<Category>> getAllCategories() throws URISyntaxException {
		List<Category> categoriesList = new ArrayList <Category>(categoryRepo.findAll());
		for (Category c : categoriesList) {
			List<LibraryItem> libraryItemsList = new ArrayList <LibraryItem>(c.getLibraryItems());
			for (LibraryItem l : libraryItemsList) {
				Category tempCat = new Category();
				tempCat.setCategoryName(l.getCategory().getCategoryName());
				tempCat.setId(l.getCategory().getId());
				l.setCategory(tempCat);
			}
		}
		return ResponseEntity.ok(categoriesList);
		}

	@GetMapping("/{id}")
	public ResponseEntity<Category> getByCategoryId(@PathVariable Integer id) throws URISyntaxException {
		Optional<Category> optionalCategory = categoryRepo.findById(id);
		if (!optionalCategory.isPresent()) {
			return ResponseEntity.badRequest().build();
		}
		List<LibraryItem> libraryItemsList = new ArrayList <LibraryItem>(optionalCategory.get().getLibraryItems());
		for (LibraryItem l : libraryItemsList) {
			Category tempCat = new Category();
			tempCat.setCategoryName(l.getCategory().getCategoryName());
			tempCat.setId(l.getCategory().getId());
			l.setCategory(tempCat);
		}
		return ResponseEntity.ok(optionalCategory.get());
	}

	@PostMapping("/add")
	ResponseEntity<String> addCategory(@Valid @RequestBody Category category) throws URISyntaxException {
		category.setId(0);
		Category result = categoryRepo.save(category);
		return ResponseEntity.status(HttpStatus.CREATED)
				.body("Category " + result.getCategoryName() + " has be successfuly added");
	}

	@PutMapping("/{id}")
	public ResponseEntity<String> updateCategory(@PathVariable Integer id, @Valid @RequestBody Category category)
			throws URISyntaxException {
		Optional<Category> optionalCategory = categoryRepo.findById(id);
		if (!optionalCategory.isPresent()) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No such category in the database");
		}

		optionalCategory.get().setCategoryName(category.getCategoryName());
		categoryRepo.save(optionalCategory.get());

		return ResponseEntity.status(HttpStatus.OK).body("Category with id: " + id + " has been successfuly updated");
	}

	@DeleteMapping("/{id}")
	ResponseEntity<String> deleteCateogry(@PathVariable int id) throws URISyntaxException {
		Optional<Category> optionalCategory = categoryRepo.findById(id);

		if (!optionalCategory.isPresent()) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No such category in the database");
		} else if (optionalCategory.get().getLibraryItems().size() != 0) { // category has a reference in the library
																			// items
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body("Requested category cannot be deleted until the referenced library items are removed");
		}
		categoryRepo.deleteById(id);
		return ResponseEntity.status(HttpStatus.OK)
				.body("Category with the id: " + id + " has been successfully deleted.");
	}
}
