import java.util.*;
import java.util.regex.*;
import java.io.*;
import java.lang.*;

class regexp{
	regexp(){
		
		// \"[^\">]*\"
		String s="<form method=\"POST\" action=index.php>";
		Pattern formPattern=Pattern.compile("\"[^\">]*\"|=[^\"][\\S]*[\\s>$]",Pattern.CASE_INSENSITIVE);
		Matcher m=formPattern.matcher(s);
		while(m.find()){
			System.out.println(m.group(0));
			//System.out.println(m.groupCount());
		}
		String[] res=formPattern.split(s);
		for(int i=0;i<res.length;i++){
			//System.out.println(i+".:"+res[i]);
		}
	}
	public static void main(String args[]){
		regexp r=new regexp();
	}
}