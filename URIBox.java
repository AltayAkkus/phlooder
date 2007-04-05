/**
 * @author The Blue Overdose Project
 * E-mail: blueover AT gmail com
 * Phlooder Website: http://code.google.com/p/phlooder
 * */

/**
 * This class is needed because we dont want to 
 * show the URI-s in their full length, so we have 
 * to assign the checkbox labels with the actual URIs. 
 * */

class URIBox{
	private String uri;
	private String boxLabel;
	private int maxLength=40;
	
	/**
	 * @param u
	 * The URI
	 * @param l
	 * Label of the box. This appears if URI is too long. 
	 * */
	URIBox(String u, String l){
		uri=u;
		boxLabel=l;
	}
	/**
	 * @param u
	 * The URI 
	 * */
	URIBox (String u){
		uri=u;
		if (uri.length()>maxLength){
			int partLength=(int)Math.ceil((maxLength-3)/2);
			boxLabel=u.substring(0,partLength)+"..."+u.substring(u.length()-partLength-1);		
		}else{
			boxLabel=u;
		}
	}
	public String getURI(){
		return uri;
	} 
	
	public String getLabel(){
		return boxLabel;
	}
	public void setURI(String u){
		uri=u;
	}
	public void setLabel(String l){
		boxLabel=l;
	}
}