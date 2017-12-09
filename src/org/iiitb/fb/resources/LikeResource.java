package org.iiitb.fb.resources;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.iiitb.fb.modals.Profile;
import org.iiitb.fb.services.LikeService;

@Path("/users/{user_id}/posts/{post_id}/")
public class LikeResource {

	LikeService service = new LikeService();

	@POST
	@Path("/likes")
	@Produces(value = { MediaType.APPLICATION_JSON })
	public Response addLike(@PathParam("user_id") String user_id, @PathParam("post_id") String post_id) {

		Profile profile = service.addLike(Integer.parseInt(user_id), Integer.parseInt(post_id));
		if (profile == null) {
			return Response.notModified().build();
		} else {
			return Response.ok().entity(profile).build();
		}

	}

	@PUT
	@Path("/likes")
	@Produces(value = { MediaType.APPLICATION_JSON })
	public Response updateLikeStatus(@PathParam("user_id") String user_id, @PathParam("post_id") String post_id) {

		int status = service.updateLikeStatus(Integer.parseInt(user_id), Integer.parseInt(post_id));
		if (status == 0) {
			return Response.notModified().build();
		} else {
			return Response.ok().build();
		}

	}
	
@GET
@Path("/getLikes")
@Produces(value = { MediaType.APPLICATION_JSON })
public Response getPostLikes(@PathParam("post_id") String post_id) {

	List<Profile>profiles = service.getPostLikes(Integer.parseInt(post_id));
	
		return Response.ok().entity(new GenericEntity<List<Profile>>(profiles) {
		}).build();
	




}
}
