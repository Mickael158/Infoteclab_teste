package com.example.backlogin.user;

import com.example.backlogin.config.PasswordEncoderConfig;
import com.example.backlogin.login.CustomUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoderConfig passwordEncoderConfig;

    public User getUserByMailAndPassword(String mail, String rawPassword) throws Exception {
        Optional<User> user = this.userRepository.getUserByMail(mail);

        if (user.isPresent() && passwordEncoderConfig.passwordEncoder().matches(rawPassword, user.get().getPassword())) {
            return user.get();
        }
        throw new Exception("mail or password error");
    }

    public CustomUserDetails getUserDetailsByUserName(String userName) throws Exception {
        Optional<User> user = this.userRepository.getUserByUsername(userName);

        if (user.isPresent()) {
            return new CustomUserDetails(
                    user.get().getUsername(),
                    user.get().getMail(),
                    user.get().getPassword(),
                    user.get().getRole());
        }
        throw new Exception("mail or password error");
    }

    public User getUserByUserName(String userName) throws Exception {
        Optional<User> user = this.userRepository.getUserByUsername(userName);

        if (user.isPresent()) {
            return user.get();
        }
        throw new Exception("mail or password error");
    }

    public User saveUser(User user) {
        user.setPassword(passwordEncoderConfig.passwordEncoder().encode(user.getPassword()));
        return this.userRepository.save(user);
    }
}
