package cli.tutoeasy.repository;

import cli.tutoeasy.model.entities.User;
import cli.tutoeasy.model.entities.UserRole;

public class TutorRepository extends BaseRepository<User> {

    public TutorRepository() {
        super(User.class);
    }

    public User findTutorById(int id) {
        User user = findById(id);
        if (user != null && user.getRol() == UserRole.tutor) {
            return user;
        }
        return null;
    }
}