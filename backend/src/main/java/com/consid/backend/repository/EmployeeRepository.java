package com.consid.backend.repository;

import java.util.List;

import javax.persistence.QueryHint;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;

import com.consid.backend.model.Employee;

public interface EmployeeRepository extends JpaRepository<Employee,Integer>{


	  Employee findByCeoTrue();
	  
	  List<Employee> findByManagerTrue();
	  
	  @Query("SELECT e FROM Employee e WHERE e.ceo = false AND e.manager = false")
	  List<Employee> findEmployees();
	  
	  @Query("SELECT e FROM Employee e WHERE e.ceo = false AND e.manager = false AND e.managerId = ?1")
	  List<Employee> findEmployeesWithManagerId(int id);

	  @Query("SELECT CASE WHEN COUNT(e) > 0 THEN true ELSE false END FROM Employee e where e.ceo = true")
	  Boolean isCEODefined();
	  
	  @Query("SELECT CASE WHEN COUNT(e) > 0 THEN true ELSE false END FROM Employee e where e.manager = true AND e.managerId = ?1")
	  Boolean isManagerforManager(int id);
	  
	  @Query("SELECT CASE WHEN COUNT(e) > 0 THEN true ELSE false END FROM Employee e where e.manager = false AND e.ceo = false AND e.managerId = ?1")
	  Boolean isManagerforRegEmployees(int id);

	  @Query("SELECT CASE WHEN COUNT(e) > 0 THEN true ELSE false END FROM Employee e where e.managerId = ?1")
	  Boolean isManagerforAnybody(int id);
	  

}
