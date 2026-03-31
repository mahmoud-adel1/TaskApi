package com.example.task.service;

import com.example.task.dto.request.UserRequest;
import com.example.task.entity.User;
import com.example.task.enums.RoleType;
import com.example.task.exception.UserNotFoundException;
import com.example.task.exception.UsernameAlreadyExistsException;
import com.example.task.mapper.Mapper;
import com.example.task.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleService roleService;

    public User create(UserRequest userRequest) {
        Optional<User> optionalUser = userRepository.getUserByUsername(userRequest.getUsername());
        if (optionalUser.isPresent()) {
            throw new UsernameAlreadyExistsException(userRequest.getUsername() + " is already exists");
        }
        User user = Mapper.mapUserRequestToUser(userRequest);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(roleService.getRoleByType(RoleType.USER));
        return userRepository.save(user);
    }

    public User getUserByUsername(String username) {
        Optional<User> user = userRepository.getUserByUsername(username);
        return user.orElseThrow(()->new UserNotFoundException(username + " is not found"));
    }
}
