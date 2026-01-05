package cg.dfs.hotel.service;

import cg.dfs.hotel.dto.LoginDTO;
import cg.dfs.hotel.dto.RegisterDTO;
import cg.dfs.hotel.dto.UserDTO;

public interface AuthService {
    UserDTO register(RegisterDTO dto); // returns safe user response (no password)
    String login(LoginDTO dto);        // returns JWT
}

