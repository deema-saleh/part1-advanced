package com.example.craft.services;

import com.example.craft.repository.BaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.craft.models.User;
import com.example.craft.repository.UserRepo;

@Service
public class UserDetailsServiceImpl extends BaseService<User, Long> implements UserDetailsService {

  UserRepo userRepository;

  @Autowired
  public UserDetailsServiceImpl(UserRepo userRepository) {
    this.userRepository = userRepository;
  }

  @Override
  @Transactional
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("User Not Found with username: " + username));

    return UserDetailsImpl.build(user);
  }

  @Transactional
  public User findByUsername(String username) {
    return userRepository.findByUsername(username).get();
  }

  @Override
  protected BaseRepository<User, Long> getRepository() {
    return userRepository;
  }
}
