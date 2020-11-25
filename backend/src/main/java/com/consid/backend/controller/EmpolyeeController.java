package com.consid.backend.controller;

import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CancellationException;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.consid.backend.model.Employee;
import com.consid.backend.repository.EmployeeRepository;

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

	@GetMapping("/{id}")
	public ResponseEntity<Employee> getEmployee(@PathVariable int id) {

		Optional<Employee> optionalEmployee = employeeRepo.findById(id);
		if (!optionalEmployee.isPresent()) {
			throw new CancellationException("No such employee in the database");
		}
		return ResponseEntity.ok(optionalEmployee.get());
	}

	@PostMapping("/add")
	ResponseEntity<String> addEmployee(@Valid @RequestBody Employee employee) throws URISyntaxException {
		
		employee.setId(0); // ignore id coming from the client
		
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

	@PutMapping("/{id}")
	public ResponseEntity<String> updateEmployee(@PathVariable Integer id, @Valid @RequestBody Employee employee)
			throws URISyntaxException {
		Optional<Employee> optionalEmployee = employeeRepo.findById(id);

		if (!optionalEmployee.isPresent()) {
			throw new CancellationException("No such employee in the database");
		}

		employee.setId(optionalEmployee.get().getId()); // ignore the Id coming in the body
		String newRole = getRole(employee);
		String oldRole = getRole(optionalEmployee.get());

		if (!oldRole.equals(newRole) && !checkRoleChange(employee, oldRole, newRole)) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Role changing doesn't work");
		}

		if (!checkInput(employee)) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Wrong in the given values");
		}

		if (newRole.equals("CEO"))
			employee.setManagerId(0);

		employee.setSalary(SalaryCalculator.calSalary(employee));

		employeeRepo.save(employee);

		return ResponseEntity.status(HttpStatus.OK)
				.body(employee.getFirstName() + " " + employee.getLastName() + " has been successfuly updated");
	}
	
	@DeleteMapping("/{id}")
	ResponseEntity<String> deleteEmployee(@PathVariable int id) throws URISyntaxException {
		Optional<Employee> optionalEmployee = employeeRepo.findById(id);

		if (!optionalEmployee.isPresent()) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No such employee in the database");
		}
		
		Employee employee = optionalEmployee.get();
		String role = getRole(employee);
		boolean isManagerforManager = employeeRepo.isManagerforManager(employee.getId());
		boolean isManagerforRegEmployees = employeeRepo.isManagerforRegEmployees(employee.getId());

		if (role.equals("CEO") && isManagerforManager) {
			throw new CancellationException("CEO cannot be deleted. The CEO is a manager of a manager");
		} else if (role.equals("Manager") && (isManagerforManager || isManagerforRegEmployees)) {
			throw new CancellationException("Manager cannot be deleted. The manager is a manager of a regular employee or another manager");
		}
		
		employeeRepo.deleteById(employee.getId());
		return ResponseEntity.status(HttpStatus.OK)
				.body("Employee with the id: " + id + " has been successfully deleted.");
	}

	/**
	 * A method checking if role changing is valid or not by checking the Employee
	 * manager and the management assignments she/he has.
	 */
	private boolean checkRoleChange(Employee employee, String oldRole, String newRole) {
		boolean isManagerforAnotherManagers = employeeRepo.isManagerforManager(employee.getId());
		boolean isManagerforRegEmployees = employeeRepo.isManagerforRegEmployees(employee.getId());

		System.out.println("checkRoleChange start");
		System.out.println("isManagerforAnotherManagers " + isManagerforAnotherManagers);
		System.out.println("isManagerforEmployees  " + isManagerforRegEmployees);

		if (oldRole.equals("CEO") && newRole.equals("Employee")) {
			System.out.println("CEO to Employee");
			if (isManagerforAnotherManagers) {
				throw new CancellationException(
						"Changing CEO role to regular employee cannot be done. CEO is already a manager of another employee.");
			}
		} else if (oldRole.equals("Manager")) {
			if (newRole.equals("CEO") && isManagerforRegEmployees) {
				System.out.println("Manager to CEO");
				throw new CancellationException(
						"Changing Manager role to CEO cannot be done. Manager is already a manager for a regular Employee.");
			} else if (newRole.equals("Employee")) {
				System.out.println("Manager to Employee");
				if (isManagerforRegEmployees || isManagerforAnotherManagers) {
					throw new CancellationException(
							"Changing Manager role to regular employee cannot be done. Manager is already a manager for a regular Employee or another manager.");
				} else if (employee.getId() == employee.getManagerId()) {
					throw new CancellationException("Changing Manager role to regular employee cannot be done.");

				}
			}
		} else if (oldRole.equals("Employee") && newRole.equals("CEO") && employeeRepo.isCEODefined()) {
			throw new CancellationException(
					"Changing regular employee role to CEO cannot be done. CEO role is already assigned.");

		}
		return true;
	}

	/*
	 * A method check if the input values are accepted
	 */
	private boolean checkInput(Employee employee) {
		int managerId = employee.getManagerId();
		Employee CEO = employeeRepo.findByCeoTrue();
		employee.setFirstName(employee.getFirstName().trim());
		employee.setLastName(employee.getLastName().trim());

		if (employee.isCeo()) {
			if (employee.isManager()) {
				throw new CancellationException("isManager and isCEO cannot be true at the same time.");
			} else if (managerId != 0) {
				throw new CancellationException("No one can be CEO's manager");
			} else if (CEO != null) {
				throw new CancellationException("CEO role is already assigned");
			}
		} else if (employee.getSalaryRank() < 1 || employee.getSalaryRank() > 10)
			throw new CancellationException(" Salary rank has to be between 1-10");
		else if (employee.getFirstName().length() == 0 || employee.getLastName().length() == 0)
			throw new CancellationException("First and last name has to be filled");
		else if (getRole(employee).equals("Employee") && employee.getManagerId() == 0)
			throw new CancellationException("Regular employee should has a manager");

		if (managerId != 0 && !isManagerAccepted(employee)) {
			return false;
		}
		return true;
	}

	// A method return the role of the given employee. Otherwise, it return an empty
	// string
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

	/**
	 * A method checking whether the manageId given is valid and can be a manager of
	 * the given employee
	 */
	private boolean isManagerAccepted(Employee employee) {

		Optional<Employee> optionalManager = employeeRepo.findById(employee.getManagerId());
		Employee CEO = employeeRepo.findByCeoTrue();

		if (!optionalManager.isPresent())
			throw new CancellationException("No such employee found with the give ManagerId");

		Employee manager = optionalManager.get();
		String employeeRole = getRole(employee);
		String managerRole = getRole(manager);

		if (managerRole.equals("Employee"))
			throw new CancellationException("Regular employee cannot be a manager");

		if (employeeRole == "Employee") {
			if (CEO != null && CEO.equals(manager))
				throw new CancellationException("Regular employee cannot be managed by the CEO");
		} else if (employeeRole == "Manager") {
			if (employee.equals(manager))
				throw new CancellationException(
						"Manager can be managed by other managers or by CEO (cannot be a manager of himself)");
		}
		return true;
	}

	/**
	 * A method handling the execution exception and send error request to the
	 * client containing the message give by the exception
	 */
	@ExceptionHandler(Exception.class)
	public ResponseEntity<String> handleException(Exception e) {
		System.out.print(e.toString());
		if (e.getClass() == MethodArgumentNotValidException.class) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body("Bad Request Error: 400 \n Employee arguments are not valid.");
		} else if (e.getClass() == CancellationException.class) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Bad Request Error: 400 \n" + e.getMessage());
		} else {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Server Error: 500");
		}
	}

}
