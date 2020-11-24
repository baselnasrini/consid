package com.consid.backend.repository;

import java.util.List;

import javax.persistence.QueryHint;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;

import com.consid.backend.model.Employee;

public interface EmployeeRepository extends JpaRepository<Employee,Integer>{

	  List<Employee> findByLastName(String lastname);

	  Employee findByCeoTrue();
	  
	  List<Employee> findByManagerTrue();

	  @Query("SELECT e FROM Employee e WHERE e.ceo = false AND e.manager = false")
	  List<Employee> findEmployees();
	  
	  @Query("SELECT CASE WHEN COUNT(e) > 0 THEN true ELSE false END FROM Employee e where e.ceo = true")
	  Boolean isCEODefined();

}
