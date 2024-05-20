package telran.java52.accounting.dao;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import telran.java52.accounting.model.UserAccount;

public interface UserRepository extends MongoRepository<UserAccount, String> {
	
	Optional<UserAccount> findUserByLogin(String login);
	
	Boolean existsByLogin(String login);
}
