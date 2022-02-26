package server.resources;

import java.util.HashMap;
import javax.annotation.Resource;
import javax.inject.Inject;
import javax.jms.JMSConsumer;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.JMSProducer;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.TextMessage;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

@Path("p1")
public class P1 {

	@Inject
	private JMSContext ct;

	@Resource(lookup = "myQ")
	private Queue q;

	private JMSProducer p;

	private JMSConsumer c;

	@POST
	@Path("mesto")
	@Produces({MediaType.APPLICATION_JSON})
	public Response createMesto(@QueryParam("naziv") String naziv, @QueryParam("postBr") String postBr) throws JMSException {
		TextMessage tm = txtMsg("CreateMesto", 1);
		tm.setStringProperty("naziv", naziv);
		tm.setStringProperty("postBr", postBr);

		return execute(tm);
	}

	@POST
	@Path("filijala")
	public Response createFilijala(@QueryParam("idMes") int idMes, @QueryParam("naziv") String naziv, @QueryParam("adresa") String adresa) throws JMSException {
		TextMessage tm = txtMsg("CreateFilijala", 1);
		tm.setIntProperty("idMes", idMes);
		tm.setStringProperty("naziv", naziv);
		tm.setStringProperty("adresa", adresa);

		return execute(tm);
	}

	@POST
	@Path("komitent")
	public Response createKomitent(@QueryParam("naziv") String naziv, @QueryParam("adresa") String adresa, @QueryParam("sediste") Integer sediste) throws JMSException {
		TextMessage tm = txtMsg("CreateKomitent", 1);
		tm.setStringProperty("naziv", naziv);
		tm.setStringProperty("adresa", adresa);
		if (sediste != null) tm.setIntProperty("sediste", sediste);

		return execute(tm);
	}

	@PUT
	@Path("komitent")
	public Response changeKomitent(@QueryParam("idKom") int idK, @QueryParam("idMes") int idMes) throws JMSException {
		TextMessage tm = txtMsg("ChangeKomitentSediste", 1);
		tm.setIntProperty("idK", idK);
		tm.setIntProperty("sediste", idMes);

		return execute(tm);
	}

	@GET
	@Path("mesto")
	@Produces({MediaType.APPLICATION_JSON})
	public Response getAllMesta() throws JMSException {
		return execute(txtMsg("GetMestoAll", 1));
	}

	@GET
	@Path("filijala")
	@Produces({MediaType.APPLICATION_JSON})
	public Response getAllFilijala() throws JMSException {
		return execute(txtMsg("GetFilijalaAll", 1));
	}

	@GET
	@Path("komitent")
	@Produces({MediaType.APPLICATION_JSON})
	public Response getAllKomitent() throws JMSException {
		return execute(txtMsg("GetKomitentAll", 1));
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
