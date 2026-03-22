package com.prince.auth_app.repositories;

import com.prince.auth_app.entities.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.rmi.server.UID;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UID> {
}
