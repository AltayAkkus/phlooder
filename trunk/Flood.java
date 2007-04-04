/**
 * @author The Blue Overdose Project
 * E-mail: blueover AT gmail com
 * Phlooder Website: http://code.google.com/p/phlooder
 * */
class Flood{
		public static final int NOP=0;
		public static final int INT=1;
		public static final int STR=2;
		public static final int EMAIL=3;
		public static final int SELECT=4;
		public int type;
		public int length;

		
		Flood(int t,int l){
			type=t;
			length=l;
		}
		Flood(){
			type=STR;
			length=4;
		}
}