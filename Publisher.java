
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;

public class Publisher {

	public ArrayList<String> topiclist;
	public HashMap<String, ArrayList<String>> topToSubMap;
	public ArrayList<String> subscriberList;
	

	public Publisher() {
		topiclist = new ArrayList<>();
		subscriberList = new ArrayList<String>();
		topToSubMap = new HashMap<>();

		for (String topic : topiclist) {
			topToSubMap.put(topic, new ArrayList<String>());
		}
	}

	
	public void addTopic(String topic) {
		if (!topiclist.contains(topic)) {
			topiclist.add(topic);
			updateTopicsToSubscriber();
		}
		
	}

	public void updateTopicsToSubscriber() {
		System.out.println(subscriberList);
		ArrayList<String> toberemoved = new ArrayList<String>();
		for (String ip : subscriberList) {
			try {
				Socket sub = new Socket(ip, Constants.SUBSCRIBER_LISTEN);
				ObjectOutputStream oos = new ObjectOutputStream(
						sub.getOutputStream());
				oos.writeObject("updateyourlist");
				oos.writeObject(topiclist);
				oos.flush();
				oos.close();
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block

			} catch (IOException e) {
				// TODO Auto-generated catch block
				toberemoved.add(ip);
			}

		}
		subscriberList.removeAll(toberemoved);
	}

	public void addSubscriber(Socket sub) {
		if (!subscriberList.contains(sub.getInetAddress().getHostAddress())) {
			subscriberList.add(sub.getInetAddress().getHostAddress());
		}
	}

	public void addSubscribertoTopic(Socket sub, String topic) {
		if (topToSubMap.containsKey(topic)) {

			if (!topToSubMap.get(topic).contains(
					sub.getInetAddress().getHostAddress())) {
				topToSubMap.get(topic).add(
						sub.getInetAddress().getHostAddress());
			}
		} 
		else {
			ArrayList<String> sublist = new ArrayList<String>();
			sublist.add(sub.getInetAddress().getHostAddress());
			topToSubMap.put(topic, sublist);
		}
	}

	public void removeSubscriberfromTopic(Socket sub, String topic) {

		if (topToSubMap.get(topic).contains(
				sub.getInetAddress().getHostAddress())) {
			topToSubMap.get(topic)
					.remove(sub.getInetAddress().getHostAddress());

		}

	}

	public void notifyAllSubscribers(String topic, String message) {
		for (String s : topToSubMap.get(topic)) {
			try {
				String ip = s;
				System.out.println("Sending message to " + s);
				Socket sub = new Socket(ip, Constants.SUBSCRIBER_LISTEN);
				ObjectOutputStream oos = new ObjectOutputStream(
						sub.getOutputStream());
				oos.writeObject("newmessageforyou");
				oos.writeObject(new TopicMessage(topic, message));
				oos.flush();
				oos.close();
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

	public static void main(String args[]) {
		Publisher pub = new Publisher();
		PublisherListener publisten = new PublisherListener(pub);
		publisten.start();
		while (true) {
			System.out.println("1.Publish a message\n2.Add new topic ");
			BufferedReader br = new BufferedReader(new InputStreamReader(
					System.in));
			try {
				int choice = Integer.parseInt(br.readLine());
				if (choice == 1) {
					System.out.println("Enter a topic from following list");
					for (String t : pub.topiclist) {
						System.out.println(t);

					}
					String topic = br.readLine();
					if (pub.topiclist.contains(topic)) {
						System.out.println("Enter message");
						String message = br.readLine();
						pub.notifyAllSubscribers(topic, message);
					}
				}
				else if(choice==2)
				{
					System.out.println("Enter name of the topic");
					String name= br.readLine();
					pub.addTopic(name);
				}
			} catch (NumberFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
