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
//import org.springframework.web.bind.annotation.RequestParam;
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
    //GET /api/socioinscripcion/socios -> lista todos los socios
    @GetMapping("/socios")
    public ResponseEntity<List<Socio>> getAllSocios(){
        List<Socio> socios = socioRepository.findAllSocios();
        ResponseEntity<List<Socio>> response = new ResponseEntity<>(socios, HttpStatus.OK);
        return response;
    }
    //GET /api/socioinscripcion/inscripciones/individuales -> lista inscripciones individuales
    @GetMapping("/inscripciones/individuales")
    public ResponseEntity<List<Inscripcion>> getIndividuales(){
        List<Inscripcion> inscripciones = inscripcionRepository.findIndividualInscripciones();
        ResponseEntity<List<Inscripcion>> response = new ResponseEntity<>(inscripciones, HttpStatus.OK);
        return response;
    }
    //GET /api/socioinscripcion/inscripciones/familiares -> lista inscripciones familiares
    @GetMapping("/inscripciones/familiares")
    public ResponseEntity<List<Inscripcion>> getFamiliares(){
        List<Inscripcion> inscripciones = inscripcionRepository.findFamiliarInscripciones(); // totalAmount > 300
        ResponseEntity<List<Inscripcion>> response = new ResponseEntity<>(inscripciones, HttpStatus.OK);
        return response;
    }
    //GET /api/socioinscripcion/socios/{id} -> obtiene un socio por id
    @GetMapping("/socios/{id}")
    public ResponseEntity<Socio> getSocioById(@PathVariable String id){
        Socio socio = socioRepository.findById(id);            
        ResponseEntity<Socio> response;
        if(socio != null){
            response = new ResponseEntity<>(socio, HttpStatus.OK);
        }
        else{
            response = new ResponseEntity<>(socio, HttpStatus.NOT_FOUND);
        }
        return response;
    }
    //GET /api/socioinscripcion/inscripciones/{userId} -> obtiene una inscripcion por userId
    @GetMapping("/inscripciones/{userId}")
    public ResponseEntity<Inscripcion> getInscripcionById(@PathVariable String userId){
        Inscripcion inscripcion = inscripcionRepository.findByUserId(userId);            
        ResponseEntity<Inscripcion> response;
        if(inscripcion != null){
            response = new ResponseEntity<>(inscripcion, HttpStatus.OK);
        }
        else{
            response = new ResponseEntity<>(inscripcion, HttpStatus.NOT_FOUND);
        }
        return response;
    }

    //AÑADIR SOCIO TITULAR + INSCRIPCION
    // POST /api/socioinscripcion/titular -> crea un nuevo socio titular con inscripcion
    @PostMapping(path="/titular", consumes="application/json")
    public ResponseEntity<Socio> createSocioWithInscripcion(@RequestBody Socio newSocio) {

        ResponseEntity<Socio> response;

        Socio existingSocio = socioRepository.findById(newSocio.getId());
        Patron existingPatron = patronRepository.findById(newSocio.getId());

        if (existingSocio != null || existingPatron != null) {
            response = new ResponseEntity<>(newSocio, HttpStatus.UNPROCESSABLE_ENTITY);
            return response;
        }


        boolean isAdult = Period.between(newSocio.getBirthdate(), LocalDate.now())
                .getYears() >= 18;

        if (!isAdult) {
            response = new ResponseEntity<>(newSocio, HttpStatus.UNPROCESSABLE_ENTITY);
            return response;
        }

        newSocio.setIsAdult(true);
        newSocio.setIsHolderInscription(true);
        newSocio.setInscriptionDate(LocalDate.now());

        boolean createdSocio = socioRepository.addSocioAdult(newSocio);

        if (!createdSocio) {
            response = new ResponseEntity<>(newSocio, HttpStatus.INTERNAL_SERVER_ERROR);
            return response;
        }

        Inscripcion inscripcion = new Inscripcion();
        inscripcion.setDate(LocalDate.now());
        inscripcion.setTotalAmount(300);
        inscripcion.setUserId(newSocio.getId());
        inscripcion.setRegisteredAdults(1);
        inscripcion.setRegisteredKids(0);

        boolean createdInscripcion = inscripcionRepository.addInscripcion(inscripcion);

        if (!createdInscripcion) {
            response = new ResponseEntity<>(newSocio, HttpStatus.INTERNAL_SERVER_ERROR);
            return response;
        }

        Inscripcion inscripcionDB = inscripcionRepository.findByUserId(newSocio.getId());

        if (inscripcionDB == null) {
            response = new ResponseEntity<>(newSocio, HttpStatus.INTERNAL_SERVER_ERROR);
            return response;
        }

        newSocio.setInscriptionId(inscripcionDB.getId());

        boolean updatedSocio = socioRepository.updateInscriptionId(newSocio);

        if (!updatedSocio) {
            response = new ResponseEntity<>(newSocio, HttpStatus.INTERNAL_SERVER_ERROR);
        } else {
            response = new ResponseEntity<>(newSocio, HttpStatus.CREATED);
        }

        return response;
    }

    //AÑADIR SOCIO NO TITULAR
    // POST /api/socioinscripcion/noTitular -> crea un nuevo socio no titular
    @PostMapping(path="/noTitular", consumes="application/json")
    public ResponseEntity<Socio> addSocioToInscripcion(@RequestBody Socio newSocio) {

        ResponseEntity<Socio> response;

        Socio existingSocio = socioRepository.findById(newSocio.getId());
        Patron existingPatron = patronRepository.findById(newSocio.getId());

        if (existingSocio != null || existingPatron != null) {
            response = new ResponseEntity<>(newSocio, HttpStatus.UNPROCESSABLE_ENTITY);
            return response;
        }

        boolean esAdulto = Period.between(newSocio.getBirthdate(), LocalDate.now())
                .getYears() >= 18;

        newSocio.setIsAdult(esAdulto);
        newSocio.setInscriptionDate(LocalDate.now());

        if (!esAdulto) {
            newSocio.setIsBoatDriver(false);
        }

        Inscripcion inscripcion = inscripcionRepository.findById(newSocio.getInscriptionId());

        if (inscripcion == null) {
            response = new ResponseEntity<>(newSocio, HttpStatus.UNPROCESSABLE_ENTITY);
            return response;
        }

        int cuotaExtra = esAdulto ? 250 : 100;

        inscripcion.setTotalAmount(inscripcion.getTotalAmount() + cuotaExtra);

        if (esAdulto) {
            inscripcion.setRegisteredAdults(inscripcion.getRegisteredAdults() + 1);
        } else {
            inscripcion.setRegisteredKids(inscripcion.getRegisteredKids() + 1);
        }

        if (inscripcion.getRegisteredAdults() > 2) {
            response = new ResponseEntity<>(newSocio, HttpStatus.UNPROCESSABLE_ENTITY);
            return response;
        }

        boolean updatedIns = inscripcionRepository.update(inscripcion);

        if (!updatedIns) {
            response = new ResponseEntity<>(newSocio, HttpStatus.INTERNAL_SERVER_ERROR);
            return response;
        }

        newSocio.setInscriptionId(inscripcion.getId());

        boolean socioCreated = socioRepository.addSocio(newSocio);

        if (socioCreated) {
            response = new ResponseEntity<>(newSocio, HttpStatus.CREATED);
        } else {
            response = new ResponseEntity<>(newSocio, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return response;
    }
    //DELETE /api/socioinscripcion/eliminarnotitular/{id} -> elimina un socio no titular por id
    @DeleteMapping("/eliminarnotitular/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteIsNotHolder(@PathVariable String id){
        Socio socio = this.socioRepository.findById(id);
        if(socio !=null){
            if(!socio.getIsHolderInscription()){
                int inscriptionId=socio.getInscriptionId();
                Inscripcion inscripcion=this.inscripcionRepository.findById(inscriptionId);
                if(socio.getIsAdult()){
                    inscripcion.setRegisteredAdults(inscripcion.getRegisteredAdults()-1);
                    inscripcion.setTotalAmount(inscripcion.getTotalAmount()-250);
                }else{
                    inscripcion.setRegisteredKids((inscripcion.getRegisteredKids()-1));
                    inscripcion.setTotalAmount(inscripcion.getTotalAmount()-100);
                }
                if(socio != null){
                    this.socioRepository.deleteIsNotHolder(id);
                    this.inscripcionRepository.update(inscripcion);
                }
            }else{
                System.out.println("[SocioInscripcionRestController]\n\tSocio is holder inscription...");

            }
        }
       
        
    }
    //PATCH /api/socioinscripcion/modificarDatosSocio/{id} -> actualiza los datos de un socio
    @PatchMapping(path="/modificarDatosSocio/{id}", consumes="application/json")
    public Socio patchSocio(@PathVariable String id, @RequestBody Socio requestSocio) {
       Socio response = requestSocio;
        try{
            // Get socio by id
            Socio currentSocio = this.socioRepository.findById(id);
            if(currentSocio != null){
                
                // Update properties
                requestSocio.setId(currentSocio.getId());
                
                if(requestSocio.getName() != null){
                    currentSocio.setName(requestSocio.getName());
                }

                if(requestSocio.getSurname() != null){
                    currentSocio.setSurname(requestSocio.getSurname());
                }

                if(requestSocio.getAddress() != null){
                    currentSocio.setAddress(requestSocio.getAddress());
                }

                if(requestSocio.getBirthdate() != null){
                    currentSocio.setBirthdate(requestSocio.getBirthdate());
                    currentSocio.setIsAdult(Period.between(requestSocio.getBirthdate(), LocalDate.now()).getYears() >= 18);
                }

                // Save updated resource
                boolean resultOk = socioRepository.updateSocio(currentSocio);
                if(resultOk){
                     response = currentSocio;
                }
            }
        }catch(Exception e){
             return requestSocio;
        }
        return response;
    }   
    //PATCH /api/socioinscripcion/modificarInscripcionNoTitular/{id} -> actualiza la inscripcion de un socio no titular
    @PatchMapping(path="/modificarInscripcionNoTitular/{id}", consumes="application/json")
    public Socio patchNoTitular(@PathVariable String id, @RequestBody Socio requestSocio) {
        Socio response = requestSocio;
        try {
            // Obtener socio actual
            Socio currentSocio = this.socioRepository.findById(id);
            if (currentSocio != null) {
                // Obtener inscripción actual del socio
                Inscripcion oldInscripcion = this.inscripcionRepository.findById(currentSocio.getInscriptionId());

                // Si existe inscripción actual, restar el socio de ella
                if (oldInscripcion != null) {
                    if (currentSocio.getIsAdult()) {
                        oldInscripcion.setRegisteredAdults(oldInscripcion.getRegisteredAdults() - 1);
                        oldInscripcion.setTotalAmount(oldInscripcion.getTotalAmount() - 250);
                    } else {
                        oldInscripcion.setRegisteredKids(oldInscripcion.getRegisteredKids() - 1);
                        oldInscripcion.setTotalAmount(oldInscripcion.getTotalAmount() - 100);
                    }
                    this.inscripcionRepository.update(oldInscripcion);
                }

                // Actualizar ID de inscripción del socio
                if (requestSocio.getInscriptionId() != 0) {
                    currentSocio.setInscriptionId(requestSocio.getInscriptionId());

                    // Obtener nueva inscripción
                    Inscripcion newInscripcion = this.inscripcionRepository.findById(requestSocio.getInscriptionId());
                    if (newInscripcion != null) {
                        // Sumar el socio a la nueva inscripción
                        if (currentSocio.getIsAdult()) {
                            newInscripcion.setRegisteredAdults(newInscripcion.getRegisteredAdults() + 1);
                            newInscripcion.setTotalAmount(newInscripcion.getTotalAmount() + 250);
                        } else {
                            newInscripcion.setRegisteredKids(newInscripcion.getRegisteredKids() + 1);
                            newInscripcion.setTotalAmount(newInscripcion.getTotalAmount() + 100);
                        }
                        this.inscripcionRepository.update(newInscripcion);
                    }
                }

                // Guardar cambios del socio
                boolean resultOk = socioRepository.updateInscriptionId(currentSocio);
                if (resultOk) {
                    response = currentSocio;
                }
            }
        } catch (Exception e) {
            return requestSocio;
        }
        return response;
    }
    // PUT /api/socioinscripcion/reemplazarSocio/{id} -> reemplaza un socio por id
    @PutMapping(path="/reemplazarSocio/{id}", consumes="application/json")
    @ResponseStatus(HttpStatus.OK)
    public void putSocio(@PathVariable String id, @RequestBody Socio requestSocio) {
        try{
            // Get course by id
            Socio currentSocio = this.socioRepository.findById(id);
            if(currentSocio != null){
                requestSocio.setId(currentSocio.getId());
                requestSocio.setInscriptionDate(currentSocio.getInscriptionDate());
                requestSocio.setInscriptionId(currentSocio.getInscriptionId());
                requestSocio.setIsHolderInscription(currentSocio.getIsHolderInscription());
                requestSocio.setIsAdult(Period.between(requestSocio.getBirthdate(), LocalDate.now()).getYears() >= 18);
                boolean resultOk = socioRepository.updateTodo(requestSocio);
                if(resultOk){
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




