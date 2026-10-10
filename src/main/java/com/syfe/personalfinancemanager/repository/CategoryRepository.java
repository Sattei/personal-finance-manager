package com.syfe.personalfinancemanager.repository;

import com.syfe.personalfinancemanager.entity.Category;
import com.syfe.personalfinancemanager.entity.User;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    List<Category> findByUser(User user);

    List<Category> findByIsDefaultTrue();

    Optional<Category> findByIdAndUser(Long id, User user);

    Optional<Category> findByNameAndUser(String name, User user);

    Optional<Category> findByNameAndIsDefaultTrue(String name);

    boolean existsByNameAndUser(String name, User user);

    boolean existsByNameAndIsDefaultTrue(String name);
}