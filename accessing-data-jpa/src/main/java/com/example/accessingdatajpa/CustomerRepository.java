package com.example.accessingdatajpa;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface CustomerRepository extends CrudRepository<Customer, Integer> {

    List<Customer> findByLastName(String lastName);
    Customer findById(Long id);
}
