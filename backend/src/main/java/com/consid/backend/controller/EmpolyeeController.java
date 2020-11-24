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
	public ResponseEntity<List<Employee>> getAllEmployees() {
		List<Employee> employeesList = employeeRepo.findAll();
		return ResponseEntity.ok(employeesList);
	}

	/*
	 * @GetMapping("/{id}") public ResponseEntity<Employee>
	 * getEmployee(@PathVariable int id) {
	 * 
	 * Optional<Employee> optionalEmployee = employeeRepo.findById(id); if
	 * (!optionalEmployee.isPresent()) { return ResponseEntity.badRequest().build();
	 * } return ResponseEntity.ok(optionalEmployee.get()); }
	 */

	@PostMapping("/add")
	ResponseEntity<String> addEmployee(@Valid @RequestBody Employee employee) throws URISyntaxException {

		if (!checkInput(employee)) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Wrong in the given values");
		}
		
		employee.setSalary(SalaryCalculator.calSalary(employee));
		
		Employee result = employeeRepo.save(employee);
		String role = getRole(result);
		System.out.println(result);
		return ResponseEntity.status(HttpStatus.CREATED)
				.body(role + ": " + result.getFirstName() + " " + result.getLastName() + " has been successfuly added");
	}

	@GetMapping("/{lastname}")
	ResponseEntity<List<Employee>> getByLastname(@PathVariable String lastname) throws URISyntaxException {
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
		employee.setSalary(SalaryCalculator.calSalary(employee));
		if (!optionalEmployee.isPresent()) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No such employee in the database");
		}

		employeeRepo.save(optionalEmployee.get());

		return ResponseEntity.status(HttpStatus.OK).body("Employee : " + optionalEmployee.get().getFirstName() + " "
				+ optionalEmployee.get().getLastName() + " has been successfuly updated");
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<String> handleException(Exception e) {
		System.out.print(e.toString());
		if (e.getClass() == MethodArgumentNotValidException.class) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body("Bad Request Error: 400 \n Box arguments are not valid.");
		} else {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Server Error: 500");
		}
	}

	/*
	 * A method check if the input values are accepted
	 */
	private boolean checkInput(Employee employee) {
		int managerId = employee.getManagerId();
		Employee CEO = employeeRepo.findByCeoTrue();

		/*
		 * isManager and isCEO cannot be true at the same time, no one can be CEO's
		 * manager, no other CEO available
		 */
		if (employee.isCeo() && (employee.isManager() || managerId != 0 || CEO != null))
			return false;
		else if (employee.getSalaryRank() < 1 || employee.getSalaryRank() > 10) // salary rank has to be between 1-10
			return false;
		else if (getRole(employee).equals("Employee") && employee.getManagerId() == 0) // regular employee should has a
																						// manager
			return false;

		if (managerId != 0 && !isManagerAccepted(employee)) {
			return false;
		}
		return true;
	}

	private String getRole(Employee employee) {
		if (employee.isCeo())
			return "CEO";
		else if (employee.isManager()) {
			return "Manager";
		} else if (!employee.isManager() && !employee.isCeo()) {
			return "Employee";
		}
		return "";
	}

	private boolean isManagerAccepted(Employee employee) {

		Optional<Employee> optionalManager = employeeRepo.findById(employee.getManagerId());
		Employee CEO = employeeRepo.findByCeoTrue();

		if (!optionalManager.isPresent()) // check that managerId given is true
			return false;

		Employee manager = optionalManager.get();
		String employeeRole = getRole(employee);
		String managerRole = getRole(manager);

		if (managerRole.equals("Employee")) // check that the manager role is not a regular employee
			return false;

		if (employeeRole == "Employee") {
			if (CEO != null && CEO.equals(manager)) // “regular” employee cannot be managed by CEO (only by managers)
				return false;
		} else if (employeeRole == "Manager") {
			if (employee.equals(manager)) // manager can be managed by other managers or by CEO (cannot be a manager of
											// himself)
				return false;
		}
		return true;
	}

}
