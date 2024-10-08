package com.secure.securebill.repositoy.implementation;

import com.secure.securebill.domain.Role;
import com.secure.securebill.domain.User;
import com.secure.securebill.exception.ApiException;
import com.secure.securebill.repositoy.RoleRepository;
import com.secure.securebill.repositoy.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static com.secure.securebill.enumeration.RoleType.ROLE_USER;
import static com.secure.securebill.enumeration.VerificationType.ACCOUNT;
import static com.secure.securebill.query.UserQuery.*;
import static java.util.Map.of;

@Repository
@RequiredArgsConstructor
@Slf4j
public class UserRepositoryImpl implements UserRepository<User> {
    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final RoleRepository<Role> roleRepository;
    private final BCryptPasswordEncoder encoder;

    @Override
    public User create(User user) {
        if(getEmailCount(user.getEmail().trim().toLowerCase()) > 0) throw new ApiException("Email already in use.");

        try {
            KeyHolder keyHolder = new GeneratedKeyHolder();
            SqlParameterSource parameters =  getSqlParameterSource(user);
            jdbcTemplate.update(INSERT_USER_QUERY, parameters, keyHolder);
            user.setId(Objects.requireNonNull(Objects.requireNonNull(keyHolder.getKey())).longValue());

            roleRepository.addRoleToUser(user.getId(), ROLE_USER.name());

            String verificationUrl = getVerificationUrl(UUID.randomUUID().toString(), ACCOUNT.getType());

            jdbcTemplate.update(INSERT_ACCOUNT_VERIFICATION_URL_QUERY, of("userId", user.getId(), "url", verificationUrl));

            //emailService.sendVerificationUrl(user.getFirstName(), user.getEmail(), verificationUrl, ACCOUNT);
            user.setEnabled(false);
            user.setNotLocked(true);
            return user;

        }catch (Exception e){
            throw new ApiException("An error occured. Please try again later.");
        }
    }


    @Override
    public Collection<User> list(int page, int pageSize) {
        return List.of();
    }

    @Override
    public User get(Long id) {
        return null;
    }

    @Override
    public User update(User data) {
        return null;
    }

    @Override
    public Boolean delete(Long id) {
        return null;
    }

    private Integer getEmailCount(String email) {
        return jdbcTemplate.queryForObject(COUNT_USER_EMAIL_QUERY, of("email",email), Integer.class);
    }

    private SqlParameterSource getSqlParameterSource(User user) {
        return new MapSqlParameterSource()
                .addValue("firstName", user.getFirstName())
                .addValue("lastName", user.getLastName())
                .addValue("email", user.getEmail())
                .addValue("password", encoder.encode(user.getPassword()));
    }
    private String getVerificationUrl(String key, String type) {
        return ServletUriComponentsBuilder.fromCurrentContextPath().path("/user/verify/" + type + "/" + key).toUriString();
    }
}
