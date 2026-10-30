package pe.edu.upeu.catalogo.dto;

import java.math.BigDecimal;

public record ProductoResponse(Long id, String nombre, BigDecimal precio) {}
