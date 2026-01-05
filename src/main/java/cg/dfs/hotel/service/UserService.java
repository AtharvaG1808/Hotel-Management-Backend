package cg.dfs.hotel.service;

import cg.dfs.hotel.dto.UserDTO;

import java.util.List;
import java.util.Optional;

public interface UserService {
    UserDTO create(UserDTO dto);              // for admin-created users (no password here)
    Optional<UserDTO> findById(Long id);
    Optional<UserDTO> findByEmail(String email);
    Optional<UserDTO> findByUsername(String username);
    List<UserDTO> findAll();
    UserDTO update(Long id, UserDTO dto);     // username/email/role only
    void delete(Long id);
}

