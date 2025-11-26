package com.barbearia.Barbearia.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.barbearia.Barbearia.Model.RegisterUser;

@Repository
public interface RegisterUserRepository extends JpaRepository<RegisterUser, Long> {

    Optional<RegisterUser> findByEmail(String email);

}