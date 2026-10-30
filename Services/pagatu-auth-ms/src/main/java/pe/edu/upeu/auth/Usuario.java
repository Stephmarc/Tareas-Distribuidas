package pe.edu.upeu.auth;

import jakarta.persistence.*;
import java.util.*;

@Entity
@Table(name = "usuarios")
public class Usuario {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  public Long id;

  @Column(unique = true, nullable = false)
  public String username;

  @Column(nullable = false)
  public String password;

  @ElementCollection(fetch = FetchType.EAGER)
  @CollectionTable(
    name = "usuario_roles",
    joinColumns = @JoinColumn(name = "usuario_id")
  )
  @Column(name = "rol")
  public Set<String> roles = new HashSet<>();
}
