# Project Title

Spring-Jpa-MySQL-JWT


## Description

This is a Spring project which is build by JPA+MySQL+JWT. JPA would implement CRUD automaticlly.

## Getting Started


### Dependencies

* Latest version of Eclipse
* Java 11
* Spring 2.5.12
* Docker Desktop
* MySQL mysql:8-oracle image


### Executing program

* Download latest version of [Docker](https://www.docker.com/products/docker-desktop/)
* Launch MySQL as Docker Container using Terminal
```
docker run --detach --env MYSQL_ROOT_PASSWORD=dummypassword --env MYSQL_USER=social-media-user --env MYSQL_PASSWORD=dummypassword --env MYSQL_DATABASE=social-media-database --name mysql --publish 3306:3306 mysql:8-oracle
```
* Database name is social-media-database, password is dummypassword
* Mysqlsh
```
mysqlsh
\connect social-media-user@localhost:3306
\sql
use social-media-database
```
<img width="599" alt="image" src="https://github.com/roseupup/spring-jpa-mysql-jwt/assets/22486690/5f0d5bbb-cf01-4472-acbe-f30b747d5097">

* Makesure application.properties file get the right database name and password
```
spring.datasource.username=social-media-user
spring.datasource.password=dummypassword
```
* Start project in Eclipse. JPA would create users\products\roles table by default.
* Send a Post request to https://localhost:8080/singup to sign up a new user using email and password. You can use Postman or Talend API Tester Chrome plug which I used.
```java
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
  ```
  <img width="1441" alt="image" src="https://github.com/roseupup/spring-jpa-mysql-jwt/assets/22486690/cf79eeef-4655-44d2-9d7c-8b8758898c2b">
  <img width="1436" alt="image" src="https://github.com/roseupup/spring-jpa-mysql-jwt/assets/22486690/cfc40c91-8dbd-44f7-8dcd-58fef01af616">

* You would get a 201 responss which means you signup a user successfully.
* After signup, send a post request to http://localhost:8080/auth/login to get a JWT Token.

<img width="1447" alt="image" src="https://github.com/roseupup/spring-jpa-mysql-jwt/assets/22486690/451f9772-7eb0-4fa6-a051-e73bc7194b79">
<img width="1453" alt="image" src="https://github.com/roseupup/spring-jpa-mysql-jwt/assets/22486690/418931e8-030f-4250-b252-9e7870639e29">

* Copy the access token and paste it to header as follow:

<img width="537" alt="image" src="https://github.com/roseupup/spring-jpa-mysql-jwt/assets/22486690/400e5182-a7b1-4389-91f3-7fd2b431cc4f">

* Now you can access resources under the protection of Spring Security.
* You can also modify your filter in ApplicationSecurity Class making the security a role based one.

```java
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.csrf().disable();
		http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
		
		http.authorizeRequests()
				.antMatchers("/auth/login", "/signup").permitAll()
				.anyRequest().authenticated();
		
        http.exceptionHandling()
                .authenticationEntryPoint(
                    (request, response, ex) -> {
                        response.sendError(
                            HttpServletResponse.SC_UNAUTHORIZED,
                            ex.getMessage()
                        );
                    }
                );
        
		http.addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class);
	}
  ```
