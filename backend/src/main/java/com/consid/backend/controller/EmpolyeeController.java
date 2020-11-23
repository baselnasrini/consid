package com.consid.backend.controller;

import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.consid.backend.model.Category;
import com.consid.backend.model.Employee;
import com.consid.backend.model.LibraryItem;
import com.consid.backend.repository.EmployeeRepository;
import com.google.gson.Gson;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/api/employee")
public class EmpolyeeController {
	
	@Autowired
	private EmployeeRepository employeeRepo;
	
	@GetMapping
	public ResponseEntity<List<Employee>>  getAllEmployees(){
		List<Employee> employeesList = employeeRepo.findAll();
		return ResponseEntity.ok(employeesList);
	}
	
	/*@GetMapping("/{id}")
	public ResponseEntity<Employee> getEmployee(@PathVariable int id) {

		Optional<Employee> optionalEmployee = employeeRepo.findById(id);
		if (!optionalEmployee.isPresent()) {
			return ResponseEntity.badRequest().build();
		}
		return ResponseEntity.ok(optionalEmployee.get());
	}*/
	
	@PostMapping("/add")
	ResponseEntity<String> addEmployee(@Valid @RequestBody Employee employee) throws URISyntaxException{
		System.out.println(employee);
		Employee result = employeeRepo.save(employee);
		System.out.println(result);
		return ResponseEntity.status(HttpStatus.CREATED)
				.body("Employee: " + result.getFirstName() + " " +  result.getLastName() + " has be successfuly added");
	}
	
	@GetMapping("/{lastname}")
	ResponseEntity<List<Employee>> getByLastname(@PathVariable String lastname) throws URISyntaxException{
		Optional<List<Employee>> result = Optional.of(employeeRepo.findByManagerTrue());
		if (!result.isPresent()) {
			return ResponseEntity.badRequest().build();
		}
		return ResponseEntity.ok(result.get());
	}
	
	@PutMapping("/{id}")
	public ResponseEntity<String> updateEmployee(@PathVariable Integer id, @Valid @RequestBody Employee employee)
			throws URISyntaxException {
		Optional<Employee> optionalEmployee = employeeRepo.findById(id);
		if (!optionalEmployee.isPresent()) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No such employee in the database");
		}

		employeeRepo.save(optionalEmployee.get());

		return ResponseEntity.status(HttpStatus.OK).body("Employee : " + optionalEmployee.get().getFirstName() + " " + optionalEmployee.get().getLastName() + " has been successfuly updated");
	}
	
	  @ExceptionHandler(Exception.class)
	    public ResponseEntity<String> handleException(Exception e) {
		  System.out.print(e.toString());
		  	if (e.getClass() == MethodArgumentNotValidException.class) {
		  		return ResponseEntity
		                .status(HttpStatus.BAD_REQUEST)
		                .body("Bad Request Error: 400 \n Box arguments are not valid.");
		  	} else {
		  		return ResponseEntity
		                .status(HttpStatus.INTERNAL_SERVER_ERROR)
		                .body("Server Error: 500");
		  	}
	    }      
	
}
