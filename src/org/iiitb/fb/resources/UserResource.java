package org.iiitb.fb.resources;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
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
import org.iiitb.fb.modals.Login;
import org.iiitb.fb.modals.Profile;
import org.iiitb.fb.services.UserImageService;
import org.iiitb.fb.services.UserService;

@Path("/")
public class UserResource {

	static String searchVal = null;

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/users/login")
	public Response login(@FormParam("email") String emailID,@FormParam("password") String password) {
		UserService userService = new UserService();
		Profile profile = userService.login(emailID, password);
		if (profile == null)
			return Response.noContent().build();
		return Response.ok().entity(profile).build();
	}

	@GET
	@Produces(value = { MediaType.APPLICATION_JSON })
	@Path("/users/getAllUsers")
	public Response getAllUsers()
	{
	UserService userService=new UserService();
	ArrayList<Login> list = userService.getAllUsers();
	if(list.isEmpty())
	{
	return Response.noContent().build();
	}
	else
	{
	return Response.ok().entity(new GenericEntity<List<Login>>(list) {
	}).build();
	}
	}
	
	@GET
	@Path("/users/validate")
	@Produces(value = { MediaType.TEXT_PLAIN })
	public Response validateEmail(@QueryParam("email") String emailID) {
		UserService userservice = new UserService();
		return Response.ok(Integer.valueOf(userservice.validateEmailId(emailID))).build();
	}

	@POST
	@Path("/register")
	@Produces(value = { MediaType.APPLICATION_JSON })
	@Consumes(value = { MediaType.APPLICATION_FORM_URLENCODED })
	public Response userRegistration(@FormParam("email") String emailID,
			@FormParam("password") String password,
			@FormParam("firstName") String firstName,
			@FormParam("lastName") String lastName,
			@FormParam("dob") String date_of_birth,
			@FormParam("gender") String gender,
			@FormParam("contact") String contact_info,
			@FormParam("hometown") String hometown) {

		UserService userservice = new UserService();
		Profile profile = userservice.setUserInfo(emailID, password, firstName,
				lastName, date_of_birth, gender, contact_info, hometown);

		if (profile == null) {
			return Response.status(409).build(); // resource conflict
		} else {

			return Response.ok().entity(profile).build();
		}

	}

	@POST
	@Path("/users/getUsers")
	@Produces(value = { MediaType.APPLICATION_JSON })
	@Consumes(value = { MediaType.APPLICATION_FORM_URLENCODED })
	public Response getUser(@FormParam("email") String emailID,
			@FormParam("password") String password) {

		UserService userservice = new UserService();
		Profile profile = userservice.login(emailID, password);
		if (profile == null) {
			return Response.status(409).build(); // resource conflict
		} else {

			return Response.ok().entity(profile).build();
		}
	}

	@GET
	@Path("/users/search")
	@Produces(value = { MediaType.APPLICATION_JSON })
	public Response search(@QueryParam("searchData") String searchData,
			@QueryParam("from") String from, @QueryParam("userId") String userId) {
		String searchVal1 = new String();
		searchVal1 = searchData;
		searchVal = searchVal1;
		System.out.println(searchVal + " " + from + " " + userId);

		if (!from.equals("search"))
			return Response.ok().build();
		UserService userservice = new UserService();
		List<Profile> listOfprofile = userservice.search(searchData, userId);

		if (listOfprofile.isEmpty()) {
			System.out.println("abc");
			return Response.noContent().build();
		} else {

			return Response.ok()
					.entity(new GenericEntity<List<Profile>>(listOfprofile) {
					}).build();
		}
	}

	@GET
	@Path("/users/onLoadSearchVal")
	@Produces(MediaType.TEXT_PLAIN)
	public String onLoadSearchValue() {
		System.out.println(searchVal);
		return searchVal;
	}

	@GET
	@Path("/users/getImages/{id}")
	public Response getImages(@PathParam("id") String userId) {
		UserImageService userImageService = new UserImageService();
		String images = userImageService.getImages(userId);
		if (images == null)
			return Response.noContent().build();
		return Response.ok().entity(images).build();
	}

	@POST
	@Path("/users/uploadProfilePic")
	@Produces(MediaType.TEXT_PLAIN)
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public Response updateProfilePic(
			@FormDataParam("file") InputStream fileInputStream,
			@FormDataParam("file") FormDataContentDisposition fileFormDataContentDisposition,
			@FormDataParam("userId") String userId) {

		// local variables
		String fileName = null;
		String uploadFilePath = null;

		fileName = fileFormDataContentDisposition.getFileName();
		uploadFilePath = new UserImageService().uploadProfilePic(
				fileInputStream, fileName, userId);
		if (uploadFilePath == null)
			return Response.notModified().build();

		return Response.ok().entity(uploadFilePath).build();

	}

	@POST
	@Path("/users/uploadImage")
	@Produces(MediaType.TEXT_PLAIN)
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public Response uploadImageFile(
			@FormDataParam("file") InputStream fileInputStream,
			@FormDataParam("file") FormDataContentDisposition fileFormDataContentDisposition,
			@FormDataParam("userId") String profileId) {

		// local variables
		String fileName = null;
		String uploadFilePath = null;

		fileName = fileFormDataContentDisposition.getFileName();

		uploadFilePath = new UserImageService().addPhoto(fileInputStream,
				fileName, profileId);

		if (uploadFilePath == null)
			return Response.notModified().build();

		return Response.ok().entity(uploadFilePath).build();

	}

	@POST
	@Path("/users/updateDetails/{variableName}/{id}")
	@Produces(value = { MediaType.APPLICATION_JSON })
	@Consumes(value = { MediaType.APPLICATION_FORM_URLENCODED })
	public Response updateDetails(@PathParam("variableName") String variableName,@PathParam("id") int id,
			@FormParam("changedValue") String changedValue) {
		Profile profile = new UserService().updateDetails(variableName, id,changedValue);
		if (profile == null) {
			System.out.println("profile did not update");
			return Response.notModified().build();
		} 
		else
			return Response.ok().entity(profile).build();
	}

	@GET
	@Path("/users/{user_id}")
	@Produces(value = { MediaType.APPLICATION_JSON })
	public Response getUsers(@PathParam("user_id") String user_id) {
		Profile profile = new UserService().getUserProfile(Integer
				.parseInt(user_id));

		return Response.ok().entity(profile).build();
	}

}