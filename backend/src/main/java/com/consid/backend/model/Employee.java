package com.consid.backend.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.springframework.lang.Nullable;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;


@Entity
@Table(name="employee")
@ToString
@EqualsAndHashCode(of="id")
public class Employee {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Getter @Setter
	private int id;
	
	@Getter @Setter @NotNull
	private String firstName;
	
	@Getter @Setter @NotNull
	private String lastName;
	
	@Getter @Setter @NotNull
	private double salary;
	
	@Getter @Setter
	private boolean ceo;
	
	@Getter @Setter
	private boolean manager;
	
	@Getter @Setter
	private int managerId;
	
}
