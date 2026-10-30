package pe.edu.upeu.orden;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import pe.edu.upeu.orden.client.ProductoClient;
import pe.edu.upeu.orden.dto.*;
import pe.edu.upeu.orden.entity.Orden;
import pe.edu.upeu.orden.event.OutboxService;
import pe.edu.upeu.orden.mapper.OrdenMapper;
import pe.edu.upeu.orden.repository.OrdenRepository;
import pe.edu.upeu.orden.service.*;

@ExtendWith(MockitoExtension.class)
class PagatuOrdenMsApplicationTests {

  @Mock
  OrdenRepository repository;

  @Mock
  OrdenMapper mapper;

  @Mock
  ProductoConsultaService productos;

  @Mock
  OutboxService outbox;

  @InjectMocks
  RegistroOrdenService service;

  @BeforeEach
  void persistencia() {
    when(repository.save(any())).thenAnswer(i -> {
      Orden o = i.getArgument(0);
      o.setId(1L);
      return o;
    });
  }

  @Test
  void calculaElPrecioDelCatalogoYPublicaSoloLaOrdenConfirmada() {
    when(productos.consultar(1L)).thenReturn(
      new ProductoClient.Producto(1L, "Prueba", new BigDecimal("15.50"))
    );
    service.crear(
      new CrearOrdenRequest(
        "YAPE_PLIN",
        List.of(new CrearOrdenRequest.Item(1L, 2))
      ),
      "usuario-1"
    );
    ArgumentCaptor<Orden> c = ArgumentCaptor.forClass(Orden.class);
    verify(repository).save(c.capture());
    assertEquals(new BigDecimal("31.00"), c.getValue().getTotal());
    assertEquals("PENDIENTE_PAGO", c.getValue().getEstado());
    assertEquals("usuario-1", c.getValue().getClienteId());
    verify(outbox).registrar(eq("orden-eventos"), any());
  }

  @Test
  void guardaCarritoSinInventarUnTotalCuandoElCatalogoFalla() {
    service.crear(
      new CrearOrdenRequest(
        "YAPE_PLIN",
        List.of(new CrearOrdenRequest.Item(1L, 2))
      ),
      "usuario-1"
    );
    ArgumentCaptor<Orden> c = ArgumentCaptor.forClass(Orden.class);
    verify(repository).save(c.capture());
    assertNull(c.getValue().getTotal());
    assertNull(c.getValue().getDetalles().getFirst().getPrecioUnitario());
    assertEquals("CARRITO", c.getValue().getEstado());
    verifyNoInteractions(outbox);
  }

  @Test
  void unaSumaParcialNuncaSePublicaComoTotal() {
    when(productos.consultar(1L)).thenReturn(
      new ProductoClient.Producto(1L, "Prueba", new BigDecimal("15.50"))
    );
    service.crear(
      new CrearOrdenRequest(
        "YAPE_PLIN",
        List.of(
          new CrearOrdenRequest.Item(1L, 2),
          new CrearOrdenRequest.Item(2L, 1)
        )
      ),
      "usuario-1"
    );
    ArgumentCaptor<Orden> c = ArgumentCaptor.forClass(Orden.class);
    verify(repository).save(c.capture());
    assertEquals(2, c.getValue().getDetalles().size());
    assertNull(c.getValue().getTotal());
    assertEquals("CARRITO", c.getValue().getEstado());
    verifyNoInteractions(outbox);
  }
}
