package org.iiitb.fb.resources;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.iiitb.fb.modals.Profile;
import org.iiitb.fb.services.FriendService;
import org.iiitb.fb.services.UserService;

@Path("/")
public class FriendResource {
	@GET
	@Path("users/{id}/friends")
	@Produces(value = { MediaType.APPLICATION_JSON })
	public Response getFriendList(@PathParam("id") String id,
			@QueryParam("filter") String filter) {
		UserService userservice = new UserService();
		System.out.println(id + "user id");
		List<Profile> listOfFriend;
		if (filter != null) {
			listOfFriend = userservice.getFilterFriendList(
					Integer.parseInt(id), filter);
		} else
			listOfFriend = userservice.getFriendList(Integer.parseInt(id));
		if (listOfFriend.isEmpty()) {
			return Response.noContent().build();
		} else {
			return Response.ok()
					.entity(new GenericEntity<List<Profile>>(listOfFriend) {
					}).build();
		}
	}

	@GET
	@Path("users/{id}/mutualfriends")
	@Produces(value = { MediaType.APPLICATION_JSON })
	public Response getmutualFriendList(@PathParam("id") String id) {

		FriendService friendservice = new FriendService();

		System.out.println(id + "user id");
		List<Profile> listOfMutualFriend = friendservice
				.getMutualFriend(Integer.parseInt(id));
		if (listOfMutualFriend.isEmpty()) {
			return Response.noContent().build();
		} else {

			return Response
					.ok()
					.entity(new GenericEntity<List<Profile>>(listOfMutualFriend) {
					}).build();
		}
	}

	@PUT
	@Path("users/{user_id}/addFriend")
	@Produces(value = { MediaType.APPLICATION_JSON })
	@Consumes(value = { MediaType.APPLICATION_JSON })
	public Response addFriendToFriendList(@PathParam("user_id") String user_id,
			Profile friend_id) {
		FriendService friendService = new FriendService();
		int result = friendService.sendFriendRequest(Integer.parseInt(user_id),
				friend_id.getUser_id());
		if (result == 0) {
			return Response.notModified().build();
		} else {
			return Response.ok().build();
		}
	}

	@PUT
	@Path("users/{user_id}/unfriend")
	@Produces(value = { MediaType.APPLICATION_JSON })
	@Consumes(value = { MediaType.APPLICATION_JSON })
	public Response unfriendFromFriendList(
			@PathParam("user_id") String user_id, Profile friend_id) {
		FriendService friendService = new FriendService();
		int result = friendService.unfriendFromFriendList(
				Integer.parseInt(user_id), friend_id.getUser_id());
		if (result == 0) {
			return Response.notModified().build();
		} else {
			return Response.ok().build();
		}
	}

	@PUT
	@Path("users/{user_id}/confirmRequest")
	@Produces(value = { MediaType.APPLICATION_JSON })
	@Consumes(value = { MediaType.APPLICATION_JSON })
	public Response confirmFriendToFriendList(
			@PathParam("user_id") String user_id, Profile friend_id) {
		FriendService friendService = new FriendService();
		int result = friendService.confirmFriendRequest(
				Integer.parseInt(user_id), friend_id.getUser_id());
		if (result == 0) {
			return Response.notModified().build();
		} else {
			return Response.ok().build();
		}
	}

	@PUT
	@Path("users/{user_id}/cancelRequest")
	@Produces(value = { MediaType.APPLICATION_JSON })
	@Consumes(value = { MediaType.APPLICATION_JSON })
	public Response cancelFriendToFriendList(
			@PathParam("user_id") String user_id, Profile friend_id) {
		FriendService friendService = new FriendService();
		int result = friendService.cancelFriendRequest(
				Integer.parseInt(user_id), friend_id.getUser_id());
		if (result == 0) {
			return Response.notModified().build();
		} else {
			return Response.ok().build();
		}
	}

	@GET
	@Path("/users/{userId}/isFriend/{friendId}")
	public Response isFriend(@PathParam("userId") String userId,
			@PathParam("friendId") String friendId) {
		FriendService friendService = new FriendService();
		int value = -1;
		value = friendService.isFriend(userId, friendId);
		if (value == -1)
			return Response.noContent().build();
		return Response.ok().entity(value).build();
	}

	@GET
	@Path("users/{user_id}/friendRequest")
	@Produces(value = { MediaType.APPLICATION_JSON })
	@Consumes(value = { MediaType.APPLICATION_JSON })
	public Response getFriendRequest(@PathParam("user_id") String user_id) {

		FriendService friendService = new FriendService();
		List<Profile> listOfFriendRequest = friendService
				.getFriendRequest(Integer.parseInt(user_id));

		if (listOfFriendRequest.isEmpty()) {
			return Response.noContent().build();
		} else {
			return Response
					.ok()
					.entity(new GenericEntity<List<Profile>>(
							listOfFriendRequest) {
					}).build();
		}
	}

	@GET
	@Path("/users/{user_id}/getFriendsToSuggest/{friend_id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getFriendsToSuggest(@PathParam("user_id") String user_id,
			@PathParam("friend_id") String friend_id) {
		FriendService friendService = new FriendService();
		ArrayList<Profile> arrayList = friendService.getFriendsToSuggest(
				user_id, friend_id);
		if (arrayList.isEmpty())
			return Response.noContent().build();
		return Response.ok()
				.entity(new GenericEntity<List<Profile>>(arrayList) {
				}).build();

	}

	@PUT
	@Path("users/{user_id}/suggest")
	@Consumes(value = { MediaType.APPLICATION_JSON })
	public Response sendFriendSuggestion(@PathParam("user_id") String user_id,
			List<Profile> profile) {
		FriendService friendService = new FriendService();
		int result = friendService.sendFriendSuggestion(Integer
				.parseInt(user_id), profile.get(0).getUser_id(), profile.get(1)
				.getUser_id());
		if (result == 0) {
			return Response.notModified().build();
		} else {
			return Response.ok().build();
		}
	}

	@GET
	@Path("/users/{user_id}/getfriendSuggestions")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getFriendSuggestions(@PathParam("user_id") String user_id) {
		FriendService friendService = new FriendService();
		ArrayList<Profile> arrayList = friendService
				.getFriendSuggestions(user_id);
		if (arrayList.isEmpty())
			return Response.noContent().build();
		return Response.ok()
				.entity(new GenericEntity<List<Profile>>(arrayList) {
				}).build();

	}

	@PUT
	@Path("users/{user_id}/ignore")
	@Consumes(value = { MediaType.APPLICATION_JSON })
	public Response ignoreFriendSuggestion(
			@PathParam("user_id") String user_id, Profile friend_id) {
		FriendService friendService = new FriendService();
		int result = friendService.ignoreFriendSuggestion(
				Integer.parseInt(user_id), friend_id.getUser_id());
		if (result == 0) {
			return Response.notModified().build();
		} else {
			return Response.ok().build();
		}
	}

	@PUT
	@Path("users/{user_id}/addSuggestedFriend")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(value = { MediaType.APPLICATION_JSON })
	public Response addFriendSuggestion(@PathParam("user_id") String user_id,
			Profile friend_id) {
		FriendService friendService = new FriendService();
		int result = friendService.addFriendSuggestion(
				Integer.parseInt(user_id), friend_id.getUser_id());
		if (result == 0) {
			return Response.notModified().build();
		} else {
			return Response.ok().build();
		}
	}

	@GET
	@Path("users/{id}/commonfriends/{friend_id}")
	@Produces(value = { MediaType.APPLICATION_JSON })
	public Response getCommonFriendList(@PathParam("id") String id,
			@PathParam("friend_id") String friend_id) {
		System.out.println("inside resource commo frnd");
		FriendService friendservice = new FriendService();

		System.out.println(id + "user id");
		System.out.println(friend_id + "friend id");
		List<Profile> listOfCommonFriend = friendservice.getCommonFriends(
				Integer.parseInt(id), Integer.parseInt(friend_id));
		if (listOfCommonFriend.isEmpty()) {
			System.out.println("empty");
			return Response.noContent().build();
		} else {
			System.out.println("not empty");
			return Response
					.ok()
					.entity(new GenericEntity<List<Profile>>(listOfCommonFriend) {
					}).build();
		}
	}

	@GET
	@Path("users/{user_id}/friends/{friend_id}")
	@Produces(value = { MediaType.TEXT_PLAIN })
	public Response getMutualFriendListForHover(
			@PathParam("user_id") String user_id,
			@PathParam("friend_id") String friend_id,
			@QueryParam("count") String count) {
		System.out.println("inside resource getMutualFriendListForHover frnd");
		FriendService friendservice = new FriendService();

		if (count != null) {
			int countfriends = friendservice.countGetMutualFriendListForHover(
					Integer.parseInt(user_id), Integer.parseInt(friend_id));
			return Response.ok().entity(countfriends).build();
		} else {
			List<String> list = friendservice.getMutualFriendListForHover(
					Integer.parseInt(user_id), Integer.parseInt(friend_id));
			StringBuilder sb = new StringBuilder("");
			int size = list.size();
			for (int i = 0; i < size - 1; i++) {
				sb.append(list.get(i) + ",");
			}
			sb.append(list.get(size - 1));
			return Response.ok().entity(sb.toString()).build();
		}

	}

	@GET
	@Path("/users/{user_id}/getfriendSuggestionsOnCity/{city}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getFriendSuggestions(@PathParam("user_id") String user_id,
			@PathParam("city") String city) {
		FriendService friendService = new FriendService();
		ArrayList<Profile> arrayList = null;
		if (city.equalsIgnoreCase("All"))
			arrayList = friendService.getFriendSuggestions(user_id);
		else
			arrayList = friendService.getFriendSuggestionsOnCity(user_id, city);
		if (arrayList.isEmpty())
			return Response.noContent().build();
		return Response.ok()
				.entity(new GenericEntity<List<Profile>>(arrayList) {
				}).build();
	}

	@GET
	@Path("/getfriendProfile/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getFriendDetails(@PathParam("id") int id) {
		Profile profile = null;
		profile = new UserService().getUserProfile(id);

		System.out.println(profile.getProfilePicUrl());
		if (profile == null)
			return Response.noContent().build();
		return Response.ok().entity(profile).build();
	}

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/users/{id}/findFriends")
	public Response findFriends(@FormParam("name") String name,
			@FormParam("homeTown") String homeTown,
			@FormParam("currentCity") String currentCity,
			@FormParam("highSchool") String highSchool,
			@FormParam("college") String college,
			@FormParam("employer") String employer,
			@FormParam("graduateSchool") String graduateSchool,
			@PathParam("id") String id) {
		name = name + "";
		homeTown = homeTown + "";
		currentCity = currentCity + "";
		highSchool = highSchool + "";
		employer = employer + "";
		graduateSchool = graduateSchool + "";
		System.out.println(name + " " + homeTown + " " + currentCity + " "
				+ highSchool + " " + college + " " + employer + " "
				+ graduateSchool);
		FriendService fs = new FriendService();
		List<Profile> list = fs.findFriends(name, homeTown, currentCity,
				highSchool, college, employer, graduateSchool, id);
		System.out.println("BACK TO RESOURCE CLASS");
		if (list.isEmpty()) {
			return Response.noContent().build();
		} else {

			return Response.ok().entity(new GenericEntity<List<Profile>>(list) {
			}).build();
		}
	}
}
