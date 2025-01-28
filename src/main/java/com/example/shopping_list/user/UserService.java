package com.example.shopping_list.user;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.shopping_list.list.List;

@Service
public class UserService implements UserDetailsService {
  private final UserRepository userRepository;

  public UserService(UserRepository userRepository) { this.userRepository = userRepository; }

  @Override
  public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
    Optional<User> user = userRepository.findByEmail(email);
    if (user.isEmpty()) {
      throw new UsernameNotFoundException("User not found");
    }
    return user.get();
  }

  public User getUserById(Long id) { return userRepository.findById(id).orElseThrow(() -> new NoSuchElementException("User not found")); }

  public Set<Long> getUserListIds(Long userId) {
    Set<List> lists = userRepository.getUserListsIds(userId);
    return lists.stream().map(List::getId).collect(Collectors.toSet());
  }

  public Long getUserIdFromAuthentication(Authentication authentication) {
    return authentication.getPrincipal() instanceof User user ? user.getId() : null;
  }
}
