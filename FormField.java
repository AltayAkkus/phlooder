/**
 * @author The Blue Overdose Project
 * E-mail: blueover AT gmail com
 * Phlooder Website: http://code.google.com/p/phlooder
 * */

/**
 * Represents one field on a html form
 * */
class FormField{		
		public String type;
		public String value;
		public String name;
		public Flood flood_type;
		public String flood_value;
		
		/**
		 * @param t
		 * Type of the field.f For example: text, password, etc..
		 * @param v
		 * Value of the field
		 * @param n
		 * Name of the filed
		 * */
		FormField(String t,String v,String n){
			type=t;
			value=v;
			name=n;
			flood_type=new Flood();
		}
		/**
		 * @param t
		 * Type of the field.f For example: text, password, etc..
		 * @param v
		 * Value of the field
		 * @param n
		 * Name of the filed
		 * @param f
		 * This parameter is used to manually associate <code>Flood</code>
		 * to a <code>FormField</code>
		 * */
		FormField(String t,String v,String n,Flood f){
			type=t;
			value=v;
			name=n;
			flood_type=f;
		}
	}