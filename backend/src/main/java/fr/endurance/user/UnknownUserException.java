package fr.endurance.user;

/** Un jeton valide dont le compte n'existe plus : on le traite comme une absence d'identité. */
public class UnknownUserException extends RuntimeException {

    public UnknownUserException() {
        super("Compte introuvable");
    }
}
