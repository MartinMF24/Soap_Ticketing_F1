package com.f1.ticketing.soap.repository;

import com.f1.ticketing.soap.model.InventarioGrada;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InventarioGradaRepository extends JpaRepository<InventarioGrada, Long> {

    /**
     * Busca todas las gradas para un evento específico.
     */
    List<InventarioGrada> findByCodigoEvento(String codigoEvento);

    /**
     * Busca las gradas con stock disponible (stock > 0) para un evento.
     */
    List<InventarioGrada> findByCodigoEventoAndStockGreaterThan(String codigoEvento, Integer stock);

    /**
     * Busca una grada específica por evento y nombre de tribuna.
     */
    Optional<InventarioGrada> findByCodigoEventoAndTribuna(String codigoEvento, String tribuna);

    /**
     * Busca una grada específica sin distinguir mayúsculas/minúsculas.
     */
    Optional<InventarioGrada> findByCodigoEventoAndTribunaIgnoreCase(String codigoEvento, String tribuna);
}
