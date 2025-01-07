# Auth server for authentication and authorization

In this project, we will build an API that allows us not only to register and persist users using JPA but will also allow us to authenticate and validate their access using JwtToken

### Security Config

First, we will need to configure some beans in a configuration class.

```
@Configuration
public class SecurityConfig {

	@Bean
	SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		
		http.csrf(crsf -> crsf.disable())
			.authorizeHttpRequests(requests -> requests.anyRequest().permitAll());
		http.headers(headers -> headers.frameOptions(option -> option.disable()));
		
		return http.build();
	}
	
	@Bean
	PasswordEncoder encoder(){
		return new BCryptPasswordEncoder();
	}
		
}
```
`SecurityFilterChain` bean will be needed to disable crsf protection to make thing easier to configure although our request may be processes by browsers. 
Disabling frameOptions is only needed if we are using H2 as a local database a we configure the H2 Console.

`PasswordEncoder` bean will be used when storing password in DB.

### User Repository

To be able to authenticate and therefore log in, it will be necessary to register the user previously. So we will need a `UserDto.java`, `UserEntity.java` and `UserRepository.java` using JPA Api.

```
public class UserDto {
	
	private int id;
	private String username;
	private String password;

---------------------------------

@Entity
public class UserEntity {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	private String username;
	private String password;

--------------------------------

public interface UserRepository extends JpaRepository<UserEntity, Integer> {
	
	Optional<UserEntity> findByUsername(String username);
```

### JWT Provider

Once the user is created and persisted, we will need to be able to give a JWT Token everytime a user log in (also we need to be able to validate that token separately).
We create a service called `AuthService` for that with the methods:
- saveUser : Receive a `UserDto` and will return the persisted one if it does not exists.
* login : Receive a `UserDto` and will return `TokenDto` in case it iexists and credentials are correct.
+ validate : Receive a string (token) a return TOkenDto if it is validated correctly

An then, we will need to create JWT Provider for creating and validating the token itself.

```
@Component
public class JwtProvider {

	private static final Logger log = LoggerFactory.getLogger(JwtProvider.class);
	
	@Value("${security.jwt.secret-key}")
    private String secretKey;

    @Value("${security.jwt.expiration-time}")
    private long jwtExpiration;
    
    @Value("${security.jwt.company}")
    private String company;
	
	public String create(UserEntity user) {
		Map<String, Object> claims = Jwts.claims().setSubject(user.getUsername());
		claims.put("id",  user.getId());
		claims.put("company-name", company);
		
		Instant issuedAt = Instant.now().truncatedTo(ChronoUnit.SECONDS);
		Instant expiredAt = issuedAt.plus(jwtExpiration, ChronoUnit.MINUTES);
		
		return Jwts.builder()
				.setHeaderParam("typ", "JWT")
				.setClaims(claims)
				.setIssuedAt(Date.from(issuedAt))
				.setExpiration(Date.from(expiredAt))
				.signWith(getSignInKey(),SignatureAlgorithm.HS256)
				.compact();
	}
	
	public boolean validate(String token) {
			
			 try {
				 
				Jwts.parserBuilder()
				    .setSigningKey(getSignInKey())
				 	.build()
				 	.parseClaimsJws(token);
				
				return true;
				
			} catch (WeakKeyException e) {
				log.error("Weak key token : " , e.getMessage());
			} catch (SignatureException e) {
				log.error("Wrong signature token : " , e.getMessage());
			} catch (ExpiredJwtException e) {
				log.error("Expired token : " , e.getMessage());
			} catch (UnsupportedJwtException e) {
				log.error("Unsupported token : " , e.getMessage());
			} catch (MalformedJwtException e) {
				log.error("Malformed token : " , e.getMessage());
			} catch (IllegalArgumentException e) {
				log.error("Illegal token : " , e.getMessage());
			}
			 
		return false;
	}
	
	public String getUsernameFromToken(String token) {
		try {
			
			return Jwts.parserBuilder()
				    .setSigningKey(getSignInKey())
				 	.build()
				 	.parseClaimsJws(token)
				    .getBody().getSubject();
			
		}catch (Exception e) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid token");
		}
	}
	
	private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

```
> [!NOTE]
> In recent version of `spring.security` secret key length must be equal to the size of bits of the hash function (also it must be decoded base64).
>
> `security.jwt.secret-key`,`security.jwt.expiration-time` and `security.jwt.company` can be found in config-server repository .properties
>
> **security.jwt.secret-key**=dcc24fedc1c0abd5371081e891f8e94d2ded7285357878e5866c49699e8e5fec
> 
> **security.jwt.expiration-time**=30
> 
> **security.jwt.company**=arq








