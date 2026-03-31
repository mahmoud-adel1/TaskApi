package com.example.task.service;

import com.example.task.entity.Role;
import com.example.task.enums.RoleType;
import com.example.task.exception.RoleNotFoundException;
import com.example.task.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RoleService {

    private final RoleRepository roleRepository;

    public Role getRoleByType(RoleType roleType) {
        Optional<Role> role = roleRepository.getRoleByRoleType(roleType);
        if (role.isEmpty()) {
            throw new RoleNotFoundException(roleType.name() + " is not found");
        }
        return role.get();
    }
}
