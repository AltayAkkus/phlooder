//import Phlooder.Flood;

class FormField{		
		public String type;
		public String value;
		public String name;
		public Flood flood_type;
		public String flood_value;
		
		FormField(String t,String v,String n){
			type=t;
			value=v;
			name=n;
			flood_type=new Flood();
		}
		FormField(String t,String v,String n,Flood f){
			type=t;
			value=v;
			name=n;
			flood_type=f;
		}
	}