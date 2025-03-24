package com.gestion_candidaturas.gestion_candidaturas.util;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.List;

/**
 * Clase de utilidad para la gestión de paginación y ordenamiento.
 * Proporciona métodos reutilizables para crear objetos Pageable a partir de parámetros de solicitud.
 */
public class PaginacionUtil {

    // Valor máximo de elementos por página para prevenir problemas de rendimiento
    private static final int MAX_PAGE_SIZE = 100;

    /**
     * Crea un objeto Pageable a partir de los parámetros de paginación y ordenamiento.
     *
     * @param page Número de página (0-indexed)
     * @param size Tamaño de la página
     * @param sort Array de strings con formato "propiedad,dirección" o solo "propiedad"
     * @return Objeto Pageable configurado
     */
    public static Pageable crearPageable(int page, int size, String[] sort){
        // Validar tamaño de pagina
        int pageSize = Math.min(size, MAX_PAGE_SIZE);

        // Construir ordenes de ordenamiento
        List<Sort.Order> orders = new ArrayList<>();

        if(sort[0].contains(",")){
            //sort=campo, direccion
            for (String sortParam : sort){
                String[] parts = sortParam.split(",");
                Sort.Direction direction = parts.length > 1 && parts[1].equalsIgnoreCase("desc")
                        ? Sort.Direction.DESC
                        : Sort.Direction.ASC;
                orders.add(new Sort.Order(direction, parts[0]));
            }
        }else {
            // sort=campo (asume ASC)
            orders.add(new Sort.Order(Sort.Direction.ASC, sort[0]));
        }
        return PageRequest.of(page, pageSize, Sort.by(orders));
    }

    /**
     * Crea un objeto Pageable con los valores predeterminados para un tipo de recurso específico.
     *
     * @param page Número de página (0-indexed)
     * @param size Tamaño de la página
     * @param defaultProperty Propiedad predeterminada para ordenar
     * @param defaultDirection Dirección predeterminada para ordenar
     * @return Objeto Pageable configurado
     */
    public static Pageable crearPageableDefault(int page, int size,
                                                String defaultProperty,
                                                Sort.Direction defaultDirection){
        // Validar tamaño de pagina
        int pageSize = Math.min(size, MAX_PAGE_SIZE);

        return PageRequest.of(page, pageSize, defaultDirection, defaultProperty);
    }

}
