package ro.mpp2024.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ro.mpp2024.model.Volunteer;
import ro.mpp2024.repository.VolunteerRepository;

@Service
public class VolunteerDetailsService implements UserDetailsService {

    @Autowired
    private VolunteerRepository repository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Volunteer volunteer = repository.findByUsername(username);
        if (volunteer == null) {
            throw new UsernameNotFoundException("Voluntarul nu a fost gasit: " + username);
        }
        return new VolunteerUserDetails(volunteer);
    }
}
