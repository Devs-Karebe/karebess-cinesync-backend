package karebes.movies.backend.core.security.principal;

import karebes.movies.backend.modules.user.entities.UserEntity;
import karebes.movies.backend.modules.user.repositories.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository repository;

    public CustomUserDetailsService(
            UserRepository repository
    ){

        this.repository = repository;
    }

    @Override
    public UserDetails loadUserByUsername(String email){
        UserEntity user = repository.findByEmail(email)
                .orElseThrow(
                        () -> new UsernameNotFoundException("Usuário não encontrado")
                );

        return new UserPrincipal(user);
    }
}