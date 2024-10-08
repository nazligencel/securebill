package com.secure.securebill.service.implementation;

import com.secure.securebill.domain.User;
import com.secure.securebill.dto.UserDTO;
import com.secure.securebill.dtomapper.UserDTOMapper;
import com.secure.securebill.repositoy.UserRepository;
import com.secure.securebill.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository<User> userRepository;

    @Override
    public UserDTO createUser(User user) {
        return UserDTOMapper.fromUser(userRepository.create(user));
    }
}
