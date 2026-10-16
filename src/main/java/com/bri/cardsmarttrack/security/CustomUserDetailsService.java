package com.bri.cardsmarttrack.security;

import com.bri.cardsmarttrack.entity.Customer;
import com.bri.cardsmarttrack.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final CustomerRepository customerRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Customer customer = customerRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Nasabah tidak ditemukan dengan email: " + email));
        return UserPrincipal.create(customer);
    }

    @Transactional(readOnly = true)
    public UserDetails loadUserById(Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("Nasabah tidak ditemukan dengan id: " + id));
        return UserPrincipal.create(customer);
    }
}
