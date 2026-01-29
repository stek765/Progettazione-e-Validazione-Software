package it.univr.track.controller.web;

import it.univr.track.dto.PasswordChangeDTO;
import it.univr.track.dto.UserProfileDTO;
import it.univr.track.dto.UserRegistrationDTO;
import it.univr.track.entity.UserRegistered;
import it.univr.track.entity.enumeration.Role;
import it.univr.track.user.UserRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Collections;

@Controller
@Profile("gestione-utenti")
/**
 * Controller principale per le funzionalità utente di base (Dashboard, Login,
 * Registrazione).
 */
public class UserWebController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/")
    public String home() {
        return "redirect:/dashboard";
    }

    // Prepara i dati di riepilogo da mostrare nella dashboard
    @GetMapping("/dashboard")
    public String dashboard(Authentication authentication, Model model) {
        String username = (authentication != null) ? authentication.getName() : "Ospite";
        boolean isAdmin = false;

        if (authentication != null) {
            isAdmin = authentication.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ADMIN"));
        }

        long activeDevicesCount = 0;
        long alertsCount = 0;

        model.addAttribute("activeDevicesCount", activeDevicesCount);
        model.addAttribute("alertsCount", alertsCount);
        model.addAttribute("username", username);
        model.addAttribute("isAdmin", isAdmin);
        return "dashboard";
    }

    @GetMapping("/signUp")
    public String signUpForm(Model model) {
        model.addAttribute("userRegistrationDTO", new UserRegistrationDTO());
        return "signUp";
    }

    // Gestisce la registrazione pubblica con controlli su password e username
    // duplicati
    @PostMapping("/signUp")
    public String doSignUp(@Valid @ModelAttribute("userRegistrationDTO") UserRegistrationDTO dto,
            BindingResult result,
            RedirectAttributes redirectAttributes,
            Model model) {

        if (!userRepository.findByUsername(dto.getUsername()).isEmpty()) {
            result.rejectValue("username", "duplicate", "Username già in uso.");
        }

        if (dto.getPassword() != null && !dto.getPassword().equals(dto.getConfirmPassword())) {
            result.rejectValue("confirmPassword", "mismatch", "Le password non coincidono.");
        }

        String password = dto.getPassword();
        boolean isComplex = password != null
                && password.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^A-Za-z0-9]).{8,}$");
        if (!isComplex) {
            result.rejectValue("password", "weak",
                    "La password deve contenere almeno 8 caratteri, una maiuscola, una minuscola, un numero e un carattere speciale.");
        }

        if (result.hasErrors()) {
            model.addAttribute("errorMessage", "Si prega di correggere gli errori nel modulo.");
            return "signUp";
        }

        UserRegistered newUser = UserRegistered.builder()
                .firstname(dto.getFirstname())
                .lastname(dto.getLastname())
                .username(dto.getUsername())
                .password(passwordEncoder.encode(dto.getPassword()))
                .email(dto.getEmail())
                .role(Role.USER)
                .gender(dto.getGender())
                .city(dto.getCity())
                .address(dto.getAddress())
                .telephoneNumber(dto.getTelephoneNumber())
                .taxIdentificationNumber(dto.getTaxIdentificationNumber())
                .build();

        userRepository.save(newUser);
        redirectAttributes.addFlashAttribute("successMessage", "Registrazione completata! Effettua il login.");
        return "redirect:/signIn";
    }

    @GetMapping("/signIn")
    public String signInForm(@RequestParam(required = false) String error,
            @RequestParam(required = false) String logout,
            Model model) {
        if (error != null) {
            model.addAttribute("errorMessage", "Username o password errati");
        }
        if (logout != null) {
            model.addAttribute("successMessage", "Logout effettuato con successo");
        }
        return "signIn";
    }

    @GetMapping("/profile")
    public String showProfile(Authentication authentication, Model model) {
        if (authentication == null)
            return "redirect:/signIn";
        String username = authentication.getName();

        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ADMIN"));
        model.addAttribute("isAdmin", isAdmin);

        userRepository.findByUsername(username).ifPresent(user -> {
            UserProfileDTO profile = new UserProfileDTO();
            profile.setUsername(user.getUsername());
            profile.setFirstname(user.getFirstname());
            profile.setLastname(user.getLastname());
            profile.setEmail(user.getEmail());
            profile.setGender(user.getGender());
            profile.setCity(user.getCity());
            profile.setAddress(user.getAddress());
            profile.setTelephoneNumber(user.getTelephoneNumber());
            profile.setTaxIdentificationNumber(user.getTaxIdentificationNumber());
            profile.setRole(user.getRole());

            model.addAttribute("userProfile", profile);
        });

        if (!model.containsAttribute("passwordChangeDTO")) {
            model.addAttribute("passwordChangeDTO", new PasswordChangeDTO());
        }

        return "profile";
    }

    @PostMapping("/profile")
    public String updateProfile(@Valid @ModelAttribute("userProfile") UserProfileDTO dto,
            BindingResult result,
            Authentication authentication,
            RedirectAttributes redirectAttributes,
            Model model) { // Aggiunto Model per gestire isAdmin in caso di errore

        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ADMIN"));

        if (result.hasErrors()) {
            model.addAttribute("isAdmin", isAdmin);
            if (!model.containsAttribute("passwordChangeDTO")) {
                model.addAttribute("passwordChangeDTO", new PasswordChangeDTO());
            }
            return "profile";
        }

        String username = authentication.getName();
        userRepository.findByUsername(username).ifPresent(user -> {
            // Aggiorna solo i campi modificabili
            user.setFirstname(dto.getFirstname());
            user.setLastname(dto.getLastname());
            // L'email spesso è vincolata all'account, qui la lasciamo inalterata
            user.setCity(dto.getCity());
            user.setAddress(dto.getAddress());
            user.setTelephoneNumber(dto.getTelephoneNumber());
            user.setTaxIdentificationNumber(dto.getTaxIdentificationNumber());

            userRepository.save(user);
        });

        redirectAttributes.addFlashAttribute("successMessage", "Profilo aggiornato con successo");
        return "redirect:/profile";
    }

    @PostMapping("/profile/change-password")
    public String changePassword(@Valid @ModelAttribute("passwordChangeDTO") PasswordChangeDTO dto,
            BindingResult result,
            Authentication authentication,
            RedirectAttributes redirectAttributes,
            Model model) {

        if (result.hasErrors()) {
            return showProfile(authentication, model);
        }

        String username = authentication.getName();
        var userOpt = userRepository.findByUsername(username);

        if (userOpt.isPresent()) {
            UserRegistered user = userOpt.get();

            // Verifica che la vecchia password corrisponda
            if (!passwordEncoder.matches(dto.getOldPassword(), user.getPassword())) {
                result.rejectValue("oldPassword", "mismatch", "La password attuale non è corretta");
                return showProfile(authentication, model);
            }

            // Verifica che la conferma della nuova password coincida
            if (!dto.getNewPassword().equals(dto.getConfirmPassword())) {
                result.rejectValue("confirmPassword", "mismatch", "Le password non coincidono");
                return showProfile(authentication, model);
            }

            // Validazione complessità password (stesse regole della registrazione)
            if (!dto.getNewPassword().matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^A-Za-z0-9]).{8,}$")) {
                result.rejectValue("newPassword", "weak",
                        "La password deve contenere almeno 8 caratteri, una maiuscola, una minuscola, un numero e un carattere speciale.");
                return showProfile(authentication, model);
            }

            user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
            userRepository.save(user);
            redirectAttributes.addFlashAttribute("successMessage", "Password modificata con successo!");
        }

        return "redirect:/profile";
    }

    /**
     * Utility per mostrare tutti gli utenti registrati
     */
    @GetMapping("/users")
    public String users(Model model) {
        Iterable<UserRegistered> users = userRepository.findAll();
        model.addAttribute("users", users);
        model.addAttribute("count", userRepository.count());
        return "users";
    }
}
