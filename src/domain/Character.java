package domain;

import java.io.Serializable;

/**
 * Personaje que representa a un pais con su tiro de poder unico.
 */
public class Character implements Serializable {
    private static final long serialVersionUID = 1L;

    private String country;
    private Power power;

    public Character(String country, Power power) {
        this.country = country;
        this.power = power;
    }

    public static Character createSouthKorea() {
        return new Character("Corea del Sur", new BlueAuraPower());
    }

    public static Character createCameroon() {
        return new Character("Camerun", new LightningPower());
    }

    public static Character createNigeria() {
        return new Character("Nigeria", new TornadoPower());
    }

    public static Character createUSA() {
        return new Character("Estados Unidos", new IllusionPower());
    }

    public static Character createColombia() {
        return new Character("Colombia", new FireShotPower());
    }

    public static Character createBrazil() {
        return new Character("Brasil", new FreezePower());
    }

    /**
     * Retorna el catalogo completo de personajes disponibles para permitir extension sencilla.
     * @return arreglo con todos los personajes registrados.
     */
    public static Character[] getCatalog() {
        return new Character[] {
            createSouthKorea(),
            createCameroon(),
            createNigeria(),
            createUSA(),
            createColombia(),
            createBrazil()
        };
    }

    public String getCountry() {
        return this.country;
    }

    public Power getPower() {
        return this.power;
    }
}