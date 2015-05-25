

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class SubscriberListener extends Thread {

	ServerSocket ss;
	Subscriber sub;
	
	public SubscriberListener(Subscriber sub)
	{
		try {
			ss= new ServerSocket(Constants.SUBSCRIBER_LISTEN);
			this.sub=sub;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	public void run()
	{
		while(true)
		{
			try {
				Socket s= ss.accept();
				ObjectInputStream ois= new ObjectInputStream(s.getInputStream());
				
				String command= (String)ois.readObject();
				if(command.equals("newmessageforyou"))
				{
					TopicMessage  newMessage= (TopicMessage)ois.readObject();
					sub.printNewMessage(newMessage.topic+": "+newMessage.message);
				}
				else if(command.equals("updateyourlist"))
				{
					System.out.println("Updating subscriber topic list");
					ArrayList<String> topiclist= (ArrayList<String>)ois.readObject();
					sub.updateTopics(topiclist);
				}
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
