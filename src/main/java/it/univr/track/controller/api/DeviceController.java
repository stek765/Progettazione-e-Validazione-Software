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
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

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

    // API per far connettere dispositivi all'endpoint del provisioning system
    @PostMapping("/devices/provision")
    public ResponseEntity<?> provisionDevice(@RequestBody Device device) {
        if (device.getStatus() == null) {
            device.setStatus(DeviceStatus.ACTIVE);
        }
        Device saved = deviceRepository.save(device);
        return ResponseEntity.ok(saved);
    }

    // --- API UTENTI (INVENTATE PER IL MODULO) ---

    // Device Login API (Verifica identità dispositivo)
    @PostMapping("/device/login")
    public ResponseEntity<?> deviceLogin(@RequestBody DeviceLoginRequest request) {
        if (request.getDeviceId() == null) {
            return ResponseEntity.badRequest().body("Device ID mancante");
        }

        Optional<Device> deviceOpt = deviceRepository.findById(request.getDeviceId());

        if (deviceOpt.isPresent()) {
            Device device = deviceOpt.get();
            // Verifica rudimentale: controlla se il nome corrisponde (se fornito) e se è
            // attivo
            if (request.getName() != null && !device.getName().equals(request.getName())) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Nome dispositivo non corrispondente");
            }

            if (device.getStatus() == DeviceStatus.ACTIVE) {
                return ResponseEntity.ok("Dispositivo autenticato: " + device.getName());
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Dispositivo non attivo");
            }
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Dispositivo non trovato");
    }

    // Register API
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody UserRegistrationDTO dto) {
        if (userRepository.findByUsername(dto.getUsername()).isPresent()) {
            return ResponseEntity.badRequest().body("Username già in uso");
        }

        // Controllo semplice password se necessario
        UserRegistered newUser = UserRegistered.builder()
                .username(dto.getUsername())
                .password(passwordEncoder.encode(dto.getPassword()))
                .email(dto.getEmail())
                .firstname(dto.getFirstname())
                .lastname(dto.getLastname())
                .gender(dto.getGender())
                .role(Role.USER) // Default role
                .city(dto.getCity())
                .address(dto.getAddress())
                .telephoneNumber(dto.getTelephoneNumber())
                .taxIdentificationNumber(dto.getTaxIdentificationNumber())
                .build();

        userRepository.save(newUser);
        return ResponseEntity.ok("Registrazione completata per l'utente " + dto.getUsername());
    }

    // User Management API - List all users
    @GetMapping("/users")
    public ResponseEntity<Iterable<UserRegistered>> listUsers() {
        return ResponseEntity.ok(userRepository.findAll());
    }

    // DTO locale per il login dispositivo
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
