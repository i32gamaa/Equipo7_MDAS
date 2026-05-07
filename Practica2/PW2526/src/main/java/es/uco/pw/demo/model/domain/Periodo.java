package es.uco.pw.demo.model.domain;

import java.time.LocalDate;

// SEMANA 4: Extraer Clase. Agrupamos fechas de inicio y fin en un objeto cohesivo.
public class Periodo {
    private LocalDate startDate;
    private LocalDate endDate;

    public Periodo(LocalDate startDate, LocalDate endDate) {
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }
}