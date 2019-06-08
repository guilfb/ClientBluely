package service;

import meserreurs.MonException;
import metier.ClientEntity;
import metier.UtilisateurEntity;

import javax.persistence.EntityTransaction;
import java.util.List;

public class ClientService extends EntityService {

    public List<ClientEntity> consulterListeClients() throws MonException {
        List<ClientEntity> mesClients = null;
        try
        {
            EntityTransaction transac = startTransaction();
            transac.begin();
            mesClients = (List<ClientEntity>)
                    entitymanager.createQuery(
                            "SELECT a FROM ClientEntity a " +
                                    "ORDER BY a.idClient").getResultList();
            entitymanager.close();
        }catch (RuntimeException e) {
            new MonException("Erreur de lecture", e.getMessage());
        }catch (Exception e) {
            e.printStackTrace();
        }
        return mesClients;
    }

    public ClientEntity consulterClientById(int numero) throws MonException {
        List<ClientEntity> mesClients = null;
        ClientEntity client = new ClientEntity();
        try {
            EntityTransaction transac = startTransaction();
            transac.begin();

            mesClients = (List<ClientEntity>)
                    entitymanager.createQuery(
                            "SELECT a FROM ClientEntity a " +
                                    "WHERE a.idClient="+numero).getResultList();
            client = mesClients.get(0);
            entitymanager.close();
        }catch (RuntimeException e) {
            new MonException("Erreur de lecture", e.getMessage());
        }catch (Exception e) {
            e.printStackTrace();
        }
        return client;
    }

    public List<ClientEntity> consulterClientByLogin(String username) throws MonException {
        List<ClientEntity> mesClients = null;
        try {
            EntityTransaction transac = startTransaction();
            transac.begin();

            mesClients = (List<ClientEntity>)
                    entitymanager.createQuery(
                            "SELECT a FROM ClientEntity a " +
                                    "WHERE a.login='"+username+"'").getResultList();

            entitymanager.close();
        }catch (RuntimeException e) {
            new MonException("Erreur de lecture", e.getMessage());
        }catch (Exception e) {
            e.printStackTrace();
        }
        return mesClients;
    }
}
