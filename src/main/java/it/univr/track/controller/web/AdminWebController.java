package it.univr.track.controller.web;

import it.univr.track.dto.mock.MockDevice;
import it.univr.track.entity.UserRegistered;
import it.univr.track.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.util.*;

@Controller
@RequestMapping("/web")
public class AdminWebController {

    // --- MOCK DATA PERSISTENCE ---
    // Mappa username -> Lista Device (così persiste anche se ricarichiamo gli utenti dal DB)
    private static Map<String, List<MockDevice>> userDevicesMap = new HashMap<>();
    private static List<MockDevice> unassignedDevices = new ArrayList<>();

    static {
        // Init Mock Data
        unassignedDevices.add(new MockDevice("DEV-003", "Sensore Umidità", "Manutenzione"));
        unassignedDevices.add(new MockDevice("DEV-004", "Camera IP", "Attivo"));
    }

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/utenti-e-dispositivi")
    public String gestioneGlobale(Model model, Authentication authentication) {

        List<UserViewModel> usersList = new ArrayList<>();
        List<UserRegistered> dbUsers = new ArrayList<>();
        userRepository.findAll().forEach(dbUsers::add);

        for (UserRegistered u : dbUsers) {
            String roleName = (u.getRole() != null) ? u.getRole().name() : "USER";
            // Colore diverso se admin
            String color = roleName.equalsIgnoreCase("ADMIN") ? "#db2777" : "#2563eb";

            UserViewModel vm = new UserViewModel(u.getUsername(), roleName, color);

            // Recupera o inizializza i device mockati per questo utente
            userDevicesMap.putIfAbsent(u.getUsername(), new ArrayList<>());
            vm.devices = userDevicesMap.get(u.getUsername());

            usersList.add(vm);
        }

        model.addAttribute("usersList", usersList);
        model.addAttribute("unassignedDevices", unassignedDevices);

        boolean isAdmin = false;
        if (authentication != null) {
            model.addAttribute("username", authentication.getName());
            isAdmin = authentication.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ADMIN"));
        }
        model.addAttribute("isAdmin", isAdmin);

        return "gestioneGlobale";
    }

    @GetMapping("/device-mock/{id}")
    public String dettaglioDeviceMock(@PathVariable("id") String id, Model model, Authentication authentication) {
        // Find device in lists
        MockDevice device = findDeviceById(id);

        if (device == null) {
            device = new MockDevice(id, "Dispositivo " + id, "Sconosciuto");
        }

        model.addAttribute("device", device);

        boolean isAdmin = false;
        if (authentication != null) {
            isAdmin = authentication.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ADMIN"));
        }
        model.addAttribute("isAdmin", isAdmin);

        return "dettaglioDeviceMock";
    }

    // Endpoint for AJAX
    @PostMapping("/device-mock/{id}/provision")
    @ResponseBody
    public Map<String, String> toggleProvision(@PathVariable("id") String id,
                                               @RequestParam("provisioned") boolean provisioned,
                                               @RequestParam(value = "mac", required = false) String mac) {

        Map<String, String> response = new HashMap<>();
        MockDevice device = findDeviceById(id);

        if (device != null) {
            device.provisioned = provisioned;
            if (provisioned) {
                device.macAddress = mac;
                // Generazione Chiavi RSA
                try {
                    KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
                    kpg.initialize(2048);
                    KeyPair kp = kpg.generateKeyPair();

                    String publicKey = Base64.getEncoder().encodeToString(kp.getPublic().getEncoded());
                    String privateKey = Base64.getEncoder().encodeToString(kp.getPrivate().getEncoded());

                    // Salviamo la pubblica (simulazione) e ritorniamo la privata
                    // In un caso reale salveremmo publicKey nel DB associata al device
                    System.out.println("Generated Public Key for device " + id + ": " + publicKey);

                    response.put("status", "OK");
                    response.put("privateKey", privateKey);
                } catch (Exception e) {
                    e.printStackTrace();
                    response.put("status", "ERROR");
                    response.put("message", "Key Generation Failed");
                    return response;
                }
            } else {
                device.macAddress = null;
                response.put("status", "OK");
            }
            return response;
        }
        response.put("status", "ERROR");
        return response;
    }

    private MockDevice findDeviceById(String id) {
        // Cerca nella mappa
        for (List<MockDevice> list : userDevicesMap.values()) {
            for (MockDevice d : list) {
                if (d.id.equals(id)) return d;
            }
        }
        // Cerca nei non assegnati
        for (MockDevice d : unassignedDevices) {
            if (d.id.equals(id)) return d;
        }
        return null;
    }

    // --- SUPPORT CLASSES ---

    public static class UserViewModel {
        public String username;
        public String role;
        public String color; // Colore esadecimale per la UI
        public List<MockDevice> devices = new ArrayList<>();

        public UserViewModel(String username, String role, String color) {
            this.username = username;
            this.role = role;
            this.color = color;
        }
    }
}
