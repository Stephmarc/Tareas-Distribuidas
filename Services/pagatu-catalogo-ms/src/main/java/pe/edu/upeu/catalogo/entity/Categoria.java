package pe.edu.upeu.catalogo.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "categorias")
public class Categoria {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  public Long id;

  public String nombre;
}
