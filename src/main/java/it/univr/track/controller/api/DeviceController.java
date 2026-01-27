package it.univr.track.controller.api;

import it.univr.track.dto.UserRegistrationDTO;
import it.univr.track.entity.Device;
import it.univr.track.entity.UserRegistered;
import it.univr.track.entity.enumeration.DeviceStatus;
import it.univr.track.entity.enumeration.Role;
import it.univr.track.repository.DeviceRepository;
import it.univr.track.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api")
@Profile("gestione-dispositivi")
public class DeviceController {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private DeviceRepository deviceRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    // --- API DISPOSITIVI ---

    @PostMapping("/devices/provision")
    @ResponseStatus(HttpStatus.CREATED) // Specifichiamo che crea una risorsa [cite: 68]
    public Device provisionDevice(@RequestBody Device device) {
        if (device.getStatus() == null) {
            device.setStatus(DeviceStatus.ACTIVE);
        }
        return deviceRepository.save(device);
    }

    @PostMapping("/device/login")
    public String deviceLogin(@RequestBody DeviceLoginRequest request) {
        // Cerchiamo il dispositivo o lanciamo 404 subito [cite: 109]
        Device device = deviceRepository.findById(request.getDeviceId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Dispositivo non trovato"));

        if (request.getName() != null && !device.getName().equals(request.getName())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Nome non corrispondente");
        }

        if (device.getStatus() != DeviceStatus.ACTIVE) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Dispositivo non attivo");
        }

        return "Dispositivo autenticato: " + device.getName();
    }

    // --- API UTENTI ---

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.OK)
    public String register(@RequestBody UserRegistrationDTO dto) {
        if (userRepository.findByUsername(dto.getUsername()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username già in uso");
        }

        userRepository.save(mapDtoToEntity(dto));
        return "Registrazione completata per " + dto.getUsername();
    }

    @GetMapping("/users")
    public Iterable<UserRegistered> listUsers() {
        return userRepository.findAll(); // Restituisce direttamente la lista [cite: 106]
    }

    // Metodo helper per tenere pulita la logica del controller [cite: 843]
    private UserRegistered mapDtoToEntity(UserRegistrationDTO dto) {
        return UserRegistered.builder()
                .username(dto.getUsername())
                .password(passwordEncoder.encode(dto.getPassword()))
                .email(dto.getEmail())
                .firstname(dto.getFirstname())
                .lastname(dto.getLastname())
                .gender(dto.getGender())
                .role(Role.USER)
                .city(dto.getCity())
                .address(dto.getAddress())
                .telephoneNumber(dto.getTelephoneNumber())
                .taxIdentificationNumber(dto.getTaxIdentificationNumber())
                .build();
    }

    public static class DeviceLoginRequest {
        private Long deviceId;
        private String name;

        public Long getDeviceId() {
            return deviceId;
        }

        public void setDeviceId(Long deviceId) {
            this.deviceId = deviceId;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}