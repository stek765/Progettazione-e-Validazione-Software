package it.univr.track.controller.web;

import it.univr.track.dto.mock.MockDevice;
import it.univr.track.entity.UserRegistered;
import it.univr.track.entity.Device;
import it.univr.track.user.UserRepository;
import it.univr.track.device.DeviceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.util.*;

@Controller
@RequestMapping("/web")
/**
 * Controller amministrativo per associare dispositivi e utenti.
 * Combina i dati di Device e User per una vista unificata.
 */
public class AdminWebController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DeviceRepository deviceRepository;

    // Aggrega utenti e stati dei dispositivi per la tabella di gestione
    @GetMapping("/utenti-e-dispositivi")
    public String deviceAndUsers(Model model, Authentication authentication) {

        List<UserViewModel> usersList = new ArrayList<>();
        List<UserRegistered> dbUsers = new ArrayList<>();
        userRepository.findAll().forEach(dbUsers::add);

        List<MockDevice> unassignedDevices = new ArrayList<>();
        Map<String, List<MockDevice>> devicesByUser = new HashMap<>();

        for (Device d : deviceRepository.findAll()) {
            MockDevice md = new MockDevice(d.getId().toString(), d.getName(),
                    (d.getStatus() != null ? d.getStatus().name() : "UNKNOWN"));

            if (d.getUser() != null) {
                devicesByUser.computeIfAbsent(d.getUser().getUsername(), k -> new ArrayList<>()).add(md);
            } else {
                unassignedDevices.add(md);
            }
        }

        for (UserRegistered u : dbUsers) {
            String roleName = (u.getRole() != null) ? u.getRole().name() : "USER";
            String color = roleName.equalsIgnoreCase("ADMIN") ? "#db2777" : "#2563eb";

            UserViewModel vm = new UserViewModel(u.getUsername(), roleName, color);
            vm.devices = devicesByUser.getOrDefault(u.getUsername(), new ArrayList<>());
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

        return "deviceAndUsers";
    }

    // Endpoint AJAX per spostare un dispositivo (assegna a utente o scollega)
    @PostMapping("/api/assign-device")
    @ResponseBody
    public ResponseEntity<?> assignDevice(@RequestParam("deviceId") Long deviceId,
            @RequestParam("targetId") String targetId) {

        Optional<Device> devOpt = deviceRepository.findById(deviceId);
        if (devOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Device not found");
        }
        Device device = devOpt.get();

        if (targetId.equals("unassigned-pool")) {
            device.setUser(null);
        } else if (targetId.startsWith("user-")) {
            String username = targetId.replace("user-", "");
            UserRegistered user = userRepository.findByUsername(username).orElse(null);
            if (user != null) {
                device.setUser(user);
            } else {
                return ResponseEntity.badRequest().body("User not found");
            }
        }

        deviceRepository.save(device);
        return ResponseEntity.ok(Map.of("status", "OK"));
    }

    @GetMapping("/device-mock/{id}")
    public String deviceProvisioning(@PathVariable("id") String id, Model model, Authentication authentication) {
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

        return "deviceProvisioning";
    }

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
                try {
                    KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
                    kpg.initialize(2048);
                    KeyPair kp = kpg.generateKeyPair();

                    String publicKey = Base64.getEncoder().encodeToString(kp.getPublic().getEncoded());
                    String privateKey = Base64.getEncoder().encodeToString(kp.getPrivate().getEncoded());

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
        try {
            Long dbId = Long.parseLong(id);
            return deviceRepository.findById(dbId)
                    .map(d -> new MockDevice(d.getId().toString(), d.getName(),
                            (d.getStatus() != null ? d.getStatus().name() : "UNKNOWN")))
                    .orElse(null);
        } catch (NumberFormatException e) {
            return null;
        }
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
