package com.consid.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import com.consid.backend.controller.MyDBInitialization;
import com.consid.backend.model.Employee;
import com.consid.backend.model.LibraryItem;

@EnableJpaRepositories(basePackages="com.consid.backend.repository")
@SpringBootApplication
public class LuncherClass {

	public static void main(String[] args) {
		MyDBInitialization dbInit = new MyDBInitialization("root", "mysqlpass", "consid", "localhost", "3306" );
		dbInit.createDB();
		dbInit.closeConnection();
		
		Employee e1 = new Employee ();
		e1.getFirstName();
		
		Employee e2 = new Employee ();
		e2.setFirstName("asd");
		
		LibraryItem l1 = new LibraryItem();
		
		System.out.println(e1.equals(e2));

		SpringApplication.run(LuncherClass.class, args);
	}

}
