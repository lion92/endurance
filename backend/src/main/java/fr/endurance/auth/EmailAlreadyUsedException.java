package fr.endurance.auth;

public class EmailAlreadyUsedException extends RuntimeException {

    public EmailAlreadyUsedException() {
        super("Cet email est déjà utilisé");
    }
}
