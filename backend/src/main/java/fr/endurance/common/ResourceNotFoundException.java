package fr.endurance.common;

/** Répond 404. Aussi quand la ressource existe mais appartient à un autre : on ne dit pas qu'elle existe. */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }
}
