package metier;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Objects;

@Entity
@Table(name = "utilise", schema = "autolib", catalog = "")
@IdClass(UtiliseEntityPK.class)
public class UtiliseEntity implements Serializable {
    private VehiculeEntity vehicule;
    private ClientEntity client;
    //private int vehicule;
    //private int client;
    private Timestamp date;
    private BorneEntity borneDepart;
    private BorneEntity borneArrivee;

/*
    @Id
    @Column(name = "vehicule")
    public int getVehicule() {
        return vehicule;
    }
    public void setVehicule(int vehicule) {
        this.vehicule = vehicule;
    }

    @Id
    @Column(name = "client")
    public int getClient() {
        return client;
    }
    public void setClient(int client) {
        this.client = client;
    }
*/

    @ManyToOne
    @JoinColumn(name = "vehicule", referencedColumnName = "idVehicule")
    public VehiculeEntity getVehicule() {
        return vehicule;
    }
    public void setVehicule(VehiculeEntity vehicule) {
        this.vehicule = vehicule;
    }

    @ManyToOne
    @JoinColumn(name = "client", referencedColumnName="idClient")
    public ClientEntity getClient() {
        return client;
    }
    public void setClient(ClientEntity client) {
        this.client = client;
    }

    @Id
    @Column(name = "date")
    public Timestamp getDate() {
        return date;
    }
    public void setDate(Timestamp date) {
        this.date = date;
    }

    @ManyToOne
    @JoinColumn(name = "borne_depart", referencedColumnName = "idBorne")
    public BorneEntity getBorneDepart() {
        return borneDepart;
    }
    public void setBorneDepart(BorneEntity borneDepart) {
        this.borneDepart = borneDepart;
    }

    @ManyToOne
    @JoinColumn(name = "borne_arrivee", referencedColumnName = "idBorne")
    public BorneEntity getBorneArrivee() {
        return borneArrivee;
    }
    public void setBorneArrivee(BorneEntity borneArrivee) {
        this.borneArrivee = borneArrivee;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UtiliseEntity that = (UtiliseEntity) o;
        return vehicule == that.vehicule &&
                client == that.client &&
                Objects.equals(date, that.date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(vehicule, client, date);
    }
}
