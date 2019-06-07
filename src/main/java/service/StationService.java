package service;

import meserreurs.MonException;
import metier.StationEntity;

import javax.persistence.EntityTransaction;
import java.util.List;

public class StationService extends EntityService {

    public List<StationEntity> consulterListeStations() throws MonException {
        List<StationEntity> mesStations = null;
        try
        {
            EntityTransaction transac = startTransaction();
            transac.begin();
            mesStations = (List<StationEntity>)
                    entitymanager.createQuery(
                            "SELECT a FROM StationEntity a " +
                                    "ORDER BY a.idStation").getResultList();
            entitymanager.close();
        }catch (RuntimeException e) {
            new MonException("Erreur de lecture", e.getMessage());
        }catch (Exception e) {
            e.printStackTrace();
        }
        return mesStations;
    }

    public StationEntity consulterStationById(int numero) throws MonException {
        List<StationEntity> mesStations = null;
        StationEntity station = new StationEntity();
        try {
            EntityTransaction transac = startTransaction();
            transac.begin();

            mesStations = (List<StationEntity>)
                    entitymanager.createQuery(
                            "SELECT a FROM StationEntity a " +
                                    "WHERE a.idStation="+numero).getResultList();
            station = mesStations.get(0);
            entitymanager.close();
        }catch (RuntimeException e) {
            new MonException("Erreur de lecture", e.getMessage());
        }catch (Exception e) {
            e.printStackTrace();
        }
        return station;
    }
}
