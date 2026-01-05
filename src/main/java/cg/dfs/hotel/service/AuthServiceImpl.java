package cg.dfs.hotel.service;

import cg.dfs.hotel.dto.LoginDTO;
import cg.dfs.hotel.dto.RegisterDTO;
import cg.dfs.hotel.dto.UserDTO;
import cg.dfs.hotel.entities.Role;
import cg.dfs.hotel.entities.User;
import cg.dfs.hotel.mapper.UserMapper;
import cg.dfs.hotel.repo.UserRepository;
import cg.dfs.hotel.security.JwtUtil;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;



@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    public AuthServiceImpl(UserRepository userRepository,
                           PasswordEncoder passwordEncoder,
                           AuthenticationManager authenticationManager,
                           JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public UserDTO register(RegisterDTO dto) {
        if (dto.getEmail() == null || dto.getUsername() == null || dto.getPassword() == null) {
            throw new IllegalArgumentException("Username, email, and password are required");
        }
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException("Email already in use");
        }
        if (userRepository.existsByUsername(dto.getUsername())) {
            throw new IllegalArgumentException("Username already in use");
        }

        User user = new User();
        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());
        user.setPassword(passwordEncoder.encode(dto.getPassword())); // hash!
        user.setRole(dto.getRole() != null ? Role.valueOf(dto.getRole()) : Role.USER);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        user = userRepository.save(user);

        // Return safe DTO (mapper should not include password)
        return UserMapper.toDto(user);
    }


    @Override
    public String login(LoginDTO dto) {
        if (dto.getEmail() == null || dto.getPassword() == null) {
            throw new IllegalArgumentException("Email and password are required");
        }

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(dto.getEmail(), dto.getPassword())
            );
        } catch (BadCredentialsException ex) {
            throw new IllegalArgumentException("Invalid email or password");
        }

        // Generate JWT on success
        User user = userRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        return jwtUtil.generateToken(user.getEmail(), user.getId(), user.getRole());
    }

}

