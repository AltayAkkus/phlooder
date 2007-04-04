/*The flooder thread. Calls form.send(). 
	 * */
	class PhlooderThread extends Thread{
		
		private boolean blinker;
		private int thread_check;
		private Form form;
		private boolean started=false;
		
		public PhlooderThread(Form f){
			form=f;
			thread_check=0;
		}
		
		/**The original stop() method is unsafe!
		 * This method is to be used instead of that!
		 * I don't know if this solution is good enough, but 
		 * the one on sun.com doesn't work :P
		 * */
		public void pause(){
			blinker=false;
			System.out.println(thread_check+" requests sent.");
		}
		public boolean isStarted(){
			return started;
		}
		public void restart(){
			if(!blinker){
				System.out.println("Restarting...");
				blinker=true;
			}
		}
		public void run(){
			started=true;
			blinker=true;
			thread_check=0;
			System.out.println(form.toString());
			while(blinker){
				form.send();
				try{
				sleep(1000);
				}catch(InterruptedException ie){
					System.out.println("Interrupt cought!");
					return;
				}
			}
		}
	}