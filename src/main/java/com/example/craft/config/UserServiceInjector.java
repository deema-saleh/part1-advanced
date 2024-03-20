package com.example.craft.config;

import com.example.craft.services.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UserServiceInjector {

  private static UserDetailsServiceImpl userDetailsServiceImpl;

  @Autowired
  public UserServiceInjector(UserDetailsServiceImpl userDetailsServiceImpl) {
    UserServiceInjector.userDetailsServiceImpl = userDetailsServiceImpl;
  }

  public static UserDetailsServiceImpl getUserDetailsService() {
    return userDetailsServiceImpl;
  }
}
