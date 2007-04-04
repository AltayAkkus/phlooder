import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

class Form{
		String method;	
		String action;
		String original_uri;
		ArrayList fields;
		
		
		Form(String a,String m){
			action=a;
			method=m;
			fields=new ArrayList();
			
		}
		Form (){
			action="";
			method="GET";
			fields=new ArrayList();
			
		}
		public void addField(FormField f){
			fields.add(f);
		}
		public void setMethod(String m){
			method=m;
		}
		
		public void setAction(String a){
			action=a;
		}
		
		public FormField getFieldByName(String name){
			return null;
		}
		
		public FormField getFieldByIndex(int index){
			Iterator it=fields.iterator();
			FormField f=null;
			for (int i=0;it.hasNext() && i<=index;i++){
				f=(FormField)it.next();
			}
			return f;
		}
		public String toString(){
			String ret=action+"->"+method+"\n";
			Iterator it=fields.iterator();
			while (it.hasNext()){
				FormField f=(FormField)it.next();
				String s=f.name+"("+f.type+")="+f.value+"\n";
				//System.out.println(s);
				ret=ret.concat(s.toString());
				
				
			}
			return ret;
		}
		int getInputCount(){
			return fields.size();
		}
		/*Sets the URI of the file that contains the form.
		 * */
		public void setURI(String uri){
			original_uri=uri;
		}
		
		/**Generates the URI of the action script, then assigns new random 
		 * values to the fields using generateRandom<TYPE>() methods, 
		 * then calls sendViaGET() or sendViaPOST() to send the form data.
		 * */
		public void send(){
			//thread_check++;
			
			try{
				URI uri=new URI(original_uri);
				uri=uri.resolve(action);
				URL url=uri.toURL();
				
				// Updating fields with random values
				Iterator i=fields.iterator();
				//System.out.println(url.toString());
				while(i.hasNext()){
					FormField f=(FormField)i.next();
					int len=f.flood_type.length;
					if(f.flood_type.type==Flood.INT)
						f.flood_value=generateRandomInt(len);
					else if (f.flood_type.type==Flood.EMAIL)
						f.flood_value=generateRandomEmail();
					else if (f.flood_type.type==Flood.STR)
						f.flood_value=generateRandomString(len);
					else if (f.flood_type.type==Flood.SELECT){
						String[] values=f.value.split("#");
						Random r=new Random();
						do{
							f.flood_value=values[r.nextInt(values.length)];
							r.setSeed(r.nextLong());
						}while(f.flood_value.equals("#") || f.flood_value.equals(null));
						System.out.println("Select value:"+f.flood_value);
					}
					else if (f.flood_type.type==Flood.NOP){
						f.flood_value=f.value;
					}
				}
				
				if (method.toUpperCase().equals("POST"))
					sendViaPOST(url);
				else
					sendViaGET(url);
				
				System.out.println(original_uri+"->"+uri.resolve(action).toString());
				
			}catch(URISyntaxException use){
				System.out.println("Invalid URI!");
				return;
			}catch(MalformedURLException mue){
				System.out.println("Cannot convert original URI to URL!");
				return;
			}
			
		}

		String generateRandomInt(int length){
			String ret=new String();
			Random r=new Random();
			for (int i=0;i<length;i++){
				r.setSeed(r.nextLong());
				ret+=r.nextInt(10);
			}
			return ret;
		}
		String generateRandomString(int length){
			String ret=new String();
			Random r=new Random();
			String chars="abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
			for (int i=0;i<length;i++){
				r.setSeed(r.nextLong());
				ret+=chars.charAt(r.nextInt(chars.length()));
			}
			return ret;
		}
		
		String generateRandomEmail(){
			String ret=new String();
			Random r = new Random();
			ret=generateRandomString(r.nextInt(15)).toLowerCase()+"@"+generateRandomString(r.nextInt(25)).toLowerCase()+"."+generateRandomString(2).toLowerCase();
			return ret;
		}

		void sendViaPOST(URL url){
			try{
				URLConnection conn=url.openConnection();
				conn.setDoOutput(true);
				conn.setUseCaches(false);
				conn.setRequestProperty("Content-type", "application/x-www-form-urlencoded");
				Iterator i=fields.iterator();
				String data=null;
				if (i.hasNext()){
					FormField firstfield=(FormField)i.next();
					data=new String();
					data=URLEncoder.encode(firstfield.name,"UTF-8")+"="+URLEncoder.encode(firstfield.flood_value,"UTF-8");
				}
				while(i.hasNext()){
					FormField field=(FormField)i.next();
					if (field.name==null || field.flood_value==null){
						System.out.println("Null van itt:"+field.name);
					}
					data+="&"+URLEncoder.encode(field.name,"UTF-8")+"="+URLEncoder.encode(field.flood_value,"UTF-8");
				}
				if (data!=null){
					System.out.println(data);
					DataOutputStream wr =new DataOutputStream(conn.getOutputStream());
					wr.writeBytes(data);
					wr.flush();
					BufferedReader rd=new BufferedReader(new InputStreamReader(conn.getInputStream()));
					String line;
					while((line=rd.readLine()) !=null){
						System.out.println("Response:"+line);
					}
					
					wr.close();
					rd.close();
				}
			}catch(IOException ioe){
				System.out.println("Cannot write to URL!");
				return;
			}
		}
		void sendViaGET(URL url){
			try{
				Iterator i=fields.iterator();
				String data=null;
				if (i.hasNext()){
					FormField firstfield=(FormField)i.next();
					data=new String();
					data=URLEncoder.encode(firstfield.name,"UTF-8")+"="+URLEncoder.encode(firstfield.flood_value,"UTF-8");
				}
				while(i.hasNext()){
					FormField field=(FormField)i.next();
					data+="&"+URLEncoder.encode(field.name,"UTF-8")+"="+URLEncoder.encode(field.flood_value,"UTF-8");
				}
				if (url.toString().indexOf("?")>0){
					data="&".concat(data);
				}else data="?".concat(data);
				URL geturl=new URL(url.toString().concat(data));
				geturl.getContent();
			}catch(IOException ioe){
				System.out.println("Cannot write to URL!");
				return;
			}
		}
	}
	