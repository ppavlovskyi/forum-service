package telran.java52.accounting.service;


import org.modelmapper.ModelMapper;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import telran.java52.accounting.dao.UserRepository;
import telran.java52.accounting.dto.RolesDto;
import telran.java52.accounting.dto.UserDto;
import telran.java52.accounting.dto.UserEditDto;
import telran.java52.accounting.dto.UserRegisterDto;
import telran.java52.accounting.dto.exeption.IncorrectRroleException;
import telran.java52.accounting.dto.exeption.UserExistsException;
import telran.java52.accounting.dto.exeption.UserNotFoundException;
import telran.java52.accounting.model.Role;
import telran.java52.accounting.model.UserAccount;

@Service
@RequiredArgsConstructor
public class UserAccountServiceImpl implements UserAccountService, CommandLineRunner {

	final UserRepository userRepository;
	final ModelMapper modelMapper;
	final PasswordEncoder passwordEncoder;

	@Override
	public UserDto register(UserRegisterDto userRegisterDto) {
		if(userRepository.existsByLogin(userRegisterDto.getLogin())) {
			throw new UserExistsException();
		}
		UserAccount userAccount = modelMapper.map(userRegisterDto, UserAccount.class);
		String password =passwordEncoder.encode(userRegisterDto.getPassword());
		userAccount.setPassword(password);
		userAccount = userRepository.save(userAccount);
		return modelMapper.map(userAccount, UserDto.class);
	}

	@Override
	public UserDto getUser(String login) {
		UserAccount userAccount = userRepository.findUserByLogin(login).orElseThrow(UserNotFoundException::new);
		return modelMapper.map(userAccount, UserDto.class);
	}

	@Override
	public UserDto removeUser(String login) {
		UserAccount userAccount = userRepository.findUserByLogin(login).orElseThrow(UserNotFoundException::new);
		userRepository.delete(userAccount);
		return modelMapper.map(userAccount, UserDto.class);
	}

	@Override
	public UserDto updateUser(String login, UserEditDto userEditDto) {
		UserAccount userAccount = userRepository.findUserByLogin(login).orElseThrow(UserNotFoundException::new);
		String firstName = userEditDto.getFirstName();
		if (firstName != null)
			userAccount.setFirstName(firstName);
		String lastName = userEditDto.getLastName();
		if (lastName != null)
			userAccount.setLastName(lastName);
		userAccount = userRepository.save(userAccount);
		return modelMapper.map(userAccount, UserDto.class);
	}

	@Override
	public RolesDto changeRolesList(String login, String role, boolean isAddRole) {
		UserAccount userAccount = userRepository.findUserByLogin(login).orElseThrow(UserNotFoundException::new);
		boolean res;
		try {
			if (isAddRole) {
				res = userAccount.addRole(role);
			} else {
				res = userAccount.removeRole(role);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			throw new IncorrectRroleException();
		}
		if(res)userAccount = userRepository.save(userAccount);
		return modelMapper.map(userAccount, RolesDto.class);
	}

	@Override
	public void changePassword(String login, String newPassword) {
		UserAccount userAccount = userRepository.findUserByLogin(login).orElseThrow(UserNotFoundException::new);
		String password =passwordEncoder.encode(newPassword);
		userAccount.setPassword(password);
		userAccount = userRepository.save(userAccount);

	}

	@Override
	public void run(String... args) throws Exception {
		if(!userRepository.existsById("admin")) {
			String password = passwordEncoder.encode("admin");
			UserAccount userAccount= new UserAccount("admin", "", "", password);
			userAccount.addRole(Role.MODERATOR.name());
			userAccount.addRole(Role.ADMINISTRATOR.name());	
			userRepository.save(userAccount);
		}
		
	}

}
