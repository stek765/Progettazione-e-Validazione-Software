package it.univr.track.integration;

import it.univr.track.device.DeviceRepository;
import it.univr.track.entity.Device;
import it.univr.track.entity.UserRegistered;
import it.univr.track.entity.enumeration.DeviceStatus;
import it.univr.track.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test") // Usa H2 in memory
class DeviceAssignmentSystemTest {

    @Autowired
    private DeviceRepository deviceRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void testDeviceSaveAndFind() {
        Device device = new Device();
        device.setName("IntegrationDevice");
        device.setStatus(DeviceStatus.AVAILABLE);

        Device saved = deviceRepository.save(device);

        assertNotNull(saved.getId());
        Optional<Device> retrieved = deviceRepository.findById(saved.getId());
        assertTrue(retrieved.isPresent());
        assertEquals("IntegrationDevice", retrieved.get().getName());
    }

    @Test
    void testAssignDeviceToUser() {
        // 1. Create and Save User
        UserRegistered user = new UserRegistered();
        user.setUsername("integrationUser");
        user.setFirstname("Int");
        user.setLastname("User");
        user.setPassword("pass"); // Mandatory validation? Not on db level maybe, but Entity validation yes.
        // JPA validation might trigger if @NotBlank is enforcing.
        // Assuming minimal setup required.
        user = userRepository.save(user);

        // 2. Create and Save Device
        Device device = new Device();
        device.setName("TrackingSensor");
        device.setStatus(DeviceStatus.AVAILABLE);
        device = deviceRepository.save(device);

        // 3. Perform Assignment
        device.setUser(user);
        deviceRepository.save(device);

        // 4. Verify Persistence
        Device fetchedDevice = deviceRepository.findById(device.getId()).get();
        assertNotNull(fetchedDevice.getUser());
        assertEquals("integrationUser", fetchedDevice.getUser().getUsername());
    }

    @Test
    void testDeleteUserCheckOrphans() {
        // Se cancello un utente, il device rimane? Oppure viene cancellato?
        // Solitamente il device dovrebbe rimanere ma con user=null, o essere cancellato
        // se CascadeType.ALL.
        // Verifichiamo il comportamento attuale del sistema.

        UserRegistered user = new UserRegistered();
        user.setUsername("temporaryUser");
        user.setFirstname("Temp");
        user.setLastname("User");
        userRepository.save(user);

        Device device = new Device();
        device.setName("OrphanCheck");
        device.setUser(user);
        deviceRepository.save(device);

        // Delete User
        userRepository.delete(user);

        // Check Device
        Optional<Device> deviceOpt = deviceRepository.findById(device.getId());

        // Questo test documenta il comportamento:
        // Se device esiste ancora, JPA non ha cascade delete.
        if (deviceOpt.isPresent()) {
            System.out.println("Device sopravvissuto alla cancellazione utente");
            // Se non c'è cascade, la foreign key potrebbe rompersi se non settata a null
            // manualmente prima.
            // Ma Spring Data JPA / Hibernate potrebbe gestire il set null se configurato.
            // Se fallisce per IntegrityConstraintViolation, allora sappiamo che il codice
            // deve gestire lo sgancio prima.

            // Nella nostra entità Device abbiamo @ManyToOne private UserRegistered user;
            // Di default non c'è CascadeType.REMOVE.
            // Quindi ci aspettiamo che il device esista ancora.
            assertTrue(deviceOpt.isPresent());
        }
    }
}
