package com.secure.securebill.service;

import com.secure.securebill.domain.User;
import com.secure.securebill.dto.UserDTO;

public interface UserService {
    UserDTO createUser(User user);
}
