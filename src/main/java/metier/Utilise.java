package metier;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Objects;


public class Utilise implements Serializable {
    private Vehicule vehicule;
    private Client client;
    private Timestamp date;


    public Vehicule getVehicule() {
        return vehicule;
    }

    public void setVehicule(Vehicule vehicule) {
        this.vehicule = vehicule;
    }


    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public Timestamp getDate() {
        return date;
    }

    public void setDate(Timestamp date) {
        this.date = date;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Utilise that = (Utilise) o;
        return vehicule == that.vehicule &&
                client == that.client &&
                Objects.equals(date, that.date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(vehicule, client, date);
    }
}
