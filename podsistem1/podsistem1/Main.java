package podsistem1;

import entiteti.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import javax.annotation.Resource;
import javax.jms.*;
import javax.persistence.*;

public class Main {

	@Resource(lookup = "myQ")
	static Queue q;

	@Resource(lookup = "jms/__defaultConnectionFactory")
	static ConnectionFactory cf;

	static JMSContext ct;
	static JMSConsumer c;
	static JMSProducer p;

	static class StatusException extends Exception {

		public int status;

		public StatusException(String error, int status) {
			super(error);
			this.status = status;
		}
	}

	public static void main(String[] args) throws JMSException {
		ct = cf.createContext();
		c = ct.createConsumer(q, "for=" + 1);
		p = ct.createProducer();

		EntityManager em = Persistence.createEntityManagerFactory("ApplicationClient1PU").createEntityManager();
		Mesto m;
		Filijala f;
		Komitent k;
		Boolean set;
		Message om = ct.createObjectMessage();
		TextMessage msg = ct.createTextMessage();
		int count = 0;

		System.out.println();

		while (true) {
			try {
				System.out.print("\n==================================\n[" + count++ + "] Waiting for message...\n");
				msg = (TextMessage) c.receive();

				System.out.println("Message recieved: " + msg.getText() + "[" + msg.getIntProperty("to") + "]");

				switch (msg.getText()) {
					case ("CreateMesto"):
						em.getTransaction().begin();
						m = new Mesto(1, msg.getStringProperty("postBr"), msg.getStringProperty("naziv"));
						em.persist(m);
						em.getTransaction().commit();

						om = ct.createObjectMessage("Napravljeno Mesto sa ID " + m.getIdMes() + ".");

						break;
					case ("CreateFilijala"):
						m = em.find(Mesto.class, msg.getIntProperty("idMes"));

						if (m == null) throw new StatusException("Mesto ne postoji!", 401);

						em.getTransaction().begin();
						(f = new Filijala(1, msg.getStringProperty("naziv"), msg.getStringProperty("adresa"))).setMesto(m);
						em.persist(f);
						em.getTransaction().commit();

						om = ct.createObjectMessage("Napravljena Filijala sa ID " + f.getIdFil() + ".");
						break;
					case ("CreateKomitent"):
						set = false;

						if (msg.propertyExists("sediste")) {
							if (em.find(Mesto.class, msg.getIntProperty("sediste")) == null) throw new StatusException("Mesto ne postoji!", 401);

							set = true;
						}

						forward(msg, 2);

						em.getTransaction().begin();
						k = new Komitent(1, msg.getStringProperty("naziv"), msg.getStringProperty("adresa"));
						if (set) k.setSediste(msg.getIntProperty("sediste"));
						em.persist(k);
						em.getTransaction().commit();

						om = ct.createObjectMessage("Napravljen Komitent sa ID " + k.getIdK() + ".");

						break;
					case ("ChangeKomitentSediste"):
						k = em.find(Komitent.class, msg.getIntProperty("idK"));

						if (k == null) throw new StatusException("Komitent ne postoji!", 401);

						if (em.find(Mesto.class, msg.getIntProperty("sediste")) == null) throw new StatusException("Mesto ne postoji!", 402);

						forward(msg, 2);

						em.getTransaction().begin();
						k.setSediste(msg.getIntProperty("sediste"));
						em.getTransaction().commit();

						om = ct.createObjectMessage("Promenjeno sediste komitenta.");

						break;
					case ("GetMestoAll"):
						om = ct.createObjectMessage((Serializable) em.createNamedQuery("Mesto.findAll", Mesto.class).getResultList());

						break;
					case ("GetFilijalaAll"):
						om = ct.createObjectMessage((Serializable) em.createNamedQuery("Filijala.findAll", Filijala.class).getResultList());
						break;
					case ("GetKomitentAll"):
						om = ct.createObjectMessage((Serializable) em.createNamedQuery("Komitent.findAll", Komitent.class).getResultList());
						break;
					case ("GetFilijalaId"):
						set = !em.createNamedQuery("Filijala.findByIdFil", Filijala.class).setParameter("idFil", msg.getIntProperty("idFil")).getResultList().isEmpty();
						System.out.println("Value: " + set);
						om = ct.createObjectMessage(set ? 1 : 0);
						break;
					case ("GetAll"):
						ArrayList<List> all = new ArrayList<>();

						all.add(em.createNamedQuery("Mesto.findAll", Mesto.class).getResultList());
						all.add(em.createNamedQuery("Filijala.findAll", Filijala.class).getResultList());
						all.add(em.createNamedQuery("Komitent.findAll", Komitent.class).getResultList());

						om = ct.createObjectMessage(all);

						break;
					default:
						throw new StatusException("Nepoznata komanda!", 201);
				}

				om.setIntProperty("status", 200);
				System.out.println("SUCCESS");

			} catch (StatusException e) {
				om = ct.createObjectMessage(e.getMessage());
				om.setIntProperty("status", e.status);
				System.out.println("FAILURE: " + e.getMessage());
			} catch (Exception e) {
				System.out.print("\nBad Message: " + e.getMessage() + "\n");
				continue;
			}

			System.out.println("Sending message to ID " + msg.getIntProperty("to") + ".\n");
			om.setIntProperty("for", msg.getIntProperty("to"));
			p.send(q, om);
		}
	}

	@SuppressWarnings("empty-statement")
	private static void forward(TextMessage msg, int to) throws JMSException {
		TextMessage copy = ct.createTextMessage(msg.getText());
		Enumeration srcProperties = msg.getPropertyNames();
		while (srcProperties.hasMoreElements()) {
			String propertyName = (String) srcProperties.nextElement();
			copy.setObjectProperty(propertyName, msg.getObjectProperty(propertyName));
		}

		copy.setIntProperty("to", 11);
		copy.setIntProperty("for", to);

		JMSConsumer c2 = ct.createConsumer(q, "for=" + 11);
		while (c2.receiveNoWait() != null);

		p.send(q, copy);

		c2.receive();
		c2.close();
	}

}
