package com.technokratos.repository;

import com.technokratos.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByIdAndDeletedFalse(Long id);

    Boolean existsByIdAndDeletedFalse(Long id);

    Optional<User> findByEmailAndDeletedFalse(String mail);

    List<User> findAllByDeletedFalse();

    @Modifying(clearAutomatically = true)
    @Transactional
    @Query("UPDATE User u SET u.deleted = true WHERE u.id = :id")
    void softDeleteById(Long id);

}
