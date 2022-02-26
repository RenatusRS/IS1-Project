package podsistem3;

import entiteti.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.annotation.Resource;
import javax.ejb.Schedule;
import javax.jms.*;
import javax.persistence.*;

public class Main {

	@Resource(lookup = "myQ")
	static Queue q;

	@Resource(lookup = "jms/__defaultConnectionFactory")
	static ConnectionFactory cf;

	@SuppressWarnings("empty-statement")
	public static void timer(EntityManager em) throws JMSException {
		JMSContext ct = cf.createContext();
		JMSConsumer c = ct.createConsumer(q, "for=" + 33);
		JMSProducer p = ct.createProducer();

		System.out.println("Backup");

		TextMessage tm = ct.createTextMessage("GetAll");
		tm.setIntProperty("to", 33);
		tm.setIntProperty("for", 1);

		while (c.receiveNoWait() != null);
		p.send(q, tm);
		ArrayList<List> all = (ArrayList) ((ObjectMessage) c.receive()).getObject();

		em.getTransaction().begin();

		for (int i = 0; i < all.get(0).size(); i++) {
			Mesto m = (Mesto) all.get(0).get(i);
			Mesto ogM = em.find(Mesto.class, m.getIdMes());

			if (ogM == null) em.persist(m);
			em.flush();
		}

		for (int i = 0; i < all.get(1).size(); i++) {
			Filijala f = (Filijala) all.get(1).get(i);
			Filijala ogF = em.find(Filijala.class, f.getIdFil());

			if (ogF == null) em.persist(f);
			em.flush();
		}

		for (int i = 0; i < all.get(2).size(); i++) {
			Komitent k = (Komitent) all.get(2).get(i);
			Komitent ogK = em.find(Komitent.class, k.getIdK());

			if (ogK == null) em.persist(k);
			else if (k.getSediste() != null) ogK.setSediste(k.getSediste());
			em.flush();
		}

		tm.setIntProperty("for", 2);
		p.send(q, tm);

		all = (ArrayList) ((ObjectMessage) c.receive()).getObject();

		for (int i = 0; i < all.get(1).size(); i++) {
			Racun r = (Racun) all.get(1).get(i);
			Racun ogR = em.find(Racun.class, r.getIdRac());

			if (ogR == null) em.persist(r);
			else {
				ogR.setBrojStavki(r.getBrojStavki());
				ogR.setStanje(r.getStanje());
				ogR.setStatus(r.getStatus());
			}
			em.flush();
		}

		for (int i = 0; i < all.get(2).size(); i++) {
			Stavka s = (Stavka) all.get(2).get(i);
			Stavka ogS = em.find(Stavka.class, s.getIdSta());

			if (ogS == null) em.persist(s);
			em.flush();
		}

		em.getTransaction().commit();

		c.close();
	}

	@SuppressWarnings("empty-statement")
	public static void main(String[] args) throws JMSException {
		JMSContext ct = cf.createContext();
		JMSConsumer c = ct.createConsumer(q, "for=" + 3);
		JMSProducer p = ct.createProducer();

		EntityManager em = Persistence.createEntityManagerFactory("podsistem3PU").createEntityManager();

		int count = 0;
		Message om = ct.createObjectMessage();
		TextMessage msg = ct.createTextMessage();

		while (true) {
			try {
				System.out.print("\n==================================\n[" + count++ + "] Waiting for message...\n");
				msg = (TextMessage) c.receive(15000);

				if (msg == null) {
					timer(em);
					continue;
				}

				System.out.println("Message recieved: " + msg.getText() + "[" + msg.getIntProperty("to") + "]");

				switch (msg.getText()) {
					case ("GetAll"):
						ArrayList<List> all = new ArrayList<>();

						all.add(em.createNamedQuery("Mesto.findAll", Mesto.class).getResultList());
						all.add(em.createNamedQuery("Filijala.findAll", Filijala.class).getResultList());
						all.add(em.createNamedQuery("Komitent.findAll", Komitent.class).getResultList());
						all.add(em.createNamedQuery("Racun.findAll", Racun.class).getResultList());
						all.add(em.createNamedQuery("Stavka.findAll", Stavka.class).getResultList());

						om = ct.createObjectMessage(all);
						break;
					case ("GetDiff"):
						break;
					default:
						throw new Exception("Unknown command!");
				}

				om.setIntProperty("status", 200);
				System.out.println("SUCCESS");

			} catch (Exception e) {
				System.out.print("\nBad Message: " + e.getMessage() + "\n");
				continue;
			}

			System.out.println("Sending message to ID " + msg.getIntProperty("to") + ".\n");
			om.setIntProperty("for", msg.getIntProperty("to"));
			p.send(q, om);
		}
	}

}
