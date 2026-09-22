package fr.endurance.user;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ProfileService {

    private final UserRepository users;

    public ProfileService(UserRepository users) {
        this.users = users;
    }

    @Transactional(readOnly = true)
    public User get(Long userId) {
        return users.findById(userId).orElseThrow(UnknownUserException::new);
    }

    public User update(Long userId, ProfileUpdate update) {
        User user = get(userId);
        user.updateProfile(update);
        return user;
    }
}
