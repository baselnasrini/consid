package com.consid.backend.controller;

import com.consid.backend.model.Employee;

public abstract class SalaryCalculator {
	private static final double CEO = 2.725;
	private static final double MANAGER = 1.725;
	private static final double EMPLOYEE = 1.125;

	public SalaryCalculator() {
	}

	public static double calSalary(Employee employee) {
		double salary = 0;
		if (employee.isCeo()) {
			salary = CEO * employee.getSalaryRank();
		} else if (employee.isManager()) {
			salary = MANAGER * employee.getSalaryRank();
		} else {
			salary = EMPLOYEE * employee.getSalaryRank();
		}
		return (double) Math.round(salary * 100) / 100;
	}
}
