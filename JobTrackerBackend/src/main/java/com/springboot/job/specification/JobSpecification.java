package com.springboot.job.specification;

import org.springframework.data.jpa.domain.Specification;
import com.springboot.job.entity.Job;
import com.springboot.job.entity.Status;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

public class JobSpecification {
	
	  public static Specification<Job> belongsToUser(String email) {
	        return (root, query, cb) ->
	                cb.equal(root.get("user").get("email"), email);
	    }

	public static Specification<Job> getSpecification(String search) {
		return new Specification<Job>() {
			@Override
			public Predicate toPredicate(Root<Job> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
				if (search == null || search.trim().isEmpty()) {
					System.out.println("No search term provided, returning all records.");
					System.out.println("Root: " + root);
					System.out.println("CriteriaBuilder: " + criteriaBuilder.conjunction());
					return criteriaBuilder.conjunction();
				}
				String likeSearch = "%" + search.toLowerCase() + "%";
				return criteriaBuilder.or(
						criteriaBuilder.like(criteriaBuilder.lower(root.get("companyName")), likeSearch),
						criteriaBuilder.like(criteriaBuilder.lower(root.get("jobTitle")), likeSearch),
						criteriaBuilder.like(criteriaBuilder.lower(root.get("location")), likeSearch));
			}
		};

	}

}
