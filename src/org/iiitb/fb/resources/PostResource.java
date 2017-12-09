package org.iiitb.fb.resources;

import java.io.InputStream;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.iiitb.fb.modals.Post;
import org.iiitb.fb.services.PostService;

@Path("/")
public class PostResource {
	
	private PostService postService = new PostService();

	@POST
	@Path("users/{user_id}/posts")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(value = { MediaType.APPLICATION_JSON })
	public Response createPost(@PathParam("user_id") String user_id, @FormDataParam("file") InputStream fileInputStream,
			@FormDataParam("file") FormDataContentDisposition fileFormDataContentDisposition,
			@FormDataParam("postdata") String postdata) {

		System.out.println("post input :" + user_id + "  " + fileInputStream + "  " + postdata + "  "
				+ fileFormDataContentDisposition);

		
		String fileName = null;
		if (fileFormDataContentDisposition.getFileName().length() >= 1) {
			System.out.println("inside !ss");
			fileName = fileFormDataContentDisposition.getFileName();
		}
		Post post = postService.createPost(fileInputStream, fileName, postdata, Integer.parseInt(user_id));

		if (post == null) {
			return Response.status(501).build();
		} else {
			return Response.ok().entity(post).build();
		}
	}

	@GET
	@Path("users/{user_id}/posts")
	@Produces(value = { MediaType.APPLICATION_JSON })
	public Response getAllUserPost(@PathParam("user_id") String user_id,
			@QueryParam("friends") String friends) {

		List<Post> listOfPost = null;

		if (friends!=null) {
			listOfPost = postService.getAllUserPost(Integer.parseInt(user_id));
		} else {
			listOfPost = postService.getAllPost(Integer.parseInt(user_id));
		}

		if (listOfPost.isEmpty()) {
			return Response.noContent().build();
		} else {
			return Response.ok()
					.entity(new GenericEntity<List<Post>>(listOfPost) {
					}).build();
		}

	}

	/*@Path("users/{user_id}/posts/{post_id}/comments")
	public CommentResource getAllComments() {

		return new CommentResource();
	}
	
	@Path("users/{user_id}/posts/{post_id}/Likes")
	public LikeResource getAllLikes() {

		return new LikeResource();
	}
	*/
}
