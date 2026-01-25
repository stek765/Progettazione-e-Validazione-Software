package it.univr.track.controller.web;

import it.univr.track.dto.UserEditDTO;
import it.univr.track.dto.UserRegistrationDTO;
import it.univr.track.entity.UserRegistered;
import it.univr.track.entity.enumeration.Role;
import it.univr.track.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

@Controller
@RequestMapping("/admin")
@Log4j2
public class AdminUserWebController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/users")
    public String listUsers(Model model) {
        model.addAttribute("users", userRepository.findAll());
        return "userListAdministration";
    }

    @PostMapping("/users/delete")
    public String deleteUser(@RequestParam("usernameToDelete") String username, Principal principal, RedirectAttributes redirectAttributes) {
        if (principal != null && principal.getName().equals(username)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Non puoi eliminare il tuo stesso account.");
            return "redirect:/admin/users";
        }

        userRepository.findByUsername(username).ifPresent(user -> {
            userRepository.delete(user);
        });
        log.info("Eliminato utente: {}", username);
        redirectAttributes.addFlashAttribute("successMessage", "Utente " + username + " eliminato con successo.");
        return "redirect:/admin/users";
    }

    @GetMapping("/users/register")
    public String registerUserForm(Model model) {
        model.addAttribute("userRegistrationDTO", new UserRegistrationDTO());
        model.addAttribute("formAction", "/admin/users/register");
        return "signUp";
    }

    @PostMapping("/users/register")
    public String registerUser(@Valid @ModelAttribute("userRegistrationDTO") UserRegistrationDTO dto,
                               BindingResult result,
                               Model model,
                               RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            model.addAttribute("formAction", "/admin/users/register");
            return "signUp";
        }

        if (userRepository.findByUsername(dto.getUsername()).isPresent()) {
            model.addAttribute("errorMessage", "Username già esistente.");
            model.addAttribute("formAction", "/admin/users/register");
            return "signUp";
        }

        UserRegistered user = new UserRegistered();
        user.setUsername(dto.getUsername());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setFirstname(dto.getFirstname());
        user.setLastname(dto.getLastname());
        user.setEmail(dto.getEmail());
        user.setRole(Role.USER);

        user.setGender(dto.getGender());
        user.setCity(dto.getCity());
        user.setAddress(dto.getAddress());
        user.setTelephoneNumber(dto.getTelephoneNumber());
        user.setTaxIdentificationNumber(dto.getTaxIdentificationNumber());

        userRepository.save(user);

        log.info("Registrato utente: {}", user.getUsername());

        redirectAttributes.addFlashAttribute("successMessage", "Nuovo utente registrato con successo.");
        return "redirect:/admin/users";
    }

    @GetMapping("/users/edit/{username}")
    public String editUserForm(@PathVariable String username, Model model, RedirectAttributes redirectAttributes) {
        return userRepository.findByUsername(username).map(user -> {
            UserEditDTO dto = UserEditDTO.builder()
                    .username(user.getUsername())
                    .firstname(user.getFirstname())
                    .lastname(user.getLastname())
                    .email(user.getEmail())
                    .gender(user.getGender())
                    .city(user.getCity())
                    .address(user.getAddress())
                    .telephoneNumber(user.getTelephoneNumber())
                    .taxIdentificationNumber(user.getTaxIdentificationNumber())
                    .build();

            model.addAttribute("userEditDTO", dto);
            return "editUserAdmin";
        }).orElseGet(() -> {
            redirectAttributes.addFlashAttribute("errorMessage", "Utente non trovato.");
            return "redirect:/admin/users";
        });
    }

    @PostMapping("/users/update")
    public String updateUser(@Valid @ModelAttribute("userEditDTO") UserEditDTO dto,
                             BindingResult result,
                             RedirectAttributes redirectAttributes) {


        if (result.hasErrors()) {
            return "editUserAdmin";
        }

        return userRepository.findByUsername(dto.getUsername()).map(user -> {
            user.setFirstname(dto.getFirstname());
            user.setLastname(dto.getLastname());
            user.setEmail(dto.getEmail());
            user.setGender(dto.getGender());
            user.setCity(dto.getCity());
            user.setAddress(dto.getAddress());
            user.setTelephoneNumber(dto.getTelephoneNumber());
            user.setTaxIdentificationNumber(dto.getTaxIdentificationNumber());

            // Handle Password
            if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
                user.setPassword(passwordEncoder.encode(dto.getPassword()));
            }

            userRepository.save(user);
            redirectAttributes.addFlashAttribute("successMessage", "Utente aggiornato con successo.");
            return "redirect:/admin/users";
        }).orElseGet(() -> {
            redirectAttributes.addFlashAttribute("errorMessage", "Errore durante l'aggiornamento.");
            return "redirect:/admin/users";
        });
    }

    // Also ensuring admin dashboard is reachable if not covered elsewhere
    @GetMapping("/dashboard")
    public String adminDashboard() {
        return "dashboard";
    }
}
