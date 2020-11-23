package com.consid.backend.model;

import java.io.Serializable;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "libraryItem")
@ToString
@EqualsAndHashCode(of = "id")
public class LibraryItem implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Getter
	@Setter
	private int id;

	@NotNull
	@Getter
	@Setter
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "category_id")
	private Category category;

	@NotNull
	@Getter
	@Setter
	private String title;

	@Getter
	@Setter
	private String author;

	@Getter
	@Setter
	private int pages;

	@Getter
	@Setter
	private int runTimeMinutes;

	@Getter
	@Setter
	private boolean isBorrowable;

	@Getter
	@Setter
	private String borrower;

	@Getter
	@Setter
	private Date borrowDate;

	@NotNull
	@Getter
	@Setter
	@Pattern(regexp = "(Reference B|reference b|Audio B|audio b|[Bb])ook|dvd|DVD") // ensure that the type is equal to the following values: Book or
																					// Reference Book or Audio Book or DVD
	private String type;
	
}
