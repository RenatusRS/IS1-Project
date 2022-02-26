package server.resources;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.jms.JMSConsumer;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.JMSProducer;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.TextMessage;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

@Path("p2")
public class P2 {

	@Inject
	private JMSContext ct;

	@Resource(lookup = "myQ")
	private Queue q;

	private JMSProducer p;

	private JMSConsumer c;

	@POST
	@Path("racun")
	public Response postRacun(@QueryParam("idK") int idK, @QueryParam("idFil") int idFil, @QueryParam("minus") double minus) throws JMSException {
		TextMessage tm = txtMsg("OpenRacun", 2);
		tm.setIntProperty("idK", idK);
		tm.setIntProperty("idFil", idFil);
		tm.setDoubleProperty("minus", minus);

		return execute(tm);
	}

	@DELETE
	@Path("racun")
	public Response deleteRacun(@QueryParam("idRac") int idRac) throws JMSException {
		TextMessage tm = txtMsg("CloseRacun", 2);
		tm.setIntProperty("idRac", idRac);

		return execute(tm);
	}

	@POST
	@Path("stavka")
	public Response postStavka(@QueryParam("tip") String tip, @QueryParam("iznos") double iznos, @QueryParam("idRac1") int idRac1, @QueryParam("random") int random) throws JMSException {
		TextMessage tm = txtMsg("U".equals(tip) ? "CreateStavkaUplata" : "I".equals(tip) ? "CreateStavkaIsplata" : "CreateStavkaRazmena", 2);
		tm.setDoubleProperty("iznos", iznos);
		tm.setIntProperty("idRac1", idRac1);
		tm.setIntProperty("random", random);

		return execute(tm);
	}

	@GET
	@Path("racun")
	@Produces({MediaType.APPLICATION_JSON})
	public Response getRacun(@QueryParam("idK") int idK) throws JMSException {
		TextMessage tm = txtMsg("GetRacunKomitent", 2);
		tm.setIntProperty("idK", idK);
		
		return execute(tm);
	}

	@GET
	@Path("stavka")
	@Produces({MediaType.APPLICATION_JSON})
	public Response getStavka(@QueryParam("idRac") int idRac) throws JMSException {
		TextMessage tm = txtMsg("GetStavkaRacun", 2);
		tm.setIntProperty("idRac", idRac);
		
		return execute(tm);
	}

	private TextMessage txtMsg(String command, int to) throws JMSException {
		TextMessage tm = ct.createTextMessage(command);
		tm.setIntProperty("for", to);
		tm.setIntProperty("to", 0);

		return tm;
	}

	private Response execute(TextMessage tm) throws JMSException {
		p = ct.createProducer();
		c = ct.createConsumer(q, "for=" + 0);
		while (c.receiveNoWait() != null);

		p.send(q, tm);

		ObjectMessage om = (ObjectMessage) c.receive(5000);

		ResponseBuilder r;
		
		if (om != null) r = Response.status(om.getIntProperty("status")).entity(om.getObject());
		else r = Response.status(400);

		return r.build();
	}

}
