package co.edu.sena.adso.soporteya.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.edu.sena.adso.soporteya.dto.CategoriaDTO;
import co.edu.sena.adso.soporteya.dto.CategoriaForm;
import co.edu.sena.adso.soporteya.dto.OpcionDTO;
import co.edu.sena.adso.soporteya.model.Categoria;
import co.edu.sena.adso.soporteya.repository.CategoriaRepository;

@Service
@Transactional(readOnly = true)
public class CategoriaService {

    private final CategoriaRepository categorias;

    public CategoriaService(CategoriaRepository categorias) {
        this.categorias = categorias;
    }

    public List<CategoriaDTO> todas() {
        return categorias.findAllByOrderByNombreAsc().stream().map(CategoriaDTO::from).toList();
    }

    // Opciones para los <select> y las casillas: solo las activas
    public List<OpcionDTO> opciones() {
        return categorias.findByActivaTrueOrderByNombreAsc().stream()
                .map(c -> new OpcionDTO(c.getId(), c.getNombre()))
                .toList();
    }

    public CategoriaForm formulario(Long id) {
        return CategoriaForm.desde(buscar(id));
    }

    @Transactional
    public CategoriaDTO guardar(CategoriaForm form) {
        String nombre = form.getNombre().trim();
        boolean repetida = form.esNuevo()
                ? categorias.existsByNombreIgnoreCase(nombre)
                : categorias.existsByNombreIgnoreCaseAndIdNot(nombre, form.getId());
        if (repetida) {
            throw new NegocioException("nombre", "Ya existe la categoría «" + nombre + "»");
        }
        Categoria categoria = form.esNuevo() ? new Categoria(nombre, form.getDescripcion()) : buscar(form.getId());
        categoria.setNombre(nombre);
        categoria.setDescripcion(form.getDescripcion());
        return CategoriaDTO.from(categorias.save(categoria));
    }

    // No se borra: tiene tickets. Se desactiva y deja de aparecer en los formularios.
    @Transactional
    public CategoriaDTO cambiarEstado(Long id) {
        Categoria categoria = buscar(id);
        categoria.setActiva(!categoria.isActiva());
        return CategoriaDTO.from(categoria);
    }

    private Categoria buscar(Long id) {
        return categorias.findById(id).orElseThrow(() -> new NegocioException("No existe la categoría " + id));
    }
}