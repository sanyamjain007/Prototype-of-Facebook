package org.iiitb.fb.resources;

import java.io.InputStream;
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

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.iiitb.fb.modals.Group;
import org.iiitb.fb.modals.Post;
import org.iiitb.fb.modals.Profile;
import org.iiitb.fb.services.GroupService;
import org.iiitb.fb.services.PostService;

@Path("/")
public class GroupResource {

	@POST
	@Path("users/{user_id}/groups")
	@Produces(value = { MediaType.APPLICATION_JSON })
	@Consumes(value = { MediaType.APPLICATION_FORM_URLENCODED })
	public Response groupCreation(@FormParam("group_name") String groupname,
			@PathParam("user_id") int user_id) {
		GroupService groupservice = new GroupService();
		Group group = groupservice.createGroup(groupname, user_id);

		if (group == null) {
			return Response.status(409).build(); // resource conflict
		} else {
			System.out.println("group created successfully !!");
			return Response.ok().entity(group).build();
		}
	}

	// status 0 add member 1:update status 2:delete from group
	@PUT
	@Path("users/{user_id}/groups")
	@Produces(value = { MediaType.APPLICATION_JSON })
	@Consumes(value = { MediaType.TEXT_PLAIN })
	public Response updateGroupMemberStatus(@PathParam("user_id") int user_id,
			Group group, @QueryParam("status") String st) {

		GroupService groupservice = new GroupService();
		int status = Integer.parseInt(st);
		int result = 0;
		if (status == 0) {

			Profile profile = groupservice.addMemberByAdmin(
					group.getGroup_id(), user_id);
			return Response.ok().entity(profile).build();
		} else if (status == 1) {
			result = groupservice.updateGroupMemberStatus(group.getGroup_id(),
					user_id, status);

		} else {
			result = groupservice.DeleteGroupMemberStatus(group.getGroup_id(),
					user_id);

		}
		if (result > 0) {
			return Response.ok().build();
		} else {
			return Response.notModified().build();
		}
	}

	@GET
	@Path("users/{user_id}/groups")
	@Produces(value = { MediaType.APPLICATION_JSON })
	public Response getGroupList(@PathParam("user_id") int user_id) {

		GroupService groupservice = new GroupService();

		List<Group> listOfMyGroup = groupservice.getGroupList(user_id);
		if (listOfMyGroup.isEmpty()) {
			return Response.noContent().build();
		} else {

			return Response.ok()
					.entity(new GenericEntity<List<Group>>(listOfMyGroup) {
					}).build();
		}
	}
	
	@GET
	@Path("users/{user_id}/discovergroups")
	@Produces(value = { MediaType.APPLICATION_JSON })
	public Response getDiscoverableGroupList(@PathParam("user_id") int user_id) {

		GroupService groupservice = new GroupService();

		List<Group> listOfMyGroup = groupservice.getDiscoverableGroupList(user_id);
		if (listOfMyGroup.isEmpty()) {
			return Response.noContent().build();
		} else {

			return Response.ok()
					.entity(new GenericEntity<List<Group>>(listOfMyGroup) {
					}).build();
		}
	}

	@GET
	@Path("groups/{group_id}/members")
	@Produces(value = { MediaType.APPLICATION_JSON })
	public Response getGroupMembersList(@PathParam("group_id") int groupid) {

		GroupService groupservice = new GroupService();

		List<Profile> listofgroupmembers = groupservice
				.getMembersOfAGroup(groupid);
		if (listofgroupmembers.isEmpty()) {
			return Response.noContent().build();
		} else {

			return Response
					.ok()
					.entity(new GenericEntity<List<Profile>>(listofgroupmembers) {
					}).build();

		}
	}
	
	@GET
	@Path("users/{user_id}/groups/{group_id}/joinrequests")
	@Produces(value = { MediaType.APPLICATION_JSON })
	public Response getGroupJoinRequestList(@PathParam("user_id") int userid,@PathParam("group_id") int groupid) {

		GroupService groupservice = new GroupService();
		List<Profile> listofgroupjoins=null;
		listofgroupjoins = groupservice
				.getGroupJoinRequests(userid,groupid);
		if (listofgroupjoins==null) {
			return Response.noContent().build();
		} else {

			return Response
					.ok()
					.entity(new GenericEntity<List<Profile>>(listofgroupjoins) {
					}).build();

		}
	}

	//add member by admin
	@PUT
	@Path("/users/{user_id}/groups/{group_id}/members")
	@Produces(value = { MediaType.APPLICATION_JSON })
	@Consumes(value = { MediaType.APPLICATION_JSON })
	public Response AddMember(@PathParam("user_id") int userid,@PathParam("group_id") int groupid,
	Profile profile) {
	GroupService groupservice = new GroupService();
	int result = groupservice.Addmember(userid,groupid, profile.getUser_id());
	
	System.out.println(result);
	
	if (result == 0) {
	return Response.notModified().build(); // resource conflict
	} else {
	System.out.println("member added successfully !!");
	return Response.ok().build();
	}
	}
	
	//remove member by admin
	@PUT
	@Path("users/{user_id}/groups/{group_id}/members/{member_id}/removemember")
	@Produces(value = { MediaType.APPLICATION_JSON })
	@Consumes(value = { MediaType.APPLICATION_JSON })
	public Response removeMember(@PathParam("user_id") Integer user_id,@PathParam("member_id") Integer member_id,
			@PathParam("group_id") Integer group_id) {

		GroupService groupService = new GroupService();
		int result = groupService.RemoveGroupMember(user_id,group_id,member_id);

		if (result == 0) {
			return Response.notModified().build();
		} else {
			return Response.ok().build();
		}
	}

	//send join request to group admin
	@PUT
	@Path("groups/{group_id}/members/{member_id}/joinGroupRequest")
	@Produces(value = { MediaType.APPLICATION_JSON })
	@Consumes(value = { MediaType.APPLICATION_JSON })
	public Response joinGroupRequest(@PathParam("member_id") Integer member_id,
			@PathParam("group_id") Integer group_id) {

		GroupService groupService = new GroupService();
		int result = groupService.joinGroupRequest(group_id,member_id);

		if (result == 0) {
			return Response.notModified().build();
		} else {
			return Response.ok().build();
		}
	}
	
	//group admin's response to join request
	//or
	// join group(response for invite)
		@PUT
		@Path("groups/{group_id}/members/{member_id}/joinGroup")
		@Produces(value = { MediaType.APPLICATION_JSON })
		@Consumes(value = { MediaType.APPLICATION_JSON })
		public Response joinGroup(@PathParam("member_id") Integer member_id,
				@PathParam("group_id") Integer group_id) {

			GroupService groupService = new GroupService();
			int result = groupService.updateGroupMemberStatus( group_id,member_id,1);

			if (result == 0) {
				return Response.notModified().build();
			} else {
				return Response.ok().build();
			}
		}
		// decline invite for group join or decline(by admin) join request 
		@PUT
		@Path("groups/{group_id}/members/{member_id}/declineGroup")
		@Produces(value = { MediaType.APPLICATION_JSON })
		@Consumes(value = { MediaType.APPLICATION_JSON })
		public Response declineGroup(@PathParam("group_id") Integer group_id,@PathParam("member_id") Integer member_id) {

			GroupService groupService = new GroupService();
			int result = groupService.DeleteGroupMemberStatus(group_id,member_id);

			if (result == 0) {
				return Response.notModified().build();
			} else {
				return Response.ok().build();
			}
		}
	
	// leave group
	@PUT
	@Path("groups/{group_id}/members/{member_id}/leaveGroup")
	@Produces(value = { MediaType.APPLICATION_JSON })
	@Consumes(value = { MediaType.APPLICATION_JSON })
	public Response leaveGroup(@PathParam("member_id") Integer member_id,
			@PathParam("group_id") Integer group_id) {
		int result=0;
		GroupService groupService = new GroupService();
		result = groupService.leaveGroup(member_id, group_id);
		
		if (result == 0) {
			return Response.notModified().build();
		} else {
			return Response.ok().build();
		}
	}

	// get group info
	@GET
	@Path("groups/{group_id}")
	@Produces(value = { MediaType.APPLICATION_JSON })
	public Response getGroup(@PathParam("group_id") int gid) {

		GroupService groupservice = new GroupService();

		Group group = groupservice.getGroup(gid);
		if (group == null) {
			return Response.noContent().build();
		} else {

			return Response.ok().entity(group).build();

		}
	}
	// get user's status in group
		@GET
		@Path("users/{user_id}/groups/{group_id}/userstatus")
		@Produces(value = { MediaType.TEXT_PLAIN })
		public Response getGroup(@PathParam("group_id") int gid,@PathParam("user_id") int uid) {

			GroupService groupservice = new GroupService();
		//	System.out.println("inside resource");
			int status = groupservice.getUserStatusInGroup(gid,uid);
			if (status == 0) {
				return Response.noContent().build();
			} else {

				return Response.ok().entity(status).build();

			}
		}
	
		// get owner of group
				@GET
				@Path("users/{user_id}/groups/{group_id}/ownername")
				@Produces(value = { MediaType.TEXT_PLAIN })
				public Response getGroupOwner(@PathParam("group_id") int gid,@PathParam("user_id") int uid) {

					GroupService groupservice = new GroupService();
				//	System.out.println("inside resource");
					String ownername = groupservice.getGroupOwnerName(gid,uid);
					if (ownername == null) {
						return Response.noContent().build();
					} else {

						return Response.ok().entity(ownername).build();

					}
				}
		
	@POST
	@Path("users/{user_id}/groups/{group_id}/posts")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(value = { MediaType.APPLICATION_JSON })
	public Response createGroupPost(
			@PathParam("group_id") String group_id,
			@PathParam("user_id") String user_id,
			@FormDataParam("file") InputStream fileInputStream,
			@FormDataParam("file") FormDataContentDisposition fileFormDataContentDisposition,
			@FormDataParam("postdata") String postdata) {

		System.out.println("group post input :" + group_id + "  "
				+ fileInputStream + "  " + postdata + "  "
				+ fileFormDataContentDisposition);

		String fileName = null;
		if (fileFormDataContentDisposition.getFileName().length() >= 1) {
			System.out.println("inside !ss");
			fileName = fileFormDataContentDisposition.getFileName();
		}
		Post post = new GroupService().createPostInGroup(fileInputStream, fileName,
				postdata, Integer.parseInt(user_id),Integer.parseInt(group_id));

		System.out.println(post.getFullName());
		if (post == null) {
			return Response.status(501).build();
		} else {
			return Response.ok().entity(post).build();
		}
	}

	@GET
	@Path("groups/{group_id}/posts")
	@Produces(value = { MediaType.APPLICATION_JSON })
	public Response getAllGroupPost(@PathParam("group_id") String group_id) {

		List<Post> listOfPost = new GroupService().getAllGroupPost(Integer
				.parseInt(group_id));

		if (listOfPost.isEmpty()) {
			return Response.noContent().build();
		} else {
			return Response.ok()
					.entity(new GenericEntity<List<Post>>(listOfPost) {
					}).build();
		}

	}

}