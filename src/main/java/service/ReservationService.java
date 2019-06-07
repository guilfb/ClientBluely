package service;

import meserreurs.MonException;
import metier.ReservationEntity;

import javax.persistence.EntityTransaction;
import java.util.List;

public class ReservationService extends EntityService {

	public List<ReservationEntity> consulterListeReservations(int idUtilisateur) throws MonException {
		List<ReservationEntity> mesRes = null;
		try {
			EntityTransaction transac = startTransaction();
			transac.begin();
			mesRes = (List<ReservationEntity>)
					entitymanager.createQuery(
							"SELECT a FROM ReservationEntity a " +
									"WHERE a.client="+idUtilisateur).getResultList();
			entitymanager.close();
		} catch (RuntimeException e) {
			new MonException("Erreur de lecture", e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return mesRes;
	}
}
