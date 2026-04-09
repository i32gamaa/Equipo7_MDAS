/**
 * Enumeración {@code EmbarcacionType}:
 * Define los distintos tipos de embarcaciones disponibles en el club náutico.
 * 
 * <p>Esta enumeración se utiliza para clasificar las embarcaciones en función
 * de su tipo, facilitando su gestión, registro y asignación dentro del sistema.</p>
 * 
 * <p>Los valores posibles son:</p>
 * <ul>
 *   <li>{@link #VELERO} — Embarcación impulsada principalmente por el viento mediante velas.</li>
 *   <li>{@link #YATE} — Embarcación de recreo motorizada, generalmente de gran tamaño y lujo.</li>
 *   <li>{@link #CATAMARAN} — Embarcación con dos cascos paralelos, estable y rápida.</li>
 *   <li>{@link #LANCHA} — Embarcación pequeña motorizada, utilizada para paseos o transporte.</li>
 * </ul>
 * 
 * @author  
 * @version 1.0
 * @since 2025-11-10
 */
package es.uco.pw.demo.model.domain;

public enum EmbarcacionType {
    VELERO,
    YATE,
    CATAMARAN,
    LANCHA
}
