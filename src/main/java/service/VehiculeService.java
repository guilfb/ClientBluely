package service;

import meserreurs.MonException;
import metier.VehiculeEntity;

import javax.persistence.EntityTransaction;
import java.util.List;

public class VehiculeService extends EntityService {

	public List<VehiculeEntity> consulterListeVehicules() throws MonException {
		List<VehiculeEntity> mesVehicules = null;
		try
		{
			EntityTransaction transac = startTransaction();
			transac.begin();
			mesVehicules = (List<VehiculeEntity>)
					entitymanager.createQuery(
							"SELECT a FROM VehiculeEntity a " +
									"ORDER BY a.idVehicule").getResultList();
			entitymanager.close();
		}catch (RuntimeException e) {
			new MonException("Erreur de lecture", e.getMessage());
		}catch (Exception e) {
			e.printStackTrace();
		}
		return mesVehicules;
	}

	public VehiculeEntity consulterVehiculeById(int numero) throws MonException {
		List<VehiculeEntity> mesVehicules = null;
		VehiculeEntity vehicule = new VehiculeEntity();
		try {
			EntityTransaction transac = startTransaction();
			transac.begin();

			mesVehicules = (List<VehiculeEntity>)
						entitymanager.createQuery(
								"SELECT a FROM VehiculeEntity a " +
										"WHERE a.idVehicule="+numero).getResultList();
			vehicule = mesVehicules.get(0);
			entitymanager.close();
		}catch (RuntimeException e) {
			new MonException("Erreur de lecture", e.getMessage());
		}catch (Exception e) {
			e.printStackTrace();
		}
		return vehicule;
	}
}
