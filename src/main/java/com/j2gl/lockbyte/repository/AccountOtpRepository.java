package com.j2gl.lockbyte.repository;

import com.j2gl.lockbyte.model.AccountOtp;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface AccountOtpRepository extends JpaRepository<AccountOtp, Long> {
    Optional<AccountOtp> findByAccountName(String accountName);
}
