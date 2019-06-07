package service;

import meserreurs.MonException;
import metier.UtiliseEntity;

import javax.persistence.EntityTransaction;
import javax.persistence.Query;
import java.util.List;

public class UtilisationService extends EntityService {

	public void insertUtilisation(UtiliseEntity uneReservation) throws MonException {
		try
		{

			EntityTransaction transac = startTransaction();
			transac.begin();
			entitymanager.persist(uneReservation);
			transac.commit();
			entitymanager.close();
		}
		catch (RuntimeException e)
		{
			new MonException("Erreur de lecture", e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void supprimerUtilisation(UtiliseEntity uneReservation) throws MonException {
		try
		{

			EntityTransaction transac = startTransaction();
			transac.begin();
			String qryString = "delete from UtiliseEntity s where s.client="+uneReservation.getClient();//mettre id reservation
			Query query = entitymanager.createQuery(qryString);
			int count = query.executeUpdate();
			transac.commit();
			entitymanager.close();
		}
		catch (RuntimeException e)
		{
			new MonException("Erreur de lecture", e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void modifierUtilisation(UtiliseEntity reservation) throws MonException {
		try
		{
			EntityTransaction transac = startTransaction();
			transac.begin();
			entitymanager.merge(reservation);
			entitymanager.flush();
			transac.commit();
		}
		catch (RuntimeException e)
		{
			new MonException("Erreur de lecture", e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public List<UtiliseEntity> consulterListeUtilisations(int idUtilisateur) throws MonException {
		List<UtiliseEntity> mesRes = null;
		try {
			EntityTransaction transac = startTransaction();
			transac.begin();
			mesRes = (List<UtiliseEntity>)
					entitymanager.createQuery(
							"SELECT o FROM UtiliseEntity o WHERE o.client="+idUtilisateur).getResultList();
			entitymanager.close();

		} catch (RuntimeException e) {
			new MonException("Erreur de lecture", e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return mesRes;
	}



}
