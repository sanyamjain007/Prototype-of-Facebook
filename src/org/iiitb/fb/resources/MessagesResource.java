package org.iiitb.fb.resources;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.tomcat.util.codec.binary.StringUtils;
import org.iiitb.fb.modals.Messages;
import org.iiitb.fb.services.MessagesService;

@Path("/userMessages")
public class MessagesResource {

	@GET
	@Produces(value = { MediaType.APPLICATION_JSON })
	@Path("/getTextMessages/{user_id}/{friend_id}")
	public Response getMessages(@PathParam("user_id") String user_id,
			@PathParam("friend_id") String friend_id) {
		List<Messages> messagesList = new MessagesService().getMessages(
				Integer.parseInt(user_id), Integer.parseInt(friend_id));
		if (messagesList.isEmpty()) {
			return Response.noContent().build();
		}
		return Response.ok()
				.entity(new GenericEntity<List<Messages>>(messagesList) {
				}).build();
	}

	@PUT
	@Path("/sendTextMessages")
	@Consumes(value = { MediaType.APPLICATION_JSON })
	@Produces(value = { MediaType.APPLICATION_JSON })
	public Response sendMessages(Messages message) {
		System.out.println("send message");
		if (message.getMessage().trim().isEmpty())
			return Response.noContent().build();
		Messages mssg = new MessagesService().sendMessage(message);
		if (mssg.getMessage_id() == 0) {
			return Response.noContent().build();
		}
		return Response.ok().entity(mssg).build();
	}
}