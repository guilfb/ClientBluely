package service;

import meserreurs.MonException;
import metier.UtiliseEntity;

import javax.persistence.EntityTransaction;
import javax.persistence.Query;
import java.util.List;

public class UtilisationService extends EntityService {

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

	public List<UtiliseEntity> getUtiliseByClient(int idClient) throws MonException {
		List<UtiliseEntity> mesRes = null;
		try {
			EntityTransaction transac = startTransaction();
			transac.begin();
			mesRes = (List<UtiliseEntity>)
					entitymanager.createQuery(
							"SELECT o FROM UtiliseEntity o WHERE o.client="+idClient+" and o.borneArrivee="+null).getResultList();
			entitymanager.close();

		} catch (RuntimeException e) {
			new MonException("Erreur de lecture", e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return mesRes;
	}

	public List<UtiliseEntity> consulterUtilisations() throws MonException {
		List<UtiliseEntity> mesRes = null;
		try {
			EntityTransaction transac = startTransaction();
			transac.begin();
			mesRes = (List<UtiliseEntity>)
					entitymanager.createQuery(
							"SELECT o FROM UtiliseEntity o").getResultList();
			entitymanager.close();

		} catch (RuntimeException e) {
			new MonException("Erreur de lecture", e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return mesRes;
	}



}
