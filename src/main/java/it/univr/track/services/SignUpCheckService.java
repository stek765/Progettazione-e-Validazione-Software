package it.univr.track.services;

import org.springframework.stereotype.Service;

import java.util.regex.Pattern;

@Service
public class SignUpCheckService {


    public boolean checkSignUpData(String email,
                                   String password,
                                   String confirmPassword) {

        Pattern patternMail = Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
        boolean mailOk = patternMail.matcher(email).matches();

        // TODO: RIMUOVERE "pwd"
        // Pattern patternPassword = Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^A-Za-z0-9]).{8,}$");
        Pattern patternPassword = Pattern.compile("^(pwd|(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^A-Za-z0-9]).{8,})$");

        boolean passwordOk = password != null
                && patternPassword.matcher(password).matches();

        return mailOk && passwordOk && password.equals(confirmPassword);
    }
}
