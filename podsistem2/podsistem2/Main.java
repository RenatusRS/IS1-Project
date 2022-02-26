package podsistem2;

import entiteti.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
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

		c = ct.createConsumer(q, "for=" + 2);
		p = ct.createProducer();

		EntityManager em = Persistence.createEntityManagerFactory("podsistem2PU").createEntityManager();
		Racun r1, r2;
		Stavka s1, s2;
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
					case ("OpenRacun"):
						if (em.find(Komitent.class, msg.getIntProperty("idK")) == null) throw new StatusException("Komitent ne postoji!", 401);
						
						if (!checkFil(msg.getIntProperty("idFil"))) throw new StatusException("Filijala ne postoji!", 402);

						em.getTransaction().begin();
						r1 = new Racun(0, 'A', 0, msg.getDoubleProperty("minus"), 0, new Date(), msg.getIntProperty("idFil"), msg.getIntProperty("idK"));
						em.persist(r1);
						em.getTransaction().commit();

						om = ct.createObjectMessage("Krejiran racun sa ID " + r1.getIdRac() + ".");

						break;
					case ("CloseRacun"):
						r1 = em.find(Racun.class, msg.getIntProperty("idRac"));

						if (r1 == null) throw new StatusException("Racun ne postoji!", 401);
						if (r1.getStatus() == 'Z') throw new StatusException("Racun je vec zatvoren!", 402);

						em.getTransaction().begin();
						r1.setStatus('Z');
						em.persist(r1);
						em.getTransaction().commit();

						om = ct.createObjectMessage(msg.getText());

						break;
					case ("CreateStavkaRazmena"):
						r1 = em.find(Racun.class, msg.getIntProperty("idRac1"));
						if (r1 == null) throw new StatusException("Racun 1 ne postoji!", 401);

						r2 = em.find(Racun.class, msg.getIntProperty("random"));
						if (r2 == null) throw new StatusException("Racun 2 ne postoji!", 402);

						if (r1.getStatus() == 'B') throw new StatusException("Racun 1 je blokiran!", 403);

						if (r1.getStatus() == 'Z') throw new StatusException("Racun 1 je zatvoren!", 404);
						if (r2.getStatus() == 'Z') throw new StatusException("Racun 2 je zatvoren!", 405);

						em.getTransaction().begin();
						s1 = new Stavka(0, getStavkaRacun(r1.getIdRac(), em).size() + 1, new Date(), msg.getDoubleProperty("iznos"), 'P');
						s1.setRacun(r1);
						em.persist(s1);
						s2 = new Stavka(0, getStavkaRacun(r2.getIdRac(), em).size() + 1, new Date(), msg.getDoubleProperty("iznos"), 'D');
						s2.setRacun(r2);
						em.persist(s2);

						r1.setStanje(r1.getStanje() - msg.getDoubleProperty("iznos"));
						if (r1.getStanje() < -r1.getDozvMinus()) r1.setStatus('B');
						em.persist(r1);

						r2.setStanje(r2.getStanje() + msg.getDoubleProperty("iznos"));
						if (r2.getStatus() == 'B' && r2.getStanje() >= -r2.getDozvMinus()) r2.setStatus('A');
						em.persist(r2);

						em.getTransaction().commit();

						om = ct.createObjectMessage("ID-jevi transakcija su " + s1.getIdSta() + " (Posaljical) / " + s2.getIdSta() + " (Primalac).");

						break;
					case ("CreateStavkaUplata"):
						r1 = em.find(Racun.class, msg.getIntProperty("idRac1"));
						if (r1 == null) throw new StatusException("Racun ne postoji!", 401);

						if (r1.getStatus() == 'Z') throw new StatusException("Racun je zatvoren!", 402);

						if (!checkFil(msg.getIntProperty("random"))) throw new StatusException("Filijala ne postoji!", 403);

						em.getTransaction().begin();
						(s1 = new Stavka(0, getStavkaRacun(r1.getIdRac(), em).size() + 1, new Date(), msg.getDoubleProperty("iznos"), 'U')).setFilijala(msg.getIntProperty("random"));
						s1.setRacun(r1);
						em.persist(s1);

						r1.setStanje(r1.getStanje() + msg.getDoubleProperty("iznos"));
						if (r1.getStatus() == 'B' && r1.getStanje() >= -r1.getDozvMinus()) r1.setStatus('A');
						em.persist(r1);

						em.getTransaction().commit();

						om = ct.createObjectMessage("ID transakcije je " + s1.getIdSta() + ".");

						break;
					case ("CreateStavkaIsplata"):
						r1 = em.find(Racun.class, msg.getIntProperty("idRac1"));
						if (r1 == null) throw new StatusException("Racun ne postoji!", 401);

						if (r1.getStatus() == 'Z') throw new StatusException("Racun je zatvoren!", 402);

						if (r1.getStatus() == 'B') throw new StatusException("Racun je blokiran!", 403);

						if (!checkFil(msg.getIntProperty("random"))) throw new StatusException("Filijala ne postoji!", 404);

						em.getTransaction().begin();
						(s1 = new Stavka(0, getStavkaRacun(r1.getIdRac(), em).size() + 1, new Date(), msg.getDoubleProperty("iznos"), 'I')).setFilijala(msg.getIntProperty("random"));
						s1.setRacun(r1);
						em.persist(s1);

						r1.setStanje(r1.getStanje() - msg.getDoubleProperty("iznos"));
						if (r1.getStanje() < -r1.getDozvMinus()) r1.setStatus('B');
						em.persist(r1);

						em.getTransaction().commit();

						om = ct.createObjectMessage("ID transakcije je " + s1.getIdSta() + ".");

						break;
					case ("GetRacunKomitent"):
						om = ct.createObjectMessage((Serializable) em.createNamedQuery("Racun.findByKomitent", Racun.class).setParameter("komitent", msg.getIntProperty("idK")).getResultList());
						break;
					case ("GetStavkaRacun"):
						om = ct.createObjectMessage((Serializable) getStavkaRacun(msg.getIntProperty("idRac"), em));
						break;
					case ("CreateKomitent"):
						set = msg.propertyExists("sediste");

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

						em.getTransaction().begin();
						k.setSediste(msg.getIntProperty("sediste"));
						em.getTransaction().commit();

						om = ct.createObjectMessage("Promenjeno sediste komitenta.");

						break;
					case ("GetAll"):
						ArrayList<List> all = new ArrayList<>();

						all.add(em.createNamedQuery("Komitent.findAll", Komitent.class).getResultList());
						all.add(em.createNamedQuery("Racun.findAll", Racun.class).getResultList());
						all.add(em.createNamedQuery("Stavka.findAll", Stavka.class).getResultList());

						om = ct.createObjectMessage(all);

						break;
					default:
						throw new StatusException("Unknown command!", 400);
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
	public static boolean checkFil(int idFil) throws JMSException {
		TextMessage tm = ct.createTextMessage("GetFilijalaId");
		tm.setIntProperty("idFil", idFil);
		tm.setIntProperty("for", 1);
		tm.setIntProperty("to", 22);
		
		JMSConsumer c2 = ct.createConsumer(q, "for=" + 22);
		while (c2.receiveNoWait() != null);

		System.out.print("Asking PodSistem11: ");
		
		// Ako posaljem samo jednom, on ce imati lag od jedne poruke i videce rezultat prethodnog izvrsavanja. Tako da se osiguram saljem 3 puta.
		// Zasto je ovo ovako mogu samo sa bogom da se konsultujem.
		p.send(q, tm);
		ObjectMessage om = (ObjectMessage) c2.receive(2500);
		if (om == null) {
			System.out.println("No response!");
			return false;
		}
		
		p.send(q, tm);
		om = (ObjectMessage) c2.receive(2500);
		if (om == null) {
			System.out.println("No response!");
			return false;
		}
		
		p.send(q, tm);
		om = (ObjectMessage) c2.receive(2500);
		if (om == null) {
			System.out.println("No response!");
			return false;
		}
		
		System.out.println((Integer) om.getObject());
		c2.close();

		return ((Integer) om.getObject()) == 1;
	}
	
	private static List<Stavka> getStavkaRacun(int idRac, EntityManager em) {
		List<Stavka> ret = em.createNamedQuery("Stavka.findAll", Stavka.class).getResultList();
		
		for (int i = 0; i < ret.size(); i++) {
			if (ret.get(i).getRacun().getIdRac() != idRac) ret.remove(i--);
		}
		
		return ret;
	}

}
