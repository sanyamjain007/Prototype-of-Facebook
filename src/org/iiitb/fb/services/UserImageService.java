package org.iiitb.fb.services;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Calendar;

import org.iiitb.fb.modals.Profile;

public class UserImageService {
	public String getImages(String userId) {
		// TODO Auto-generated method stub
		File folder = new File(PathSetup.imagePath+"users/"+userId+"/images");
	    File[] listOfFiles = folder.listFiles();
	    int size=listOfFiles.length;
	    String images=null;
	    System.out.println("files");
	    for (int i = 0; i < listOfFiles.length; i++) 
	    {	     
	       System.out.println("File " + listOfFiles[i].getName());
	       images=images+"_"+listOfFiles[i].getName();   
	    }		
			System.out.println(images);
			return images;
	}
	
	@SuppressWarnings("finally")
	public String uploadProfilePic(InputStream fileInputStream,String fileName, String userName) {	
		File folder = new File(PathSetup.imagePath+"users/"+userName);
	    File[] listOfFiles = folder.listFiles();
	    listOfFiles[1].delete();
	    //System.out.println(userName+" "+listOfFiles[1].getName()+" profilepic to delete");
	   
	    //System.out.println(f.delete());
		OutputStream outputStream=null;
		OutputStream outputStream1=null;
		fileName=""+Calendar.getInstance().getTimeInMillis()+fileName;
		String path=PathSetup.imagePath+"users/"+userName+"/images/";
		String profilePicPath=PathSetup.imagePath+"users/"+userName+"/";
		Profile profile=new Profile();
		try{
			outputStream=new FileOutputStream(new File(path+fileName));
			outputStream1=new FileOutputStream(new File(profilePicPath+"p"+fileName));
			int read = 0;
            byte[] bytes = new byte[1024];
            while ((read = fileInputStream.read(bytes)) != -1) { 
            	outputStream.write(bytes, 0, read);
            	outputStream1.write(bytes, 0, read);               
            }
            outputStream.close();
            outputStream1.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally{	  
			if(outputStream!=null)
			{
			profile.setProfilePicUrl("p"+fileName);
			System.out.println(profile.getProfilePicUrl()+"........................................says hello");
				return profile.getProfilePicUrl();
			}
			return null;
		}			
	}

	public String addPhoto(InputStream fileInputStream, String fileName,String profileId) {
		fileName="" + Calendar.getInstance().getTimeInMillis()+fileName;
		OutputStream outputStream=null;
		String path=PathSetup.imagePath+"users/"+profileId+"/images/";
	    System.out.println(fileName);
		try{
			outputStream=new FileOutputStream(new File(path+fileName));
			int read = 0;
            byte[] bytes = new byte[1024];
            while ((read = fileInputStream.read(bytes)) != -1) 
                outputStream.write(bytes, 0, read);       
            outputStream.close();
		}
		catch(Exception e){
			e.printStackTrace();
		}
		finally{	  
			if(outputStream!=null)
				return "uploaded successfully!!!";		
		}
		return null;
	}
		
	public String getProfilePicName(int user_id){   
		File folder = new File(PathSetup.imagePath+"users/"+user_id);
		File[] listOfFiles = folder.listFiles();
		System.out.println("hello");
		System.out.println(user_id+"................................... ");
		return listOfFiles[1].getName();
	}
}