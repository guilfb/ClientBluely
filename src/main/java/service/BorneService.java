package service;

import meserreurs.MonException;
import metier.BorneEntity;

import javax.persistence.EntityTransaction;
import java.util.List;

public class BorneService extends EntityService {

	public List<BorneEntity> consulterListeBornes() throws MonException {
		List<BorneEntity> mesBornes = null;
		try
		{
			EntityTransaction transac = startTransaction();
			transac.begin();
			mesBornes = (List<BorneEntity>)
					entitymanager.createQuery(
							"SELECT a FROM BorneEntity a " +
									"ORDER BY a.idBorne").getResultList();
			entitymanager.close();
		}catch (RuntimeException e) {
			new MonException("Erreur de lecture", e.getMessage());
		}catch (Exception e) {
			e.printStackTrace();
		}
		return mesBornes;
	}

	public BorneEntity consulterBorneById(int numero) throws MonException {
		List<BorneEntity> mesBornes = null;
		BorneEntity borne = new BorneEntity();
		try {
			EntityTransaction transac = startTransaction();
			transac.begin();

			mesBornes = (List<BorneEntity>)
						entitymanager.createQuery(
								"SELECT a FROM BorneEntity a " +
										"WHERE a.idBorne="+numero).getResultList();
			borne = mesBornes.get(0);
			entitymanager.close();
		}catch (RuntimeException e) {
			new MonException("Erreur de lecture", e.getMessage());
		}catch (Exception e) {
			e.printStackTrace();
		}
		return borne;
	}


	public List<BorneEntity> getListBorneByStationWithVehicule(int idStation) throws MonException {
		List<BorneEntity> mesBornes = null;
		try {
			EntityTransaction transac = startTransaction();
			transac.begin();

			mesBornes = (List<BorneEntity>)
					entitymanager.createQuery(
							"SELECT a FROM BorneEntity a " +
									"WHERE a.etatBorne=0 and a.station="+idStation).getResultList();
			entitymanager.close();
		}catch (RuntimeException e) {
			new MonException("Erreur de lecture", e.getMessage());
		}catch (Exception e) {
			e.printStackTrace();
		}
		return mesBornes;
	}

	public List<BorneEntity> getListBorneByStationWithNoVehicule(int idStation) throws MonException {
		List<BorneEntity> mesBornes = null;
		try {
			EntityTransaction transac = startTransaction();
			transac.begin();

			mesBornes = (List<BorneEntity>)
					entitymanager.createQuery(
							"SELECT a FROM BorneEntity a " +
									"WHERE a.etatBorne=1 and a.station="+idStation).getResultList();
			entitymanager.close();
		}catch (RuntimeException e) {
			new MonException("Erreur de lecture", e.getMessage());
		}catch (Exception e) {
			e.printStackTrace();
		}
		return mesBornes;
	}

	public int getNbParking(int idStation) throws MonException {
		List<BorneEntity> mesBornes = null;
		int nbVehicule = 0;

		try {
			EntityTransaction transac = startTransaction();
			transac.begin();

			mesBornes = (List<BorneEntity>)
					entitymanager.createQuery(
							"SELECT a FROM BorneEntity a " +
									"WHERE a.station="+idStation).getResultList();

			if(mesBornes != null) {
				for(BorneEntity borne: mesBornes) {
					if(borne.getEtatBorne() == 1) {
						nbVehicule++;
					}
				}
			}
			entitymanager.close();
		}catch (RuntimeException e) {
			new MonException("Erreur de lecture", e.getMessage());
		}catch (Exception e) {
			e.printStackTrace();
		}
		return nbVehicule;
	}
}
