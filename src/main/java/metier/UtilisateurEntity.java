package metier;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;


@Entity
@Table(name = "utilisateur", schema = "autolib", catalog = "")
@NamedQuery(name = "UtilisateurEntity.rechercheNom", query = "select ct  from UtilisateurEntity  ct where ct.nomUtil = :name")
public class UtilisateurEntity implements Serializable {
    private Integer numUtil;
    private String nomUtil;
    private String motPasse;
    private String role;

    @Id
    @Column(name = "numutil", nullable = false)
    public Integer getNumUtil() {
        return numUtil;
    }
    public void setNumUtil(Integer numUtil) {
        this.numUtil = numUtil;
    }

    @Basic
    @Column(name = "nomutil", nullable = false, length = 100)
    public String getNomUtil() {
        return nomUtil;
    }
    public void setNomUtil(String nomUtil) {
        this.nomUtil = nomUtil;
    }

    @Basic
    @Column(name = "motpasse", nullable = false, length = 100)
    public String getMotPasse() {
        return motPasse;
    }
    public void setMotPasse(String motPasse) {
        this.motPasse = motPasse;
    }

    @Basic
    @Column(name = "role", nullable = false, length = 100)
    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
    @Override
    public int hashCode() {

        return Objects.hash(numUtil, nomUtil, motPasse, role);
    }
}
