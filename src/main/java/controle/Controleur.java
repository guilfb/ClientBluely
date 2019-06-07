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
import java.lang.reflect.Type;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

import metier.*;
import service.*;

import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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

    private static final String AJOUTER_INSCRIPTION = "ajouteInscription";
    private static final String ENVOI_INSCRIPTION = "envoiInscription";

    private static final String ERREUR = "Erreur";
    private static final String FOOTER = "footer";
    private static final String HEADER = "header";
    private static final String NAVIGATION = "navigation";

    private static final String CONTROLE_LOGIN = "controleLogin";
    private static final String CREER_COMPTE = "creerCompte";
    private static final String FORM_LOGIN = "formLogin";
    private static final String INDEX = "index";
    private static final String MAP = "map";
    private static final String MON_ESPACE = "monEspace";
    private static final String RESERVER_VEHICULE = "reserverVehicule";
    private static final String RESERVER = "reserver";

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

    public void TraiteRequete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, MonException {
        // On récupère l'action
        String actionName = request.getParameter(ACTION_TYPE);

        // Si on veut afficher l'ensemble des demandes d'inscription
        if (AJOUTER_INSCRIPTION.equals(actionName)) {
            request.getRequestDispatcher("/AjouteInscription.jsp").forward(request, response);
        }
        else if (INDEX.equals(actionName)) {
            this.getServletContext().getRequestDispatcher("/index.jsp").include(request, response);
        }
        else if (CREER_COMPTE.equals(actionName)) {
            this.getServletContext().getRequestDispatcher("/creerCompte.jsp").include(request, response);
        }
        else if (FORM_LOGIN.equals(actionName)) {
            this.getServletContext().getRequestDispatcher("/formLogin.jsp").include(request, response);
        }
        else if (CONTROLE_LOGIN.equals(actionName)) {
            String destinationPage = "";
            String login = request.getParameter("login");
            String pwd = request.getParameter("pwd");
            String message ="";
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

            if (listAllResa != null) {
                for (ReservationEntity resa: listAllResa) {
                    if (currentDate.after(resa.getDateReservation()) && currentDate.before(resa.getDateEcheance())) {
                        if (resa.getClient().getIdClient() == idClient) {
                            alreadyReserved = true;
                        }
                    }
                }
            }

            boolean alreadyUsed = false;
            List<UtiliseEntity> listUse = useService.consulterListeUtilisations(idClient);
            if(listUse != null) {
                if(listUse.size() > 0) {
                    alreadyUsed = true;
                }
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

                // Recup de l'id véhicule
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

                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
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
                // On passe l'erreur à  la page JSP
                request.setAttribute("MesErreurs", m.getMessage());
                request.getRequestDispatcher("PostMessage.jsp").forward(request, response);
            } catch (Exception e) {
                // On passe l'erreur à la page JSP
                System.out.println("Erreur client  :" + e.getMessage());
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
                /*
                BorneEntity borneFinale = null;
                int i=0;
                boolean boucle = true;
                int id;
                int idRecup;

                while(boucle && i<nbV) {
                    for (BorneEntity borne : listBorne) {
                        id = borne.getVehicule().getTypeVehicule().getIdTypeVehicule();
                        idRecup = Integer.parseInt(request.getParameter("id"+i));

                        if (id == idRecup) {
                            borneFinale = borne;
                            boucle = false;
                            break;
                        }
                    }
                    i++;
                }
                */

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
        else if (ENVOI_INSCRIPTION.equals(actionName)) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            response.setContentType("text/html;charset=UTF-8");
            // On récupère les informations sisies
            String nom = request.getParameter("nom");
            String prenom = request.getParameter("prenom");

            if ((nom != null) && (prenom != null)) {
                try {
                    // On récupère la valeur des autres champs saisis par
                    // l'utilisateur
                    // on transfome la date
                    // au format Mysql java.sql.Date
                    String datenaissance = request.getParameter("datenaissance");
                    java.util.Date initDate = new SimpleDateFormat("dd/MM/yyyy").parse(datenaissance);
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                    String parsedDate = formatter.format(initDate);
                    initDate = formatter.parse(parsedDate);
                    Date uneDate = new Date(initDate.getTime());

                    String adresse = request.getParameter("adresse");
                    String cpostal = request.getParameter("cpostal");
                    String ville = request.getParameter("ville");

                    // On crée une demande d'inscription avec ces valeurs
                    Inscription unedemande = new Inscription();
                    unedemande.setNomcandidat(nom);
                    unedemande.setPrenomcandidat(prenom);
                    unedemande.setDatenaissance(uneDate);
                    unedemande.setAdresse(adresse);
                    unedemande.setCpostal(cpostal);
                    unedemande.setVille(ville);

                    // On envoie cette demande d'inscription dans le topic
                    EnvoiInscription unEnvoi = new EnvoiInscription();
                    boolean ok = unEnvoi.publier(unedemande,topic,cf);
                    if (ok) {
                        // On retourne àla page d'accueil
                        this.getServletContext().getRequestDispatcher("/index.jsp").include(request, response);
                    } else {
                        this.getServletContext().getRequestDispatcher("/Erreur.jsp").include(request, response);
                    }
                } catch (MonException m) {
                    // On passe l'erreur à  la page JSP
                    request.setAttribute("MesErreurs", m.getMessage());
                    request.getRequestDispatcher("PostMessage.jsp").forward(request, response);
                } catch (Exception e) {
                    // On passe l'erreur à la page JSP
                    System.out.println("Erreur client  :" + e.getMessage());
                    request.setAttribute("MesErreurs", e.getMessage());
                    request.getRequestDispatcher("PostMessage.jsp").forward(request, response);
                }
            }
        }else{
            this.getServletContext().getRequestDispatcher("/Erreur.jsp").include(request, response);
        }
    }
}