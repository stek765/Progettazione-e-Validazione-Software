package it.univr.track.controller.web;

import it.univr.track.dto.UserProfileDTO;
import it.univr.track.dto.UserRegistrationDTO;
import it.univr.track.entity.UserRegistered;
import it.univr.track.entity.enumeration.Role;
import it.univr.track.repository.UserRepository;
import it.univr.track.services.SignUpCheckService;
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

@Controller
@Profile("gestione-utenti")
public class UserWebController {

    @Autowired
    private SignUpCheckService signUpCheckService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Home page - redirects to dashboard
     */
    @GetMapping("/")
    public String home() {
        return "redirect:/dashboard";
    }

    /**
     * Dashboard page with statistics overview
     */
    @GetMapping("/dashboard")
    public String dashboard(Authentication authentication, Model model) {
        String username = (authentication != null) ? authentication.getName() : "Ospite";
        boolean isAdmin = false;

        if (authentication != null) {
            isAdmin = authentication.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ADMIN"));
        }

        // TODO: Implementare logica reale quando le entità saranno collegate
        long activeDevicesCount = 0;
        long activeShipmentsCount = 0;
        long completedShipmentsCount = 0;
        long alertsCount = 0;

        model.addAttribute("activeDevicesCount", activeDevicesCount);
        model.addAttribute("activeShipmentsCount", activeShipmentsCount);
        model.addAttribute("completedShipmentsCount", completedShipmentsCount);
        model.addAttribute("alertsCount", alertsCount);
        model.addAttribute("username", username);
        model.addAttribute("isAdmin", isAdmin);
        // model.addAttribute("shipments", ...) // Rimosso come richiesto

        return "dashboard";
    }

    // Registrazione di un nuovo utente

    /**
     * Richiama la pagina signUp.html per visualizzare il form di registrazione.
     *
     * @return signup.html
     */
    @GetMapping("/signUp")
    public String signUpForm(Model model) {
        model.addAttribute("userRegistrationDTO", new UserRegistrationDTO());
        return "signUp";
    }

    /**
     * Gestisce la registrazione usando il DTO.
     */
    @PostMapping("/signUp")
    public String doSignUp(@Valid @ModelAttribute("userRegistrationDTO") UserRegistrationDTO dto,
            BindingResult result,
            RedirectAttributes redirectAttributes,
            Model model) {

        // Check for duplicate username
        if (!userRepository.findByUsername(dto.getUsername()).isEmpty()) {
            result.rejectValue("username", "duplicate", "Username già in uso.");
        }

        // Check password match
        if (dto.getPassword() != null && !dto.getPassword().equals(dto.getConfirmPassword())) {
            result.rejectValue("confirmPassword", "mismatch", "Le password non coincidono.");
        }

        // Check password complexity (using same logic as Service)
        // Regex: 8 chars, 1 uppercase, 1 lowercase, 1 digit, 1 special char OR "pwd"
        // (backdoor)
        String password = dto.getPassword();
        boolean isComplex = password != null
                && password.matches("^(pwd|(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^A-Za-z0-9]).{8,})$");
        if (!isComplex) {
            result.rejectValue("password", "weak",
                    "La password deve contenere almeno 8 caratteri, una maiuscola, una minuscola, un numero e un carattere speciale.");
        }

        if (result.hasErrors()) {
            model.addAttribute("errorMessage", "Si prega di correggere gli errori nel modulo.");
            return "signUp";
        }

        // All checks passed
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

    // Autenticazione

    /**
     * Richiama la pagina signIn.html.
     */
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

    // La logica di POST /signIn è gestita tipicamente da Spring Security.
    // Se il tuo SecurityConfig è configurato per intercettare il form login,
    // questo metodo potrebbe non servire a meno che tu non stia facendo auth
    // manuale.
    // Tuttavia, per "lasciare tutto com'è", manterrò la versione vecchia o mi
    // adeguerò
    // a quella nuova che NON HA una POST /signIn perché ci pensa Spring Security.
    // Il nuovo controller NON aveva il metodo POST /signIn.
    // Il vecchio controller AVEVA il metodo POST /signIn per fare controlli
    // manuali.
    // Visto che hai SecurityConfig, è probabile che tu debba usare quello.
    // Rimuovo il metodo manuale POST /signIn per affidarmi a Spring Security (come
    // il nuovo controller).

    // Visualizzazione Profilo

    @GetMapping("/profile")
    public String showProfile(Authentication authentication, Model model) {
        if (authentication == null)
            return "redirect:/signIn";
        String username = authentication.getName();

        // Pass isAdmin flag to the view for navbar consistency
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

        return "profile";
    }

    @PostMapping("/profile")
    public String updateProfile(@Valid @ModelAttribute("userProfile") UserProfileDTO dto,
            BindingResult result,
            Authentication authentication,
            RedirectAttributes redirectAttributes,
            Model model) { // Added Model for returning isAdmin if errors occur

        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ADMIN"));

        if (result.hasErrors()) {
            model.addAttribute("isAdmin", isAdmin);
            return "profile";
        }

        String username = authentication.getName();
        userRepository.findByUsername(username).ifPresent(user -> {
            // Aggiorna solo i campi modificabili
            user.setFirstname(dto.getFirstname());
            user.setLastname(dto.getLastname());
            // user.setEmail(dto.getEmail()); // Spesso l'email è legata all'account,
            // dipende dalle policy
            user.setCity(dto.getCity());
            user.setAddress(dto.getAddress());
            user.setTelephoneNumber(dto.getTelephoneNumber());
            user.setTaxIdentificationNumber(dto.getTaxIdentificationNumber());
            // Gender e Role potrebbero non essere modificabili

            userRepository.save(user);
        });

        redirectAttributes.addFlashAttribute("successMessage", "Profilo aggiornato con successo");
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
