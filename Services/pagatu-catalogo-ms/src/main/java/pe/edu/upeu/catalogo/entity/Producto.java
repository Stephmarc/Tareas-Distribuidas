package pe.edu.upeu.catalogo.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "productos")
public class Producto {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  public Long id;

  public String nombre;
  public BigDecimal precio;

  @ManyToOne
  public Categoria categoria;
}
