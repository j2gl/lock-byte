package com.j2gl.lockbyte.repository;

import com.j2gl.lockbyte.model.OTPAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface OTPAccountRepository extends JpaRepository<OTPAccount, Long> {
    Optional<OTPAccount> findByUsername(String username);
}
