package com.consid.backend.controller;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
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
import com.consid.backend.repository.LibraryItemRepository;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/api/libraryitem")
public class LibraryItemController {

	@Autowired
	private LibraryItemRepository libraryItemRepo;

	@Autowired
	private CategoryRepository categoryRepo;

	@GetMapping
	public ResponseEntity<List<LibraryItem>> getAllLibraryItems() {
		List<LibraryItem> libraryItemsList = new ArrayList<LibraryItem>(libraryItemRepo.findAll());

		for (LibraryItem l : libraryItemsList) {
			Category temp = new Category();
			temp.setCategoryName(l.getCategory().getCategoryName());
			temp.setId(l.getCategory().getId());
			l.setCategory(temp);
		}
		return ResponseEntity.ok(libraryItemsList);
	}

	@GetMapping("/{id}")
	public ResponseEntity<LibraryItem> getLibraryItem(@PathVariable int id) {

		Optional<LibraryItem> optionalLibraryItem = libraryItemRepo.findById(id);
		if (!optionalLibraryItem.isPresent()) {
			return ResponseEntity.badRequest().build();
		}
		
		Category temp = new Category();
		temp.setCategoryName(optionalLibraryItem.get().getCategory().getCategoryName());
		temp.setId(optionalLibraryItem.get().getCategory().getId());
		optionalLibraryItem.get().setCategory(temp);

		return ResponseEntity.ok(optionalLibraryItem.get());
	}

	@PostMapping("/add")
	ResponseEntity<String> addLibraryItem(@Valid @RequestBody LibraryItem libraryItem) throws URISyntaxException {
		libraryItem.setId(0);
		Optional<Category> optionalCateogry = categoryRepo.findById(libraryItem.getCategory().getId());
		if (!optionalCateogry.isPresent()) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No such category in the database");
		} else if (!inputTypeValidation(libraryItem)) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Wrong in the library item values");
		}

		libraryItem = setBorrowable(libraryItem);
		libraryItem.setCategory(optionalCateogry.get());
		LibraryItem result = libraryItemRepo.save(libraryItem);
		return ResponseEntity.status(HttpStatus.CREATED)
				.body("The library item " + result.getTitle() + " has been successfully added");
	}

	@PutMapping("/{id}")
	public ResponseEntity<String> updateLibraryItem(@PathVariable Integer id,
			@Valid @RequestBody LibraryItem libraryItem) throws URISyntaxException {

		Optional<LibraryItem> optionalLibraryItem = libraryItemRepo.findById(id);
		Optional<Category> optionalCateogry = categoryRepo.findById(libraryItem.getCategory().getId());
		
		if (!optionalLibraryItem.isPresent()) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No such library item in the database");
		} else if (!optionalCateogry.isPresent()) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No such category in the database");
		} else if (!inputTypeValidation(libraryItem)) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Wrong in the library item values");
		}

		libraryItem = setBorrowable(libraryItem);
		libraryItem.setId(id);
		libraryItem.setCategory(optionalCateogry.get());
		libraryItemRepo.save(libraryItem);
		return ResponseEntity.status(HttpStatus.OK)
				.body("Library item with id: " + id + " has been successfuly updated");
	}

	@DeleteMapping("/{id}")
	ResponseEntity<String> deleteLibraryItem(@PathVariable int id) {

		Optional<LibraryItem> optionalLibraryItem = libraryItemRepo.findById(id);

		if (!optionalLibraryItem.isPresent()) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No such library item in the database");
		}
		libraryItemRepo.deleteById(id);
		return ResponseEntity.status(HttpStatus.OK)
				.body("The library item " + optionalLibraryItem.get().getTitle() + " has been successfully deleted");
	}

	/**
	 * A method validate LibraryItem values depends on it's type
	 */
	private boolean inputTypeValidation(LibraryItem input) {

		String author = input.getAuthor();
		int pages = input.getPages();
		int runTime = input.getRunTimeMinutes();

		switch (input.getType().trim().toLowerCase()) {
		case "book":
		case "reference book":
			if (author == null || author.trim().isEmpty() || pages <= 0 || runTime > 0) {
				return false;
			}
			break;
		case "dvd":
		case "audio book":
			if (author != null || pages > 0 || runTime <= 0) {
				return false;
			}
			break;
		}
		return true;
	}

	/**
	 * A method set isBorrowable value depending on the LibraryItem's type
	 */
	private LibraryItem setBorrowable(LibraryItem input) {
		if (!input.getType().trim().toLowerCase().equals("reference book")) {
			input.setBorrowable(true);
		} else {
			input.setBorrowable(false);
		}
		return input;
	}

}
