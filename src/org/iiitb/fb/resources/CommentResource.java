package org.iiitb.fb.resources;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.iiitb.fb.modals.Comment;
import org.iiitb.fb.services.CommentService;

@Path("/")
public class CommentResource {

	CommentService service = new CommentService();

	@POST
	@Path("/comments/{user_id}/{post_id}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(value = { MediaType.APPLICATION_JSON })
	public Response addComment(@PathParam("user_id") String user_id, @PathParam("post_id") String post_id,
			Comment comm) {

		System.out.println(user_id + "   " + post_id);

		Comment comment = service.addComment(Integer.parseInt(user_id), Integer.parseInt(post_id),
				comm.getCommentMessage());
		if (comment == null) {
			return Response.notModified().build();
		} else {
			return Response.ok().entity(comment).build();
		}

	}

	@GET
	@Path("/comments/{post_id}")
	@Produces(value = { MediaType.APPLICATION_JSON })
	public Response getAllComment(@PathParam("post_id") String post_id) {

		List<Comment> list = service.getAllComment(Integer.parseInt(post_id));

		if (list.isEmpty()) {
			return Response.noContent().build();
		} else {
			return Response.ok().entity(new GenericEntity<List<Comment>>(list) {
			}).build();
		}

	}
}
