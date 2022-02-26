package server.resources;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.inject.Inject;
import javax.jms.*;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

/**
 *
 * @author 
 */
@Path("javaee8")
public class JavaEE8Resource {
	
	@Inject
	private JMSContext con;
	
	@Resource(lookup="myQ")
	private Queue q;
    
    @GET
    public Response ping(){
		
		try {
			JMSProducer p = con.createProducer();
			
			TextMessage tm = con.createTextMessage("CreateMesto");
			tm.setStringProperty("naziv", "Trelles");
			tm.setStringProperty("postbr", "222-SUS");
			
			p.send(q, tm);
			

		} catch (JMSException ex) {
			Logger.getLogger(JavaEE8Resource.class.getName()).log(Level.SEVERE, null, ex);
		}
		
		return Response.ok("ping").build();
    }
}
