package org.iiitb.fb.resources;

import java.io.InputStream;
import java.sql.Date;
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
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.iiitb.fb.modals.Event;
import org.iiitb.fb.modals.Post;
import org.iiitb.fb.modals.Profile;
import org.iiitb.fb.services.EventService;

@Path("/")
public class EventResource {

	@POST
	@Path("users/{user_id}/events")
	@Produces(value = { MediaType.APPLICATION_JSON })
	@Consumes(value = { MediaType.APPLICATION_FORM_URLENCODED })
	public Response eventCreation(@FormParam("event_name") String eventname,
			@FormParam("location") String loc,
			@FormParam("start_date") Date start,
			@FormParam("end_date") Date end,
			@FormParam("description") String desc,
			@PathParam("user_id") int user_id) {
		EventService eventservice = new EventService();
		
		if(start.after(end))
			return Response.noContent().build();
			
		Event event = eventservice.createEvent(eventname, user_id, start, end,
				loc, desc);

		if (event == null) {
			System.out.println("creation failed!!");
			return Response.status(409).build(); // resource conflict
		} else {
			System.out.println("event status : " + event.getStatus());
			return Response.ok().entity(event).build();
		}
	}

	@GET
	@Path("users/{user_id}/events")
	@Produces(value = { MediaType.APPLICATION_JSON })
	public Response getEventList(@PathParam("user_id") int user_id) {
		List<Event> eventList = new EventService().getComingEventList(user_id);
		if (eventList.isEmpty())
			return Response.noContent().build();
		else
			return Response.ok()
					.entity(new GenericEntity<List<Event>>(eventList) {
					}).build();
	}
	
	@POST
	@Path("users/{id}/accept")
	@Produces(value = { MediaType.APPLICATION_JSON })
	//@Consumes(value = { MediaType.APPLICATION_JSON })
	public Response getfriendacceptedEventsList(@PathParam("id") int u_id,@FormParam("frnd_id") int friend_id) {
		System.out.println("i m in resource******************************************");
		List<Event> discover_list = new EventService()
				.getEventList1(u_id,friend_id);
		
		if (discover_list == null)
			return Response.noContent().build();
		else {
			System.out.println("in resource : " + discover_list);
			return Response.ok()
					.entity(new GenericEntity<List<Event>>(discover_list) {
					}).build();
		}
	}
	
	
	@GET
	@Path("events/{event_id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Event getEvent(@PathParam("event_id") int event_id) {
		Event event = new EventService().getEvent(event_id);
		System.out.println(event);
		return event;
	}

	@GET
	@Path("events/{event_id}/posts")
	@Produces(value = { MediaType.APPLICATION_JSON })
	public Response getAllGroupPost(@PathParam("event_id") String event_id) {
		List<Post> listOfPost = new EventService().getAllEventPost(Integer
				.parseInt(event_id));
		if (listOfPost.isEmpty())
			return Response.noContent().build();
		else
			return Response.ok()
					.entity(new GenericEntity<List<Post>>(listOfPost) {
					}).build();
	}

	@POST
	@Path("users/{user_id}/events/{event_id}/posts")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(value = { MediaType.APPLICATION_JSON })
	public Response createEventPost(
			@PathParam("event_id") String event_id,
			@PathParam("user_id") String user_id,
			@FormDataParam("file") InputStream fileInputStream,
			@FormDataParam("file") FormDataContentDisposition fileFormDataContentDisposition,
			@FormDataParam("postdata") String postdata) {

		String fileName = null;
		if (fileFormDataContentDisposition.getFileName().length() >= 1) {
			System.out.println("inside !ss");
			fileName = fileFormDataContentDisposition.getFileName();
		}
		Post post = new EventService().createPostInEvent(fileInputStream,
				fileName, postdata, Integer.parseInt(user_id),
				Integer.parseInt(event_id));

		if (post == null) {
			return Response.status(501).build();
		} else {
			return Response.ok().entity(post).build();
		}
	}

	@PUT
	@Path("events/{event_id}/changeStatus/{status}/{user_id}")
	@Produces(value = { MediaType.APPLICATION_JSON })
	@Consumes(value = { MediaType.APPLICATION_JSON })
	public Response changeInviteStatus(@PathParam("user_id") Integer user_id,
			@PathParam("event_id") Integer event_id,
			@PathParam("status") String status) {
		int res;
		if (status.equals("ignore"))
			res = new EventService().deleteEvent(event_id, user_id);
		else
			res = new EventService().changeStatus(event_id, user_id, status);

		if (res == 0)
			return Response.notModified().build();
		else
			return Response.ok().build();
	}

	@POST
	@Path("events/{event_id}/changeStatus/{status}/{user_id}")
	@Produces(value = { MediaType.APPLICATION_JSON })
	@Consumes(value = { MediaType.APPLICATION_JSON })
	public Response changeDiscoveryStatus(
			@PathParam("user_id") Integer user_id,
			@PathParam("event_id") Integer event_id,
			@PathParam("status") String status) {
		int res;
		if (status.equals("ignore"))
			res = new EventService().deleteEvent(event_id, user_id);
		else
			res = new EventService().changeDiscoveryStatus(event_id, user_id,
					status);

		if (res == 0)
			return Response.notModified().build();
		else
			return Response.ok().build();
	}

	@GET
	@Path("events/getCount/{event_id}")
	@Produces(MediaType.TEXT_PLAIN)
	public Response countGoingAndInterested(@PathParam("event_id") int event_id) {
		System.out.println("inside count resource");
		int count[] = new EventService().getCount(event_id);
		String s = new String();
		// if(count.length>0)
		s = "" + count[0] + "_" + count[1];
		// System.out.println(count1.get(0)+" "+count1.get(1)+"...............");
		if (s.isEmpty())
			return Response.ok().entity(s).build();
		else {
			System.out.println("getting count in resource : " + count[0]
					+ " , " + count[1]);
			return Response.ok().entity(s).build();
		}
	}

	@GET
	@Path("users/{user_id}/{event_id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getFriendsToInvite(@PathParam("user_id") String user_id,
			@PathParam("event_id") String event_id) {
		EventService eventService = new EventService();
		ArrayList<Profile> inviteList = eventService.getFriendsToInvite(
				user_id, event_id);
		if (inviteList.isEmpty())
			return Response.noContent().build();
		return Response.ok()
				.entity(new GenericEntity<List<Profile>>(inviteList) {
				}).build();
	}

	@PUT
	@Path("users/invite/{user_id}/{event_id}")
	// @Consumes
	public Response sendFriendInvitation(@PathParam("user_id") String user_id,
			@PathParam("event_id") String event_id) {
		EventService eventService = new EventService();
		System.out.println("resource event");
		int rs = eventService.sendFriendInvitation(user_id, event_id);

		if (rs >= 1) {
			return Response.ok().build();
		} else {
			return Response.notModified().build();

		}
	}

	@GET
	@Path("users/{id}/discover_events")
	@Produces(value = { MediaType.APPLICATION_JSON })
	public Response getDiscoverEvent(@PathParam("id") int user_id) {
		List<Event> discover_list = new EventService()
				.getDiscoverEvent(user_id);
		if (discover_list == null)
			return Response.noContent().build();
		else {
			System.out.println("in resource : " + discover_list);
			return Response.ok()
					.entity(new GenericEntity<List<Event>>(discover_list) {
					}).build();
		}
	}

	@GET
	@Path("users/{user_id}/pastevents")
	@Produces(value = { MediaType.APPLICATION_JSON })
	public Response getPastEventList(@PathParam("user_id") int user_id) {
		List<Event> eventList = new EventService().getPastEventList(user_id);
		if (eventList.isEmpty())
			return Response.noContent().build();
		else
			return Response.ok()
					.entity(new GenericEntity<List<Event>>(eventList) {
					}).build();
	}
	
	@POST
	@Path("users/{event_id}/addSummary")
	@Produces(MediaType.TEXT_PLAIN)
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public String addSummary(@PathParam("event_id") int event_id, @FormParam("summaryArea") String summary) { 
	return  new EventService().addSummary(event_id, summary);
	}

	@GET
	@Path("users/events/Interestedmembers/{event_id}")
	@Produces(value = { MediaType.APPLICATION_JSON })
	public Response Interestedmembers(@PathParam("event_id") int event_id) {
	System.out.println("in interested members resource");
	List<Profile> UserList = new EventService().Interestedmembers(event_id);
	if (UserList.isEmpty())
	return Response.noContent().build();
	else
	return Response.ok()
	.entity(new GenericEntity<List<Profile>>(UserList) {
	}).build();
	}
}