//package com.projectManagementTool;
//import org.springframework.security.authentication.BadCredentialsException;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.Authentication;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//
//@RestController
//@RequestMapping("/auth")
//public class AuthController {
//	
//	@Autowired
//	private UserRepository userRepository;
//	
//	@Autowired
//	private PasswordEncoder passwordEncoder;
//	
//	@Autowired
//	private CustomeUserDetailsImpl customeUserDetails;
//	
//	@Autowired
//	private SubscriptionService subscriptionService;
//	
//	@Value("${jwt.secret}")
//	private String jwtSecret;
//	
//	@PostMapping("/signup")
//	public ResponseEntity<AuthResponse> createUserHandler(@RequestBody User user) throws Exception{
//		 User isUserExist = userRepository.findByEmail(user.getEmail());
//		 
//		 if(isUserExist!=null) {
//			  throw new Exception("email already exists !!");
//		 }
//		 
//		 User createdUser = new User();
//		 createdUser.setPassword(passwordEncoder.encode(user.getPassword()));
//	     createdUser.setEmail(user.getEmail());
//	     createdUser.setFullName(user.getFullName());
//	     
//	     User savedUser=userRepository.save(createdUser);
//	     subscriptionService.createSubscription(savedUser);
//
//	     Authentication authentication = new UsernamePasswordAuthenticationToken(user.getEmail(),user.getPassword());
//	     SecurityContextHolder.getContext().setAuthentication(authentication);
//	     
//	     String jwt = JwtProvider.generateToken(authentication,jwtSecret);
//	     
//	     AuthResponse res = new AuthResponse();
//	     res.setMessage("signup success");
//	     res.setJwt(jwt);
//	     
//	     return new ResponseEntity<>(res,HttpStatus.CREATED);
//	}
//	
//	
//	@PostMapping("/signin")
//    public ResponseEntity<AuthResponse> signin(@RequestBody LoginRequest loginRequest){
//		String username = loginRequest.getEmail();
//		String password = loginRequest.getPassword();
//	   
//		Authentication authentication = authenticate(username,password);
//		
//	    SecurityContextHolder.getContext().setAuthentication(authentication);
//	     
//	    String jwt = JwtProvider.generateToken(authentication,jwtSecret);
//	     
//	     AuthResponse res = new AuthResponse();
//	     res.setMessage("signin success");
//	     res.setJwt(jwt);
//	     
//	     return new ResponseEntity<>(res,HttpStatus.CREATED);
//	}
//	
//	
//	private Authentication authenticate (String username , String password) {
//		  UserDetails userDetails = customeUserDetails.loadUserByUsername(username);
//		  if(userDetails==null) {
//			   throw new BadCredentialsException("Invalid username or password !!");
//			   
//		  }
//		  if(!passwordEncoder.matches(password, userDetails.getPassword())) {
//			   throw new BadCredentialsException("Invalid password !!");
//		  }
//		  
//		  return new UsernamePasswordAuthenticationToken(userDetails,null,userDetails.getAuthorities());
//	}
//	
//}
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//








package com.projectManagementTool;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private CustomeUserDetailsImpl customeUserDetails;

    @Autowired
    private SubscriptionService subscriptionService;

    // inject jwt.secret from application.properties
    @Value("${jwt.secret}")
    private String jwtSecret;

    @PostMapping("/signup")
    public ResponseEntity<AuthResponse> createUserHandler(@RequestBody User user) throws Exception {
        User isUserExist = userRepository.findByEmail(user.getEmail());

        if (isUserExist != null) {
            throw new Exception("email already exists !!");
        }

        User createdUser = new User();
        createdUser.setPassword(passwordEncoder.encode(user.getPassword()));
        createdUser.setEmail(user.getEmail());
        createdUser.setFullName(user.getFullName());

        User savedUser = userRepository.save(createdUser);
        subscriptionService.createSubscription(savedUser);

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                user.getEmail(), user.getPassword());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // ✅ pass secret here
        String jwt = JwtProvider.generateToken(authentication, jwtSecret);

        AuthResponse res = new AuthResponse();
        res.setMessage("signup success");
        res.setJwt(jwt);

        return new ResponseEntity<>(res, HttpStatus.CREATED);
    }

    @PostMapping("/signin")
    public ResponseEntity<AuthResponse> signin(@RequestBody LoginRequest loginRequest) {
        String username = loginRequest.getEmail();
        String password = loginRequest.getPassword();

        Authentication authentication = authenticate(username, password);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // ✅ pass secret here
        String jwt = JwtProvider.generateToken(authentication, jwtSecret);

        AuthResponse res = new AuthResponse();
        res.setMessage("signin success");
        res.setJwt(jwt);

        return new ResponseEntity<>(res, HttpStatus.CREATED);
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse> forgotPassword(@RequestParam String email) {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new RuntimeException("User not found");
        }

        String resetToken = JwtProvider.generatePasswordResetToken(email, jwtSecret);

        // Logic to send email: Use Spring Boot Starter Mail
        // String resetUrl = "yourfrontend.com" + resetToken;
        // emailService.send(email, "Password Reset", "Click here: " + resetUrl);

        return ResponseEntity.ok(new ApiResponse("Reset link sent to email", true));
    }

    private Authentication authenticate(String username, String password) {
        UserDetails userDetails = customeUserDetails.loadUserByUsername(username);
        if (userDetails == null) {
            throw new BadCredentialsException("Invalid username or password !!");
        }
        if (!passwordEncoder.matches(password, userDetails.getPassword())) {
            throw new BadCredentialsException("Invalid password !!");
        }

        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }
}

