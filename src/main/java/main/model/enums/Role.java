package main.model.enums;

import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

public enum Role {

  USER(Set.of(Permission.USER)),
  MODERATOR(Set.of(Permission.USER, Permission.MODERATE));

  private final Set<Permission> permission;

  Role(Set<Permission> permission) {
    this.permission = permission;
  }

  public Set<Permission> getPermission() {
    return permission;
  }

  public Set<SimpleGrantedAuthority> getAuthorities() {
    return permission.stream()
        .map(p -> new SimpleGrantedAuthority(p.getPermission()))
        .collect(Collectors.toSet());
  }
}
