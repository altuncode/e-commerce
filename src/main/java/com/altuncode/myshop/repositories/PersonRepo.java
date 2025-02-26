package com.altuncode.myshop.repositories;

import com.altuncode.myshop.model.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository("PersonRepo")
public interface PersonRepo extends JpaRepository<Person, Long> {

    @Query("SELECT new com.altuncode.myshop.model.Person(p.id, p.email, p.password, p.resetPasswordCode, p.verificationCodeExpiresAtForResetPassword, p.role, p.createDate, p.blocked) FROM Person p WHERE p.email = :email")
    Optional<Person> findByEmail(@Param("email") String email);

    @Query("SELECT COUNT(p) FROM Person p")
    Long countPersons();

}
