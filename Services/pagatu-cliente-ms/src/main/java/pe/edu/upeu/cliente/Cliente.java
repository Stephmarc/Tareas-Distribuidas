package pe.edu.upeu.cliente;

import jakarta.persistence.*;

@Entity
public class Cliente {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  public Long id;

  @Column(nullable = false)
  public String nombre;

  public String documento;
}
