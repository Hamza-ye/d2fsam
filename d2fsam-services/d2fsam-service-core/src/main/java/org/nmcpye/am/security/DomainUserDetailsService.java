//package org.nmcpye.am.security;
//
//import org.hibernate.validator.internal.constraintvalidators.hv.EmailValidator;
//import org.nmcpye.am.user.User;
//import org.nmcpye.am.user.UserRepositoryExt;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.security.core.GrantedAuthority;
//import org.springframework.security.core.authority.SimpleGrantedAuthority;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.security.core.userdetails.UsernameNotFoundException;
//import org.springframework.stereotype.Component;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.List;
//import java.util.Locale;
//import java.util.Optional;
//import java.util.stream.Collectors;
//
///**
// * Authenticate a user from the database.
// */
//@Component("userDetailsService")
//public class DomainUserDetailsService implements UserDetailsService {
//
//    private final Logger log = LoggerFactory.getLogger(DomainUserDetailsService.class);
//
//    private final UserRepositoryExt userRepository;
//
//    public DomainUserDetailsService(UserRepositoryExt userRepository) {
//        this.userRepository = userRepository;
//    }
//
//    @Override
//    @Transactional(readOnly = true)
//    public UserDetails loadUserByUsername(final String username) {
//        log.debug("Authenticating {}", username);
//
//        if (new EmailValidator().isValid(username, null)) {
//            return Optional.ofNullable(userRepository
//                .getUserByUsername(username, true))
//                .map(user -> createSpringSecurityUser(username, user))
//                .orElseThrow(() -> new UsernameNotFoundException("User with email " + username + " was not found in the database"));
//        }
//
//        String lowercaseLogin = username.toLowerCase(Locale.ENGLISH);
//        return Optional.ofNullable(userRepository
//            .getUserByEmail(lowercaseLogin, true))
//            .map(user -> createSpringSecurityUser(lowercaseLogin, user))
//            .orElseThrow(() -> new UsernameNotFoundException("User " + lowercaseLogin + " was not found in the database"));
//    }
//
//    private org.springframework.security.core.userdetails.User createSpringSecurityUser(String lowercaseLogin, User user) {
//        if (!user.isActivated()) {
//            throw new UserNotActivatedException("User " + lowercaseLogin + " was not activated");
//        }
//        List<GrantedAuthority> grantedAuthorities = user
//            .getAuthorities()
//            .stream()
//            .map(GrantedAuthority::getAuthority)
//            .map(SimpleGrantedAuthority::new)
//            .collect(Collectors.toList());
//        return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), grantedAuthorities);
//    }
//}
