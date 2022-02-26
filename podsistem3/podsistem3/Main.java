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
		String toAdd;
		Message om = ct.createObjectMessage();
		TextMessage msg = ct.createTextMessage();

		while (true) {
			try {
				System.out.print("\n==================================\n[" + count++ + "] Waiting for message...\n");
				msg = (TextMessage) c.receive(120000);

				if (msg == null) {
					timer(em);
					continue;
				}

				System.out.println("Message recieved: " + msg.getText() + "[" + msg.getIntProperty("to") + "]");

				switch (msg.getText()) {
					case ("GetAll"):
						toAdd = new String();

						List<Mesto> m = em.createNamedQuery("Mesto.findAll", Mesto.class).getResultList();
						toAdd += "\n=============================================\nKolone su: idMes Naziv Adresa\n\n";

						for (int i = 0; i < m.size(); i++) {
							toAdd += m.get(i).getIdMes().toString() + " ";
							toAdd += m.get(i).getNaziv() + " ";
							toAdd += m.get(i).getPostBr();
							toAdd += "\n";
						}

						List<Filijala> f = em.createNamedQuery("Filijala.findAll", Filijala.class).getResultList();

						toAdd += "\n=============================================\nKolone su: idF Naziv Mesto Adresa\n\n";

						for (Filijala ft : f) {
							toAdd += ft.getIdFil() + " ";
							toAdd += ft.getNaziv() + " ";
							toAdd += ft.getMesto().getNaziv() + " ";
							toAdd += ft.getAdresa();
							toAdd += "\n";
						}

						List<Komitent> k = em.createNamedQuery("Komitent.findAll", Komitent.class).getResultList();

						toAdd += "\n=============================================\nKolone su: idK Naziv Adresa Sediste\n\n";

						for (Komitent kt : k) {
							toAdd += kt.getIdK() + " ";
							toAdd += kt.getNaziv() + " ";
							toAdd += kt.getAdresa() + " ";
							if (kt.getSediste() != null) toAdd += kt.getSediste();
							toAdd += "\n";
						}

						List<Racun> r = em.createNamedQuery("Racun.findAll", Racun.class).getResultList();

						toAdd += "\n=============================================\nKolone su: idRac Status Stanje DozvoljeniMinus BrojStavki Komitent Filijala DatumOtvaranja\n\n";

						for (Racun rt : r) {
							toAdd += rt.getIdRac() + " ";
							toAdd += rt.getStatus() + " ";
							toAdd += rt.getStanje() + " ";
							toAdd += rt.getDozvMinus() + " ";
							toAdd += rt.getBrojStavki() + " ";
							toAdd += rt.getKomitent() + " ";
							toAdd += rt.getFilijala() + " ";
							toAdd += rt.getDatum().toString();
							toAdd += "\n";
						}

						List<Stavka> s = em.createNamedQuery("Stavka.findAll", Stavka.class).getResultList();

						toAdd += "\n=============================================\nKolone su: idK Naziv Adresa Sediste\n\n";

						for (Stavka st : s) {
							toAdd += st.getIdSta() + " ";
							toAdd += st.getRacun().getIdRac() + " ";
							toAdd += st.getDatum().toString() + " ";
							toAdd += st.getIznos() + " ";
							toAdd += st.getRedBroj() + " ";
							toAdd += st.getTip() + " ";
							if (st.getFilijala() != null) toAdd += st.getFilijala();
							toAdd += "\n";
						}

						om = ct.createObjectMessage(toAdd);
						break;
					case ("GetDiff"):
						toAdd = new String();
						JMSConsumer cK = ct.createConsumer(q, "for=" + 333);
						while (cK.receiveNoWait() != null);

						TextMessage tm = ct.createTextMessage("GetAll");
						tm.setIntProperty("to", 333);
						tm.setIntProperty("for", 1);

						while (cK.receiveNoWait() != null);
						p.send(q, tm);
						ArrayList<List> all = (ArrayList) ((ObjectMessage) cK.receive()).getObject();

						toAdd += "\n=========================================\nMesta\n";

						for (int i = 0; i < all.get(0).size(); i++) {
							Mesto MM = (Mesto) all.get(0).get(i);
							Mesto ogM = em.find(Mesto.class, MM.getIdMes());

							if (ogM == null) toAdd += "Missing " + MM.getIdMes() + "\n\n";
						}

						toAdd += "\n=========================================\nFilijale\n";

						for (int i = 0; i < all.get(1).size(); i++) {
							Filijala FF = (Filijala) all.get(1).get(i);
							Filijala ogF = em.find(Filijala.class, FF.getIdFil());

							if (ogF == null) toAdd += "Missing " + FF.getIdFil() + "\n\n";
						}

						toAdd += "\n=========================================\nKomitenti\n";

						for (int i = 0; i < all.get(2).size(); i++) {
							Komitent KK = (Komitent) all.get(2).get(i);
							Komitent ogK = em.find(Komitent.class, KK.getIdK());

							if (ogK == null) toAdd += "Missing " + KK.getIdK() + "\n\n";
							else if (KK.getSediste() != null && (ogK.getSediste() == null || !ogK.getSediste().equals(KK.getSediste()))) {
								toAdd += "Data: " + KK.getIdK() + " " + KK.getSediste() + "\n";
								toAdd += "Base: " + ogK.getIdK() + " ";
								if (ogK.getSediste() != null) toAdd += ogK.getSediste();
								toAdd += "\n\n";
							};
						}

						tm.setIntProperty("for", 2);
						p.send(q, tm);

						all = (ArrayList) ((ObjectMessage) cK.receive()).getObject();

						toAdd += "\n=========================================\nRacuni\n";

						for (int i = 0; i < all.get(1).size(); i++) {
							Racun RR = (Racun) all.get(1).get(i);
							Racun ogR = em.find(Racun.class, RR.getIdRac());

							if (ogR == null) toAdd += "Missing " + RR.getIdRac() + "\n\n";
							else if (RR.getBrojStavki() != ogR.getBrojStavki() || RR.getStanje() != ogR.getStanje() || !RR.getStatus().equals(ogR.getStatus())) {
								toAdd += "Data: " + RR.getIdRac() + " " + RR.getBrojStavki() + " " + RR.getStanje() + " " + RR.getStatus() + "\n";
								toAdd += "Base: " + ogR.getIdRac() + " " + ogR.getBrojStavki() + " " + ogR.getStanje() + " " + ogR.getStatus() + "\n\n";
							}
						}

						toAdd += "\n=========================================\nStavke\n";

						for (int i = 0; i < all.get(2).size(); i++) {
							Stavka SS = (Stavka) all.get(2).get(i);
							Stavka ogS = em.find(Stavka.class, SS.getIdSta());

							if (ogS == null) toAdd += "Missing " + SS.getIdSta() + "\n\n";
						}
						
						cK.close();
						
						om = ct.createObjectMessage(toAdd);
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
