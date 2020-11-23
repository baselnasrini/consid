package com.consid.backend.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "category")
@JsonInclude(Include.NON_NULL)
@EqualsAndHashCode(of = "id")
public class Category implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Getter
	@Setter
	private int id;

	@Getter
	@Setter
	@NotNull
	private String categoryName;

	@Getter
	@Setter
	@OneToMany(mappedBy = "category", cascade = CascadeType.ALL)
	private List<LibraryItem> libraryItems = new ArrayList<LibraryItem>();


}
