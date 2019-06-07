package service;

import meserreurs.MonException;
import metier.Reservation;

import javax.jms.*;

public class EnvoiReservation {

    private TopicSession session = null;
    private TopicPublisher producer;

    public boolean publier(Reservation uneDemande, Topic topic, TopicConnectionFactory cf) throws Exception {
        boolean ok = true;
        TopicConnection connection = null;

        try {
            connection = cf.createTopicConnection();
            session = connection.createTopicSession(false, Session.AUTO_ACKNOWLEDGE);

            producer = session.createPublisher(topic);

            connection.start();
            ObjectMessage message = session.createObjectMessage();
            message.setObject(uneDemande);

            producer.publish(message);
            producer.close();
            session.close();
            connection.close();
        } catch (JMSException j) {
            new MonException(j.getMessage());
            ok = false;
        } catch (Exception e) {
            new MonException(e.getMessage());
            ok = false;
        }
        return ok;
    }

}
