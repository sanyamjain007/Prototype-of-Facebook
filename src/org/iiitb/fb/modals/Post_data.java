package org.iiitb.fb.modals;

public class Post_data {

	private String post_data_message;
	private String postImage;
	public Post_data() {
		super();
	}
	
	public Post_data(String post_data_message, String postImage) {
		super();
		this.post_data_message = post_data_message;
		this.postImage = postImage;
	}


	public String getPost_data_message() {
		return post_data_message;
	}
	public void setPost_data_message(String post_data_message) {
		this.post_data_message = post_data_message;
	}
	public String getPostImage() {
		return postImage;
	}
	public void setPostImage(String postImage) {
		this.postImage = postImage;
	}

	@Override
	public String toString() {
		return "Post_data [post_data_message=" + post_data_message + ", postImage=" + postImage + "]";
	}
	
	
}

