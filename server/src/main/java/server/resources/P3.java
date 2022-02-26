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
	public Response getAll() throws JMSException {
		return execute(txtMsg("GetAll", 3));
	}

	@GET
	@Path("difference")
	@Produces({MediaType.APPLICATION_JSON})
	public Response getAllDiff() throws JMSException {
		return execute(txtMsg("GetDiff", 3));
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

		ObjectMessage om = (ObjectMessage) c.receive(15000);

		ResponseBuilder r;
		
		if (om != null) r = Response.status(om.getIntProperty("status")).entity(om.getObject());
		else r = Response.status(400);

		return r.build();
	}

}
