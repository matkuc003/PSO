package com.example.PSO.models;

import com.sun.istack.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity(name = "users")
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long uid;
    @NotNull
    @Size(min = 3, max = 25)
    private String companyName;
    @NotNull
    @Size(min = 4)
    private String companyAddress;
    @NotNull
    @Size(min = 10, max = 10)
    private String companyNip;
    @NotNull
    @Size(min = 3, max = 20)
    private String name;
    @NotNull
    @Size(min = 3, max = 20)
    private String lastName;
    @NotNull
    @NotEmpty
    @Email
    private String email;
    @NotNull
    @Pattern(regexp = "^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[#?!@$%^&*-]).{8,}$",
            message = "Password must be greater than 8 char, have 1 small letter, 1 high letter, 1 digital and 1 special character")
    private String password;
    @Column
    private boolean status = true;
    @Column
    private LocalDate registrationDate = LocalDate.now();
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id",referencedColumnName = "uid"),
            inverseJoinColumns = @JoinColumn(name = "role_id",referencedColumnName ="rid"))
    private Set<Role> roles = new HashSet<>();

    //toooo
    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER)
    private List<Delegation> delegations;

    public void addRole(Role role) {
        this.roles.add(role);
        role.getUsers().add(this);
}

    public void removeRole(Role role) {
        this.roles.forEach(System.out::println);
        this.roles.remove(this.roles.stream().filter(r -> r.getRid().equals(role.getRid())).findFirst().get());
        role.getUsers().remove(role.getUsers().stream().filter(u -> u.getUid().equals(this.uid)).findFirst().get());
        this.roles.forEach(System.out::println);
    }

    public User(String companyName, String companyAddress, String companyNip, String name, String lastName, String email, String password) {
        this.companyName = companyName;
        this.companyAddress = companyAddress;
        this.companyNip = companyNip;
        this.name = name;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream()
                .map(r -> new SimpleGrantedAuthority(r.getRoleName()))
                .collect(Collectors.toSet());
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
