package com.cropop.android.v1.model;
import com.facebook.model.GraphUser;
import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseObject;

@ParseClassName("FacebookGraphUser")
public class ParseGraphUser extends ParseObject{
	
	public ParseGraphUser() {
	
	}

	/**
	 * Contructs a ParseGraphUser from a Facebook user.
	 * 
	 * @param facebookGraphUser
	 */
	public static ParseGraphUser fromFacebookGraphUser(GraphUser facebookGraphUser) {
		ParseGraphUser user = new ParseGraphUser();
		user.setId(facebookGraphUser.getId());
		user.setName(facebookGraphUser.getName());
		user.setFirstName(facebookGraphUser.getFirstName());
		user.setLastName(facebookGraphUser.getLastName());
		user.setMiddleName(facebookGraphUser.getMiddleName());
		user.setLink(facebookGraphUser.getLink());
		user.setUsername(facebookGraphUser.getUsername());
		user.setBirthday(facebookGraphUser.getBirthday());
		user.setLocation(ParseGraphLocation.fromFacebookLocation(facebookGraphUser.getLocation()));
		return user;
	}
	
	public String getId() {
		return getString("id");
	}
	public void setId(String id) {
		if(id != null) put("id", id);
	}
	public String getName() {
		return getString("name");
	}
	public void setName(String name) {
		if(name != null) put("name", name);
	}
	public String getFirstName() {
		return getString("firstName");
	}
	public void setFirstName(String firstName) {
		if(firstName != null) put("firstName", firstName);
	}
	public String getLastName() {
		return getString("lastName");
	}
	public void setLastName(String lastName) {
		if(lastName != null) put("lastName", lastName);
	}
	public String getMiddleName() {
		return getString("middleName");
	}
	public void setMiddleName(String middleName) {
		if(middleName != null) put("middleName", middleName);
	}
	public String getLink() {
		return getString("link");
	}
	public void setLink(String link) {
		if(link != null) put("link", link);
	}
	public String getUsername() {
		return getString("username");
	}
	public void setUsername(String username) {
		if(username != null) put("username", username);
	}
	public String getBirthday() {
		return getString("birthday");
	}
	public void setBirthday(String birthday) {
		if(birthday != null) put("birthday", birthday);
	}

	public ParseGraphLocation getLocation() throws ParseException {
		//Needs a network call
		return (ParseGraphLocation) getRelation("location").getQuery().getFirst();
	}

	public void setLocation(ParseGraphLocation location) {
		if(location != null) put("location", location);
	}
}
