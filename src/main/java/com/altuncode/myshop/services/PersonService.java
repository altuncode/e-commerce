package com.altuncode.myshop.services;

import com.altuncode.myshop.model.Person;
import com.altuncode.myshop.model.Product;
import com.altuncode.myshop.repositories.PersonRepo;
import com.altuncode.myshop.repositories.ProductRepo;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Service;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;


@Service("PersonService")
@Transactional
public class PersonService {

    private final PersonRepo personRepo;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final EmailService emailService;
    private final ProductRepo productRepo;
    private final SessionService sessionService;


    @Autowired
    public PersonService(@Qualifier("PersonRepo") PersonRepo personRepo, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, EmailService emailService, @Qualifier("ProductRepo") ProductRepo productRepo, SessionService sessionService) {
        this.personRepo = personRepo;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.emailService = emailService;
        this.productRepo = productRepo;
        this.sessionService = sessionService;
    }

    // List all products with pageable
    public Page<Person> getAllPersonList(Pageable pageable) {
        return personRepo.findAll(pageable);
    }

    public Long getPersonCount() {
        return personRepo.countPersons();
    }

    //Altun buna baxdi
    // Find user by email for admin panel
    public Person findByEmailForAdmin(String username) {
        return personRepo.findByEmail(username).orElse(null);
    }

    //Altun buna baxdi
    // Find user by email for user withod password
    public Person findByEmailForUser(String username) {
        Person person = personRepo.findByEmail(username).orElse(null);
        if (person != null) {
            person.setPassword(null);  // Set the password to null if person is found
        }
        return person;
    }

    //Altun buna baxdi
    // Find user by id
    public Person findById(Long id) {
        return personRepo.findById(id).orElse(null);
    }

    //Altun buna baxdi
    //Get all persons with pageable
    public Page<Person> getAllPersonWithPage(Pageable pageable) {
        return personRepo.findAll(pageable);
    }

    //Altun buna baxdi
    // Register and then login user and only loginuser
    public void registerOrLoginUser(String email, String rawPassword, HttpServletRequest request) {
        Person person = findByEmailForAdmin(email);
        if (person == null) {
            Person person2 = new Person();
            person2.setEmail(email);
            person2.setPassword(passwordEncoder.encode(rawPassword));
            person2.setRole("ROLE_USER");
            personRepo.save(person2);
            authenticateUser(email, rawPassword,request);
        } else if (passwordEncoder.matches(rawPassword, person.getPassword())) {
            authenticateUser(email, rawPassword, request);
        }else {
            throw new AuthenticationException("Invalid password or email") {};
        }
    }

    //Altun buna baxdi
    // login user
    private void authenticateUser(String email, String rawPassword, HttpServletRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, rawPassword)
            );
            if (authentication.isAuthenticated()) {
                SecurityContext context = SecurityContextHolder.getContext();
                context.setAuthentication(authentication);

                HttpSession session = request.getSession(true);
                session.setMaxInactiveInterval(31536000);
                session.setAttribute(
                        HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                        context
                );
                sessionService.setUserSessions(session.getId(), authentication.getPrincipal());
            } else {
                throw new BadCredentialsException("Authentication failed");
            }
        } catch (AuthenticationException e) {
            SecurityContextHolder.clearContext();
            throw new BadCredentialsException("Authentication failed: " + e.getMessage());
        }
    }

    public void logoutCurrentSession(HttpServletRequest request, HttpServletResponse response) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            new SecurityContextLogoutHandler().logout(request, response, authentication);
        }
        SecurityContextHolder.clearContext();
        request.getSession().invalidate(); // Invalidate only the current session
    }

    //Altun buna baxdi
    // update login in user mail
    public void updateUserEmail(String oldEmail, String newEmail) throws MessagingException {
        Optional<Person> existingUser = personRepo.findByEmail(newEmail);
        if (existingUser.isPresent()) {
            throw new IllegalArgumentException("Email already in use");
        }
        Person person = findByEmailForAdmin(oldEmail);
        person.setEmail(newEmail);
        personRepo.save(person);
        sessionService.expireUserSessions(oldEmail);
    }

    //Altun buna baxdi
    // update user's password
    public void changePassword(String mail, String newPassword, HttpServletRequest request)  {
        Person person1 = findByEmailForAdmin(mail);
        if(person1!=null){
            person1.setPassword(passwordEncoder.encode(newPassword));
            personRepo.save(person1);
            sessionService.expireUserSessions(mail); // Remove all active sessions
            authenticateUser(mail, newPassword,request);
        }else {
            throw new IllegalArgumentException("The provided information is incorrect. Please check and try again.");
        }
    }



    //Altun buna baxdi
    // update user's email and lohin
    public void updateUserEmailAndLogin(String oldEmail, String newEmail, HttpServletRequest request) throws MessagingException {
        updateUserEmail(oldEmail,newEmail);
        authenticateUser(newEmail,findByEmailForAdmin(newEmail).getPassword(),request);
    }



    // Generate and send reset password code
    public void generateResetPasswordCode(String email) throws MessagingException {
        // Find user by email
        Person person = findByEmailForAdmin(email);

        // If the user already has an unexpired reset code, resend it
        if (person.getResetPasswordCode() != null && person.getVerificationCodeExpiresAtForResetPassword() != null
                && person.getVerificationCodeExpiresAtForResetPassword().isAfter(LocalDateTime.now())) {
            emailService.sendHtmlEmail(person.getEmail(), "Password Reset Code",
                    "Your password reset code is: " + person.getResetPasswordCode());
            return;
        }

        // generate new code
        Random random = new Random();
        String code = String.format("%06d", random.nextInt(1000000));
        person.setResetPasswordCode(code);
        person.setVerificationCodeExpiresAtForResetPassword(LocalDateTime.now().plusMinutes(10));

        // Save the reset code to the user and send it via email
        personRepo.save(person);
        emailService.sendHtmlEmail(person.getEmail(), "Password Reset Code",
                "Your password reset code is: " + person.getResetPasswordCode());
    }

    //Altun buna baxdi
    // Validate reset password code
    public void validateResetPassword(String email, String resetCode, String newPassword) {
        // Find user by email
        Person person = personRepo.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User with this email does not exist"));

        // Validate the reset code
        if (person.getResetPasswordCode() == null || person.getVerificationCodeExpiresAtForResetPassword() == null
                || LocalDateTime.now().isAfter(person.getVerificationCodeExpiresAtForResetPassword())) {
            throw new IllegalArgumentException("Reset code is invalid or has expired.");
        }

        if (!person.getResetPasswordCode().equals(resetCode)) {
            throw new IllegalArgumentException("The reset code you entered is invalid. Please double-check and try again. If the issue persists, request a new code or contact support for assistance.");
        }

        person.setPassword(passwordEncoder.encode(newPassword));

        // Clear the reset code and expiration time
        person.setResetPasswordCode(null);
        person.setVerificationCodeExpiresAtForResetPassword(null);

        // Save the updated user
        personRepo.save(person);
    }



}
