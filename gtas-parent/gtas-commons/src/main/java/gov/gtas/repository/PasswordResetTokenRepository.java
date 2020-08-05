package gov.gtas.repository;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import gov.gtas.model.PasswordResetToken;

public interface PasswordResetTokenRepository extends CrudRepository<PasswordResetToken, String> {
	
	@Query("SELECT prt from PasswordResetToken prt WHERE prt.token = :token")
	Optional<PasswordResetToken> findByTokenValue(@Param("token") String token);

}
