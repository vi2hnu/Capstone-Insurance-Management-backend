package org.example.identityservice.service.user;

import org.example.identityservice.model.entity.Users;
import org.example.identityservice.repository.UsersRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UsersRepository usersRepository;

    public UserDetailsServiceImpl(UsersRepository usersRepository) {
        this.usersRepository = usersRepository;
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) {
        Users user = usersRepository.findUsersByUsername(username);
        if(user==null){
            throw new UsernameNotFoundException("User Not found");
        }
        return UserDetailsImpl.build(user);
    }

    public Users getUserDetails(String username){
        return usersRepository.findUsersByUsername(username);
    }
}