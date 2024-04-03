package com.team73.studyshare.model.data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.team73.studyshare.model.Visibility;
import com.team73.studyshare.model.Role;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.validation.constraints.NotNull;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;

/**
 * Represents a user in the system.
 */
@Entity
@Table(name = "_user")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties({"authorities", "credentialsNonExpired", "accountNonExpired", "accountNonLocked", "enabled", "username"})
public class User implements UserDetails {

    /**
     * The unique identifier for the user.
     */
    @Id
    @GeneratedValue
    private Long id;

    /**
     * The name of the user.
     */
    @NotNull
    @Column(name = "name", unique = true)
    private String name;

    /**
     * The email address of the user.
     */
    @NotNull
    @Column(name = "email", unique = true)
    private String email;

    /**
     * The password of the user.
     */
    @NotNull
    @Column(name = "password")
    @JsonIgnore
    private String password;

    /**
     * The visibility of the user (e.g., "Private" or "Public").
     */
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "visibility")
    private Visibility visibility;

    /**
     * Defines the user's role (permission level) using the "Role" enumeration type.
     */
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    private Role role;

    /**
     * Represents the description of the user, providing additional information or details about the user.     */
    @NotNull
    @Column(name = "description")
    private String description;

    /**
     * The timestamp when the user was created.
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at")
    private Date createdAt;

    /**
     * The list of modules associated with this user.
     */
    @ManyToMany(fetch = FetchType.EAGER, cascade = {CascadeType.DETACH, CascadeType.MERGE,
                    CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinTable(
            name = "module_user",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "module_id")
    )
    @JsonIgnore
    @Builder.Default
    private List<Module> modules = new ArrayList<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "last_quizzed_cardsets", joinColumns = @JoinColumn(name = "user_id"))
    @MapKeyColumn(name = "cardset_id")
    @Column(name = "last_quizzed_date")
    private Map<Long, Date> lastQuizzedCardSets = new HashMap<Long, Date>();

    /**
     * Adds a module to the list of modules associated with this user.
     *
     * @param tempModule The module to be added to the user's list of associated modules.
     */
    public void addModule(Module tempModule) {
        modules.add(tempModule);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public String getPassword() {
        return password;
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

    public void addLastQuizzed(Long cardId) {
        lastQuizzedCardSets.put(cardId, new Date());
    }

    public void removeLastQuizzed(Long cardId) {
        lastQuizzedCardSets.remove(cardId);
    }
}

