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
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

@Path("p3")
public class P3 {

	@Inject
	private JMSContext ct;

	@Resource(lookup = "myQ")
	private Queue q;

	private JMSProducer p;

	private JMSConsumer c;

	@GET
	@Path("all")
	@Produces({MediaType.APPLICATION_JSON})
	public Response getAllFilijala() throws JMSException {
		return execute(txtMsg("GetFilijalaAll", 1));
	}

	@GET
	@Path("difference")
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
		if (p == null) {
			p = ct.createProducer();
			c = ct.createConsumer(q, "for=" + 0);
		}

		p.send(q, tm);

		ObjectMessage om = (ObjectMessage) c.receive();

		ResponseBuilder r = Response.status(om.getIntProperty("status"));

		if (om.getIntProperty("status") == 200) r.entity(om.getObject());

		return r.build();
	}

}
