package it.univr.track.security;

import it.univr.track.entity.UserRegistered;
import it.univr.track.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserProfileService implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserRegistered userRegistered = userRepository.findByUsername(username).orElseThrow();
        return new CustomUserDetails(userRegistered);
    }
}
