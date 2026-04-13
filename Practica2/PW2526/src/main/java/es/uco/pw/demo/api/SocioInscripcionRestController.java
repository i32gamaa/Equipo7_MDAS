package es.uco.pw.demo.api;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import es.uco.pw.demo.model.domain.Socio;
import es.uco.pw.demo.model.domain.Inscripcion;
import es.uco.pw.demo.model.domain.Patron;
import es.uco.pw.demo.model.repository.SocioRepository;
import es.uco.pw.demo.model.repository.InscripcionRepository;
import es.uco.pw.demo.model.repository.PatronRepository;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.time.LocalDate;
import java.time.Period;

@RestController()
@RequestMapping(path="/api/socioinscripcion", produces="application/json")
public class SocioInscripcionRestController {

    SocioRepository socioRepository;
    InscripcionRepository inscripcionRepository;
    PatronRepository patronRepository;
    
    public SocioInscripcionRestController(SocioRepository socioRepository, InscripcionRepository inscripcionRepository, PatronRepository patronRepository){
        this.socioRepository = socioRepository;
        this.inscripcionRepository = inscripcionRepository;
        this.patronRepository=patronRepository;
        String sqlQueriesFileName = "./src/main/resources/db/sql.properties";
        this.socioRepository.setSQLQueriesFileName(sqlQueriesFileName);
        this.inscripcionRepository.setSQLQueriesFileName(sqlQueriesFileName);
        this.patronRepository.setSQLQueriesFileName(sqlQueriesFileName);
    }

    @GetMapping("/socios")
    public ResponseEntity<List<Socio>> getAllSocios(){
        List<Socio> socios = socioRepository.findAllSocios();
        return new ResponseEntity<>(socios, HttpStatus.OK);
    }

    @GetMapping("/inscripciones/individuales")
    public ResponseEntity<List<Inscripcion>> getIndividualInscripciones(){ // REFACTORIZACIÓN (Regla 11): getIndividuales -> getIndividualInscripciones
        List<Inscripcion> inscripciones = inscripcionRepository.findIndividualInscripciones();
        return new ResponseEntity<>(inscripciones, HttpStatus.OK);
    }

    @GetMapping("/inscripciones/familiares")
    public ResponseEntity<List<Inscripcion>> getFamiliarInscripciones(){ // REFACTORIZACIÓN (Regla 11): getFamiliares -> getFamiliarInscripciones
        List<Inscripcion> inscripciones = inscripcionRepository.findFamiliarInscripciones(); 
        return new ResponseEntity<>(inscripciones, HttpStatus.OK);
    }

    @GetMapping("/socios/{id}")
    public ResponseEntity<Socio> getSocioById(@PathVariable String id){
        Socio socio = socioRepository.findById(id);            
        if(socio != null){
            return new ResponseEntity<>(socio, HttpStatus.OK);
        } else{
            return new ResponseEntity<>(socio, HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/inscripciones/{userId}")
    public ResponseEntity<Inscripcion> getInscripcionById(@PathVariable String userId){
        Inscripcion inscripcion = inscripcionRepository.findByUserId(userId);            
        if(inscripcion != null){
            return new ResponseEntity<>(inscripcion, HttpStatus.OK);
        } else{
            return new ResponseEntity<>(inscripcion, HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping(path="/titular", consumes="application/json")
    public ResponseEntity<Socio> registerSocioHolder(@RequestBody Socio newSocio) { // REFACTORIZACIÓN (Regla 11): createSocioWithInscripcion -> registerSocioHolder

        Socio existingSocio = socioRepository.findById(newSocio.getSocioId()); // Adaptado (getSocioId)
        Patron existingPatron = patronRepository.findById(newSocio.getSocioId());

        if (existingSocio != null || existingPatron != null) {
            return new ResponseEntity<>(newSocio, HttpStatus.UNPROCESSABLE_ENTITY);
        }

        boolean isAdult = Period.between(newSocio.getBirthdate(), LocalDate.now()).getYears() >= 18;

        if (!isAdult) {
            return new ResponseEntity<>(newSocio, HttpStatus.UNPROCESSABLE_ENTITY);
        }

        newSocio.setAdult(true); // Adaptado
        newSocio.setHolderInscription(true); // Adaptado
        newSocio.setInscriptionDate(LocalDate.now());

        boolean isSocioSaved = socioRepository.addSocioAdult(newSocio); // REFACTORIZACIÓN: createdSocio -> isSocioSaved

        if (!isSocioSaved) {
            return new ResponseEntity<>(newSocio, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        Inscripcion inscripcion = new Inscripcion();
        inscripcion.setRegistrationDate(LocalDate.now()); // Adaptado
        inscripcion.setTotalAmount(300);
        inscripcion.setUserId(newSocio.getSocioId()); // Adaptado
        inscripcion.setRegisteredAdults(1);
        inscripcion.setRegisteredKids(0);

        boolean isInscripcionSaved = inscripcionRepository.addInscripcion(inscripcion); // REFACTORIZACIÓN: createdInscripcion -> isInscripcionSaved

        if (!isInscripcionSaved) {
            return new ResponseEntity<>(newSocio, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        Inscripcion savedInscripcion = inscripcionRepository.findByUserId(newSocio.getSocioId()); // REFACTORIZACIÓN: inscripcionDB -> savedInscripcion

        if (savedInscripcion == null) {
            return new ResponseEntity<>(newSocio, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        newSocio.setInscriptionId(savedInscripcion.getId());

        boolean isSocioUpdated = socioRepository.updateInscriptionId(newSocio); // REFACTORIZACIÓN: updatedSocio -> isSocioUpdated

        if (!isSocioUpdated) {
            return new ResponseEntity<>(newSocio, HttpStatus.INTERNAL_SERVER_ERROR);
        } else {
            return new ResponseEntity<>(newSocio, HttpStatus.CREATED);
        }
    }

    @PostMapping(path="/noTitular", consumes="application/json")
    public ResponseEntity<Socio> registerSocioToExistingInscripcion(@RequestBody Socio newSocio) { // REFACTORIZACIÓN (Regla 11): addSocioToInscripcion -> registerSocioToExistingInscripcion

        Socio existingSocio = socioRepository.findById(newSocio.getSocioId()); // Adaptado
        Patron existingPatron = patronRepository.findById(newSocio.getSocioId()); // Adaptado

        if (existingSocio != null || existingPatron != null) {
            return new ResponseEntity<>(newSocio, HttpStatus.UNPROCESSABLE_ENTITY);
        }

        boolean isAdult = Period.between(newSocio.getBirthdate(), LocalDate.now()).getYears() >= 18; // REFACTORIZACIÓN: esAdulto -> isAdult

        newSocio.setAdult(isAdult); // Adaptado
        newSocio.setInscriptionDate(LocalDate.now());

        if (!isAdult) {
            newSocio.setBoatDriver(false); // Adaptado
        }

        Inscripcion parentInscripcion = inscripcionRepository.findById(newSocio.getInscriptionId()); // REFACTORIZACIÓN: inscripcion -> parentInscripcion

        if (parentInscripcion == null) {
            return new ResponseEntity<>(newSocio, HttpStatus.UNPROCESSABLE_ENTITY);
        }

        int extraFee = isAdult ? 250 : 100; // REFACTORIZACIÓN: cuotaExtra -> extraFee

        parentInscripcion.setTotalAmount(parentInscripcion.getTotalAmount() + extraFee);

        if (isAdult) {
            parentInscripcion.setRegisteredAdults(parentInscripcion.getRegisteredAdults() + 1);
        } else {
            parentInscripcion.setRegisteredKids(parentInscripcion.getRegisteredKids() + 1);
        }

        if (parentInscripcion.getRegisteredAdults() > 2) {
            return new ResponseEntity<>(newSocio, HttpStatus.UNPROCESSABLE_ENTITY);
        }

        boolean isInscripcionUpdated = inscripcionRepository.update(parentInscripcion); // REFACTORIZACIÓN: updatedIns -> isInscripcionUpdated

        if (!isInscripcionUpdated) {
            return new ResponseEntity<>(newSocio, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        newSocio.setInscriptionId(parentInscripcion.getId());

        boolean isSocioSaved = socioRepository.addSocio(newSocio); // REFACTORIZACIÓN: socioCreated -> isSocioSaved

        if (isSocioSaved) {
            return new ResponseEntity<>(newSocio, HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>(newSocio, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/eliminarnotitular/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteSocioIfNotHolder(@PathVariable String id){ // REFACTORIZACIÓN (Regla 11): deleteIsNotHolder -> deleteSocioIfNotHolder
        Socio socio = this.socioRepository.findById(id);
        if(socio !=null){
            if(!socio.isHolderInscription()){ // Adaptado
                int inscriptionId = socio.getInscriptionId();
                Inscripcion parentInscripcion = this.inscripcionRepository.findById(inscriptionId); // REFACTORIZACIÓN: inscripcion -> parentInscripcion
                if(socio.isAdult()){ // Adaptado
                    parentInscripcion.setRegisteredAdults(parentInscripcion.getRegisteredAdults()-1);
                    parentInscripcion.setTotalAmount(parentInscripcion.getTotalAmount()-250);
                }else{
                    parentInscripcion.setRegisteredKids((parentInscripcion.getRegisteredKids()-1));
                    parentInscripcion.setTotalAmount(parentInscripcion.getTotalAmount()-100);
                }
                
                this.socioRepository.deleteIfNotHolder(id); // Adaptado al cambio de nombre en el repo
                this.inscripcionRepository.update(parentInscripcion);
                
            } else{
                System.out.println("[SocioInscripcionRestController]\n\tSocio is holder inscription...");
            }
        }
    }

    @PatchMapping(path="/modificarDatosSocio/{id}", consumes="application/json")
    public Socio updateSocioData(@PathVariable String id, @RequestBody Socio requestSocio) { // REFACTORIZACIÓN (Regla 11): patchSocio -> updateSocioData
        try{
            Socio existingSocio = this.socioRepository.findById(id); // REFACTORIZACIÓN: currentSocio -> existingSocio
            if(existingSocio != null){
                
                requestSocio.setSocioId(existingSocio.getSocioId()); // Adaptado
                
                if(requestSocio.getName() != null){
                    existingSocio.setName(requestSocio.getName());
                }

                if(requestSocio.getSurname() != null){
                    existingSocio.setSurname(requestSocio.getSurname());
                }

                if(requestSocio.getAddress() != null){
                    existingSocio.setAddress(requestSocio.getAddress());
                }

                if(requestSocio.getBirthdate() != null){
                    existingSocio.setBirthdate(requestSocio.getBirthdate());
                    existingSocio.setAdult(Period.between(requestSocio.getBirthdate(), LocalDate.now()).getYears() >= 18); // Adaptado
                }

                boolean isUpdated = socioRepository.updateSocio(existingSocio); // REFACTORIZACIÓN: resultOk -> isUpdated
                if(isUpdated){
                     return existingSocio;
                }
            }
        }catch(Exception e){
             return requestSocio;
        }
        return requestSocio;
    }   

    @PatchMapping(path="/modificarInscripcionNoTitular/{id}", consumes="application/json")
    public Socio transferSocioToAnotherInscripcion(@PathVariable String id, @RequestBody Socio requestSocio) { // REFACTORIZACIÓN (Regla 11): patchNoTitular -> transferSocioToAnotherInscripcion
        try {
            Socio existingSocio = this.socioRepository.findById(id); // REFACTORIZACIÓN: currentSocio -> existingSocio
            if (existingSocio != null) {
                Inscripcion oldInscripcion = this.inscripcionRepository.findById(existingSocio.getInscriptionId());

                if (oldInscripcion != null) {
                    if (existingSocio.isAdult()) { // Adaptado
                        oldInscripcion.setRegisteredAdults(oldInscripcion.getRegisteredAdults() - 1);
                        oldInscripcion.setTotalAmount(oldInscripcion.getTotalAmount() - 250);
                    } else {
                        oldInscripcion.setRegisteredKids(oldInscripcion.getRegisteredKids() - 1);
                        oldInscripcion.setTotalAmount(oldInscripcion.getTotalAmount() - 100);
                    }
                    this.inscripcionRepository.update(oldInscripcion);
                }

                if (requestSocio.getInscriptionId() != 0) {
                    existingSocio.setInscriptionId(requestSocio.getInscriptionId());

                    Inscripcion newInscripcion = this.inscripcionRepository.findById(requestSocio.getInscriptionId());
                    if (newInscripcion != null) {
                        if (existingSocio.isAdult()) { // Adaptado
                            newInscripcion.setRegisteredAdults(newInscripcion.getRegisteredAdults() + 1);
                            newInscripcion.setTotalAmount(newInscripcion.getTotalAmount() + 250);
                        } else {
                            newInscripcion.setRegisteredKids(newInscripcion.getRegisteredKids() + 1);
                            newInscripcion.setTotalAmount(newInscripcion.getTotalAmount() + 100);
                        }
                        this.inscripcionRepository.update(newInscripcion);
                    }
                }

                boolean isUpdated = socioRepository.updateInscriptionId(existingSocio); // REFACTORIZACIÓN: resultOk -> isUpdated
                if (isUpdated) {
                    return existingSocio;
                }
            }
        } catch (Exception e) {
            return requestSocio;
        }
        return requestSocio;
    }

    @PutMapping(path="/reemplazarSocio/{id}", consumes="application/json")
    @ResponseStatus(HttpStatus.OK)
    public void replaceSocio(@PathVariable String id, @RequestBody Socio requestSocio) { // REFACTORIZACIÓN (Regla 11): putSocio -> replaceSocio
        try{
            Socio existingSocio = this.socioRepository.findById(id); // REFACTORIZACIÓN: currentSocio -> existingSocio
            if(existingSocio != null){
                requestSocio.setSocioId(existingSocio.getSocioId()); // Adaptado
                requestSocio.setInscriptionDate(existingSocio.getInscriptionDate());
                requestSocio.setInscriptionId(existingSocio.getInscriptionId());
                requestSocio.setHolderInscription(existingSocio.isHolderInscription()); // Adaptado
                requestSocio.setAdult(Period.between(requestSocio.getBirthdate(), LocalDate.now()).getYears() >= 18); // Adaptado
                
                boolean isUpdated = socioRepository.updateAllSocioFields(requestSocio); // Adaptado al nombre del método del repo
                if(isUpdated){
                    System.out.println("[SocioInscripcionRestController]\n\tSocio update correct...");
                }
                else{
                    System.out.println("[SocioInscripcionRestController]\n\tSocio update incorrect...");
                }
            }
            else{
                System.out.println("[SocioInscripcionRestController]\n\tSocio not found...");
            }
        }catch(Exception e){
            System.out.println("[SocioInscripcionRestController]\n\tDatabase exception...");
        }
    }
}