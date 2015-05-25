import java.io.Serializable;



public class TopicMessage implements Serializable {

	public String topic;
	public String message;
	
	public TopicMessage(String topic, String message)
	{
		this.topic=topic;
		this.message=message;
	}
}
