package com.techarha.poc.springboot.repositories;

import com.techarha.poc.springboot.entity.User;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User, Long> {
}
