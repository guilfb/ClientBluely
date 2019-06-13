package controle;

import java.io.IOException;
import javax.jms.*;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

import metier.*;
import service.*;

import java.sql.Date;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import meserreurs.MonException;
import utilitaires.FonctionsUtiles;

@WebServlet(name = "/Controleur" ,urlPatterns={"/Controleur"})
public class Controleur extends HttpServlet {

    private static final long serialVersionUID = 10L;
    private static final String ACTION_TYPE = "action";

    private static final String CONTROLE_LOGIN = "controleLogin";
    private static final String CREER_COMPTE = "creerCompte";
    private static final String VALIDER_COMPTE = "insererClient";
    private static final String FORM_LOGIN = "formLogin";
    private static final String LOGOUT = "logout";
    private static final String INDEX = "index";
    private static final String MAP = "map";
    private static final String MON_ESPACE = "monEspace";
    private static final String RESERVER_VEHICULE = "reserverVehicule";
    private static final String RESERVER = "reserver";
    private static final String RENDRE_VEHICULE = "rendreVehicule";
    private static final String RENDRE = "rendre";
    private static final String RETIRER_VEHICULE = "retirerVehicule";
    private static final String RETIRER = "retirer";

    @Resource(lookup = "java:jboss/exported/topic/DemandeInscriptionJmsTopic")
    private Topic topic;

    @Resource(mappedName = "java:/ConnectionFactory")
    private TopicConnectionFactory cf;

    public Controleur() {
        super();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            TraiteRequete(request, response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // TODO Auto-generated method stub
        try {
            TraiteRequete(request, response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void TraiteRequete(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, MonException {
        // On récupère l'action
        String actionName = request.getParameter(ACTION_TYPE);

        if (INDEX.equals(actionName)) {
            this.getServletContext().getRequestDispatcher("/index.jsp").include(request, response);
        }
        else if (CREER_COMPTE.equals(actionName)) {
            this.getServletContext().getRequestDispatcher("/creerCompte.jsp").include(request, response);
        }
        else if (LOGOUT.equals(actionName)) {
            HttpSession session = request.getSession();
            session.invalidate();
            this.getServletContext().getRequestDispatcher("/index.jsp").include(request, response);
        }
        else if(VALIDER_COMPTE.equals(actionName)) {
            String destinationPage;
            try {
                ClientService clientService = new ClientService();

                String login = request.getParameter("login");
                String pwd = request.getParameter("password");
                String nom = request.getParameter("nom");
                String prenom = request.getParameter("prenom");
                String datenaissance = request.getParameter("datenaissance");
                java.util.Date initDate = new SimpleDateFormat("yyyy-MM-dd").parse(datenaissance);
                Date uneDate = new Date(initDate.getTime());

                List<ClientEntity> clientEntity = clientService.consulterClientByLogin(login);

                if(clientEntity.size() < 1) {
                    Client unClient = new Client();

                    pwd = FonctionsUtiles.md5(pwd);

                    unClient.setNom(nom);
                    unClient.setPrenom(prenom);
                    unClient.setDateNaissance(uneDate);
                    unClient.setLogin(login);
                    unClient.setMotdepasse(pwd);

                    EnvoiClient envoiClient = new EnvoiClient();
                    envoiClient.publier(unClient,topic,cf);

                    destinationPage = "/index.jsp";
                }else{
                    request.setAttribute("MesErreurs", "Utilisateur deja existant.");
                    destinationPage = "/creerCompte.jsp";
                }
            } catch (MonException e) {
                request.setAttribute("MesErreurs", e.getMessage());
                destinationPage = "/Erreur.jsp";
            } catch (Exception e) {
                request.setAttribute("MesErreurs", e.getMessage());
                destinationPage = "/Erreur.jsp";
            }
            this.getServletContext().getRequestDispatcher(destinationPage).include(request, response);
        }
        else if (FORM_LOGIN.equals(actionName)) {
            this.getServletContext().getRequestDispatcher("/formLogin.jsp").include(request, response);
        }
        else if (CONTROLE_LOGIN.equals(actionName)) {
            String destinationPage = "";
            String login = request.getParameter("login");
            String pwd = request.getParameter("pwd");
            String message = "";
            try {
                Service unService = new Service();
                ClientEntity unUtilisateur = unService.getUtilisateur(login);
                if (unUtilisateur != null) {
                    try {
                        String pwdmd5 = FonctionsUtiles.md5(pwd);
                        if (unUtilisateur.getMotdepasse().equals(pwdmd5)) {
                            HttpSession session = request.getSession();
                            session.setAttribute("id", unUtilisateur.getIdClient());
                            destinationPage = "/index.jsp";
                        } else {
                            message = "mot de passe erroné";
                            request.setAttribute("message", message);
                            destinationPage = "/formLogin.jsp";
                        }
                    } catch (NoSuchAlgorithmException e) {
                        request.setAttribute("MesErreurs", e.getMessage());
                        destinationPage = "/Erreur.jsp";
                    }
                } else {
                    message = "login erroné";
                    request.setAttribute("message", message);
                    destinationPage = "/formLogin.jsp";
                }
            } catch (MonException e) {
                request.setAttribute("MesErreurs", e.getMessage());
                destinationPage = "/Erreur.jsp";
            }

            this.getServletContext().getRequestDispatcher(destinationPage).include(request, response);
        }
        else if (MAP.equals(actionName)) {
            JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
            JsonObjectBuilder objectBuilder = Json.createObjectBuilder();

            HttpSession session = request.getSession();
            int idClient = (int)session.getAttribute("id");

            ReservationService resaService = new ReservationService();
            UtilisationService useService = new UtilisationService();

            BorneService borneService = new BorneService();
            List<BorneEntity> listeBornes = borneService.consulterListeBornes();

            for(BorneEntity borne : listeBornes) {
                objectBuilder
                        .add("idBorne", borne.getIdBorne())
                        .add("etatBorne", borne.getEtatBorne())
                        .add("stationBorne", Json.createObjectBuilder()
                                .add("idStation", borne.getStation().getIdStation())
                                .add("latitudeStation", borne.getStation().getLatitude())
                                .add("longitudeStation", borne.getStation().getLongitude())
                                .add("numAdresseStation", borne.getStation().getNumero())
                                .add("adresseStation", borne.getStation().getAdresse())
                                .add("cpStation", borne.getStation().getCodePostal())
                                .add("villeStation", borne.getStation().getVille()));

                if(borne.getVehicule() != null) {
                    objectBuilder
                            .add("vehiculeBorne", Json.createObjectBuilder()
                                    .add("idVehicule", borne.getVehicule().getIdVehicule())
                                    .add("rfidVehicule", borne.getVehicule().getRfid())
                                    .add("batterieVehicule", borne.getVehicule().getEtatBatterie())
                                    .add("dispoVehicule", borne.getVehicule().getDisponibilite())
                                    .add("latitudeVehicule", borne.getVehicule().getLatitude())
                                    .add("longitudeVehicule", borne.getVehicule().getLongitude())
                                    .add("typeVehicule", Json.createObjectBuilder()
                                            .add("idTV", borne.getVehicule().getTypeVehicule().getIdTypeVehicule())
                                            .add("categTV", borne.getVehicule().getTypeVehicule().getCategorie())
                                            .add("TV", borne.getVehicule().getTypeVehicule().getTypeVehicule())));
                }else{
                    objectBuilder.add("vehiculeBorne",Json.createObjectBuilder()
                            .add("idVehicule", "NO_VEHICULE"));
                }
                arrayBuilder.add(objectBuilder);
                objectBuilder = Json.createObjectBuilder();
            }

            List<ReservationEntity> listAllResa = resaService.consulterReservations();

            boolean alreadyReserved = false;
            LocalDateTime localDate = LocalDateTime.now();
            Timestamp currentDate = Timestamp.valueOf(localDate);

            List<ReservationEntity> resaPerso = new ArrayList<>();

            if (listAllResa != null) {
                for (ReservationEntity resa: listAllResa) {
                    if (currentDate.after(resa.getDateReservation()) && currentDate.before(resa.getDateEcheance())) {
                        if (resa.getClient().getIdClient() == idClient) {
                            alreadyReserved = true;
                            resaPerso.add(resa);
                        }
                    }
                }
            }

            boolean alreadyUsed = false;
            List<UtiliseEntity> listUse = useService.consulterListeUtilisations(idClient);
            List<UtiliseEntity> usePerso = new ArrayList<>();
            if(listUse != null) {
                for(UtiliseEntity use : listUse) {
                    if(use.getBorneArrivee() == null) {
                        alreadyUsed = true;
                        usePerso.add(use);
                    }
                }
            }

            if(resaPerso.size() < 1) {
                request.setAttribute("resaPerso", null);
            } else {
                request.setAttribute("resaPerso", resaPerso);
            }

            if(usePerso.size() < 1) {
                request.setAttribute("usePerso", null);
            } else {
                request.setAttribute("usePerso", resaPerso);
            }

            request.setAttribute("mesUsed", alreadyUsed);
            request.setAttribute("mesResa",alreadyReserved);
            request.setAttribute("bornes",arrayBuilder.build().toString());

            this.getServletContext().getRequestDispatcher("/map.jsp").include(request, response);
        }
        else if (MON_ESPACE.equals(actionName)) {
            String destinationPage;
            try {
                HttpSession session = request.getSession();
                int idUtilisateur = (int) session.getAttribute("id");

                ReservationService reservationService = new ReservationService();
                UtilisationService utiliseService = new UtilisationService();


                List<ReservationEntity> listResa = reservationService.consulterListeReservations(idUtilisateur);
                request.setAttribute("mesReservations", listResa);

                List<UtiliseEntity> listUtilise = utiliseService.consulterListeUtilisations(idUtilisateur);
                request.setAttribute("mesUtilisations", listUtilise);

                destinationPage = "/monEspace.jsp";
            } catch (MonException e) {
                request.setAttribute("MesErreurs", e.getMessage());
                destinationPage = "/Erreur.jsp";

            }
            this.getServletContext().getRequestDispatcher(destinationPage).include(request, response);
        }
        else if (RESERVER.equals(actionName)) {
            try {
                HttpSession session = request.getSession();
                int idClient = (int) session.getAttribute("id");

                VehiculeService vehiculeService = new VehiculeService();
                ClientService clientService = new ClientService();

                int numeroVehicule = Integer.parseInt(request.getParameter("idVehicule"));

                VehiculeEntity vehiculeEntity = vehiculeService.consulterVehiculeById(numeroVehicule);
                ClientEntity clientEntity = clientService.consulterClientById(idClient);

                Client client = new Client();
                client.setIdClient(clientEntity.getIdClient());
                client.setDateNaissance(clientEntity.getDateNaissance());
                client.setLogin(clientEntity.getLogin());
                client.setMotdepasse(clientEntity.getMotdepasse());
                client.setNom(clientEntity.getNom());
                client.setPrenom(clientEntity.getPrenom());

                TypeVehicule typeVehicule = new TypeVehicule();
                typeVehicule.setIdTypeVehicule(vehiculeEntity.getTypeVehicule().getIdTypeVehicule());
                typeVehicule.setCategorie(vehiculeEntity.getTypeVehicule().getCategorie());
                typeVehicule.setTypeVehicule(vehiculeEntity.getTypeVehicule().getTypeVehicule());

                Vehicule vehicule = new Vehicule();
                vehicule.setIdVehicule(vehiculeEntity.getIdVehicule());
                vehicule.setDisponibilite(vehiculeEntity.getDisponibilite());
                vehicule.setEtatBatterie(vehiculeEntity.getEtatBatterie());
                vehicule.setLatitude(vehiculeEntity.getLatitude());
                vehicule.setLongitude(vehiculeEntity.getLongitude());
                vehicule.setRfid(vehiculeEntity.getRfid());
                vehicule.setTypeVehicule(typeVehicule);

                LocalDateTime localDate = LocalDateTime.now();
                Timestamp currentDate = Timestamp.valueOf(localDate);
                Timestamp maxDate = Timestamp.valueOf(localDate.plusDays(1));

                Reservation reservation = new Reservation();
                reservation.setClient(client);
                reservation.setDateEcheance(maxDate);
                reservation.setDateReservation(currentDate);
                reservation.setVehicule(vehicule);

                EnvoiReservation unEnvoi = new EnvoiReservation();
                boolean ok = unEnvoi.publier(reservation,topic,cf);
                if (ok) {
                    // On retourne à la page d'accueil
                    this.getServletContext().getRequestDispatcher("/index.jsp").include(request, response);
                } else {
                    this.getServletContext().getRequestDispatcher("/Erreur.jsp").include(request, response);
                }
            } catch (MonException m) {
                m.printStackTrace();
                // On passe l'erreur à  la page JSP
                request.setAttribute("MesErreurs", m.getMessage());
                request.getRequestDispatcher("PostMessage.jsp").forward(request, response);
            } catch (Exception e) {
                e.printStackTrace();
                // On passe l'erreur à la page JSP
                request.setAttribute("MesErreurs", e.getMessage());
                request.getRequestDispatcher("PostMessage.jsp").forward(request, response);
            }
        }
        else if (RESERVER_VEHICULE.equals(actionName)) {
            String destinationPage;
            try {
                BorneService borneService = new BorneService();
                int idStation = Integer.parseInt(request.getParameter("idStation"));
                int nbV = Integer.parseInt(request.getParameter("idV"));

                List<BorneEntity> listBorne = borneService.getListBorneByStationWithVehicule(idStation);

                ReservationService resaService = new ReservationService();

                List<ReservationEntity> listAllResa = resaService.consulterReservations();

                LocalDateTime localDate = LocalDateTime.now();
                Timestamp currentDate = Timestamp.valueOf(localDate);
                List<ReservationEntity> resaAct = new ArrayList<>();

                if (listAllResa != null) {
                    for (ReservationEntity resa: listAllResa) {
                        if (currentDate.after(resa.getDateReservation()) && currentDate.before(resa.getDateEcheance())) {
                            resaAct.add(resa);
                        }
                    }
                }

                request.setAttribute("bornes", listBorne);
                request.setAttribute("resas", resaAct);

                destinationPage = "/reserverVehicule.jsp";
            } catch (Exception e) {
                request.setAttribute("MesErreurs", e.getMessage());
                destinationPage = "/Erreur.jsp";

            }

            this.getServletContext().getRequestDispatcher(destinationPage).include(request, response);
        }
        else if (RENDRE.equals(actionName)) {
            try {
                HttpSession session = request.getSession();

                int idClient = (int)session.getAttribute("id");
                int idBorne = Integer.parseInt(request.getParameter("idBorne"));

                UtiliseEntity useFinal = null;

                UtilisationService useService = new UtilisationService();

                List<UtiliseEntity> listUse = useService.consulterListeUtilisations(idClient);

                for (UtiliseEntity use : listUse) {
                    if (use.getBorneArrivee() == null) {
                        useFinal = use;
                    }
                }

                Utilise utilise = new Utilise();
                utilise.setVehicule(useFinal.getVehicule().getIdVehicule());
                utilise.setClient(useFinal.getClient().getIdClient());
                utilise.setDate(useFinal.getDate());
                utilise.setBorneDepart(useFinal.getBorneDepart().getIdBorne());
                utilise.setBorneArrivee(idBorne);

                EnvoiUtilise unEnvoi = new EnvoiUtilise();
                boolean ok = unEnvoi.publier(utilise,topic,cf);
                if (ok) {
                    this.getServletContext().getRequestDispatcher("/index.jsp").include(request, response);
                } else {
                    this.getServletContext().getRequestDispatcher("/Erreur.jsp").include(request, response);
                }
            } catch (MonException m) {
                m.printStackTrace();
                // On passe l'erreur à  la page JSP
                request.setAttribute("MesErreurs", m.getMessage());
                request.getRequestDispatcher("PostMessage.jsp").forward(request, response);
            } catch (Exception e) {
                e.printStackTrace();
                // On passe l'erreur à la page JSP
                request.setAttribute("MesErreurs", e.getMessage());
                request.getRequestDispatcher("PostMessage.jsp").forward(request, response);
            }
        }
        else if(RENDRE_VEHICULE.equals(actionName)){
            String destinationPage;
            try {
                BorneService borneService = new BorneService();
                HttpSession session = request.getSession();
                int idClient = (int)session.getAttribute("id");
                int idStation = Integer.parseInt(request.getParameter("idStation"));

                StationService stationService = new StationService();
                UtilisationService useService = new UtilisationService();

                List<UtiliseEntity> listUse = useService.consulterListeUtilisations(idClient);
                UtiliseEntity useFinal = null;

                for (UtiliseEntity use : listUse) {
                    if (use.getBorneArrivee() == null) {
                        useFinal = use;
                    }
                }

                List<BorneEntity> listBorne = borneService.getListBorneByStationWithNoVehicule(idStation);
                StationEntity station = stationService.consulterStationById(idStation);

                request.setAttribute("utilise", useFinal);
                request.setAttribute("bornes", listBorne);
                request.setAttribute("station", station);

                destinationPage = "/rendreVehicule.jsp";

            } catch (Exception e) {
                request.setAttribute("MesErreurs", e.getMessage());
                destinationPage = "/Erreur.jsp";
            }

            this.getServletContext().getRequestDispatcher(destinationPage).include(request, response);
        }
        else if (RETIRER.equals(actionName)) {
            try {
                HttpSession session = request.getSession();

                int idClient = (int)session.getAttribute("id");
                int idBorne = Integer.parseInt(request.getParameter("idBorne"));

                BorneService borneService = new BorneService();
                BorneEntity borne = borneService.consulterBorneById(idBorne);

                LocalDateTime localDate = LocalDateTime.now();
                Timestamp currentDate = Timestamp.valueOf(localDate);

                Utilise utilise = new Utilise();
                utilise.setVehicule(borne.getVehicule().getIdVehicule());
                utilise.setClient(idClient);
                utilise.setDate(currentDate);
                utilise.setBorneDepart(idBorne);

                ReservationService resaService = new ReservationService();
                VehiculeService vehiculeService = new VehiculeService();
                ClientService clientService = new ClientService();

                List<ReservationEntity> listResa = resaService.consulterListeReservations(idClient);
                ReservationEntity resaFinal = null;


                if (listResa != null) {
                    for (ReservationEntity resa: listResa) {
                        if (currentDate.after(resa.getDateReservation()) && currentDate.before(resa.getDateEcheance())) {
                            resaFinal = resa;
                            VehiculeEntity vehiculeEntity = vehiculeService.consulterVehiculeById(resaFinal.getVehicule().getIdVehicule());
                            ClientEntity clientEntity = clientService.consulterClientById(idClient);

                            Client client = new Client();
                            client.setIdClient(clientEntity.getIdClient());
                            client.setDateNaissance(clientEntity.getDateNaissance());
                            client.setLogin(clientEntity.getLogin());
                            client.setMotdepasse(clientEntity.getMotdepasse());
                            client.setNom(clientEntity.getNom());
                            client.setPrenom(clientEntity.getPrenom());

                            TypeVehicule typeVehicule = new TypeVehicule();
                            typeVehicule.setIdTypeVehicule(vehiculeEntity.getTypeVehicule().getIdTypeVehicule());
                            typeVehicule.setCategorie(vehiculeEntity.getTypeVehicule().getCategorie());
                            typeVehicule.setTypeVehicule(vehiculeEntity.getTypeVehicule().getTypeVehicule());

                            Vehicule vehicule = new Vehicule();
                            vehicule.setIdVehicule(vehiculeEntity.getIdVehicule());
                            vehicule.setDisponibilite(vehiculeEntity.getDisponibilite());
                            vehicule.setEtatBatterie(vehiculeEntity.getEtatBatterie());
                            vehicule.setLatitude(vehiculeEntity.getLatitude());
                            vehicule.setLongitude(vehiculeEntity.getLongitude());
                            vehicule.setRfid(vehiculeEntity.getRfid());
                            vehicule.setTypeVehicule(typeVehicule);

                            Reservation reservation = new Reservation();
                            reservation.setClient(client);
                            reservation.setDateEcheance(resa.getDateEcheance());
                            reservation.setDateReservation(resa.getDateReservation());
                            reservation.setVehicule(vehicule);

                            EnvoiReservation unEnvoi2 = new EnvoiReservation();
                            unEnvoi2.publier(reservation,topic,cf);
                            break;
                        }
                    }
                }

                EnvoiUtilise unEnvoi = new EnvoiUtilise();
                boolean ok = unEnvoi.publier(utilise,topic,cf);
                if (ok) {
                    // On retourne à la page d'accueil
                    this.getServletContext().getRequestDispatcher("/index.jsp").include(request, response);
                } else {
                    this.getServletContext().getRequestDispatcher("/Erreur.jsp").include(request, response);
                }

            } catch (MonException m) {
                m.printStackTrace();
                // On passe l'erreur à  la page JSP
                request.setAttribute("MesErreurs", m.getMessage());
                request.getRequestDispatcher("PostMessage.jsp").forward(request, response);
            } catch (Exception e) {
                e.printStackTrace();
                // On passe l'erreur à la page JSP
                request.setAttribute("MesErreurs", e.getMessage());
                request.getRequestDispatcher("PostMessage.jsp").forward(request, response);
            }
        }
        else if(RETIRER_VEHICULE.equals(actionName)){
            String destinationPage;
            try {
                HttpSession session = request.getSession();
                int idClient = (int)session.getAttribute("id");

                BorneService borneService = new BorneService();
                ReservationService resaService = new ReservationService();

                int idStation = Integer.parseInt(request.getParameter("idStation"));
                List<BorneEntity> listBorne = borneService.getListBorneByStationWithVehicule(idStation);

                LocalDateTime localDate = LocalDateTime.now();
                Timestamp currentDate = Timestamp.valueOf(localDate);

                List<ReservationEntity> resaAct = new ArrayList<>();
                List<ReservationEntity> resaBooked = new ArrayList<>();
                List<ReservationEntity> listAllResa = resaService.consulterReservations();

                if (listAllResa != null) {
                    for (ReservationEntity resa: listAllResa) {
                        if(currentDate.after(resa.getDateReservation()) && currentDate.before(resa.getDateEcheance())){
                            if(resa.getClient().getIdClient() == idClient) {
                                resaBooked.add(resa);
                            }else{
                                resaAct.add(resa);
                            }
                        }
                    }
                }

                request.setAttribute("resas", resaAct);
                request.setAttribute("mesResas", resaBooked);
                request.setAttribute("bornes", listBorne);

                destinationPage = "/retirerVehicule.jsp";

            } catch (Exception e) {
                request.setAttribute("MesErreurs", e.getMessage());
                destinationPage = "/Erreur.jsp";
            }

            this.getServletContext().getRequestDispatcher(destinationPage).include(request, response);
        }else{
            this.getServletContext().getRequestDispatcher("/Erreur.jsp").include(request, response);
        }
    }
}