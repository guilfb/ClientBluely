package metier;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Objects;

@Entity
@Table(name = "reservation", schema = "autolib", catalog = "")
//@IdClass(ReservationEntityPK.class)
public class ReservationEntity implements Serializable {
    private VehiculeEntity vehicule;
    private ClientEntity client;
    private Timestamp dateReservation;
    private Timestamp dateEcheance;

    @ManyToOne
    @JoinColumn(name = "vehicule", referencedColumnName = "idVehicule")
    public VehiculeEntity getVehicule() {
        return vehicule;
    }
    public void setVehicule(VehiculeEntity vehicule) {
        this.vehicule = vehicule;
    }

    @ManyToOne
    @JoinColumn(name = "client", referencedColumnName = "idClient", nullable = false)
    public ClientEntity getClient() {
        return client;
    }
    public void setClient(ClientEntity client) {
        this.client = client;
    }

    @Id
    @Column(name = "date_reservation")
    public Timestamp getDateReservation() {
        return dateReservation;
    }
    public void setDateReservation(Timestamp dateReservation) {
        this.dateReservation = dateReservation;
    }

    @Basic
    @Column(name = "date_echeance")
    public Timestamp getDateEcheance() {
        return dateEcheance;
    }
    public void setDateEcheance(Timestamp dateEcheance) {
        this.dateEcheance = dateEcheance;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ReservationEntity that = (ReservationEntity) o;
        return vehicule == that.vehicule &&
                client == that.client &&
                Objects.equals(dateReservation, that.dateReservation) &&
                Objects.equals(dateEcheance, that.dateEcheance);
    }

    @Override
    public int hashCode() {
        return Objects.hash(vehicule, client, dateReservation, dateEcheance);
    }
}
