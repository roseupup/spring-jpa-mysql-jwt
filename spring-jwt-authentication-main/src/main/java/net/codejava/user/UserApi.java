package net.codejava.user;

import java.net.URI;
import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import net.codejava.product.Product;

@RestController
public class UserApi {

	@Autowired private UserRepository userRepo;
	@Autowired PasswordEncoder passwordEncode;
	
	
	@GetMapping("/users")
	public List<User> list() {
		return userRepo.findAll();
	}
	
	@PostMapping("/signup")
	public ResponseEntity<?> create(@RequestBody  User user) {
		String userEmail = user.getEmail();
		
		Optional<User> existingUser = userRepo.findByEmail(userEmail);
		if(existingUser.isPresent()) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: Email is already in use!");
		}
		String userPassword = passwordEncode.encode(user.getPassword());
		User savedUser = userRepo.save(new User(userEmail, userPassword));
		URI userURI = URI.create("/users/" + savedUser.getId());
		return ResponseEntity.created(userURI).body(savedUser);
	}
	
	@PostMapping("/users/delete")
	public List<User> deleteUser(@RequestBody @Valid User user){
		Optional<User> existingUser = userRepo.findByEmail(user.getEmail());
		if(existingUser.isPresent()) {
			userRepo.delete(existingUser.get());
			return userRepo.findAll();
		}
//		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: user is not existed!");
		return null;
	}
	
	
}
