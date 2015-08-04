package codemagnus.com.dealogeolib.service.notif;

import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClient;
import com.amazonaws.services.sqs.model.CreateQueueRequest;
import com.amazonaws.services.sqs.model.CreateQueueResult;
import com.amazonaws.services.sqs.model.DeleteMessageRequest;
import com.amazonaws.services.sqs.model.GetQueueAttributesRequest;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.amazonaws.services.sqs.model.SendMessageResult;
import com.amazonaws.services.sqs.model.SetQueueAttributesRequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import codemagnus.com.dealogeolib.service.AwsService;
import static codemagnus.com.dealogeolib.utils.LogUtils.LOGI;


public class SimpleQueue {

	private static AmazonSQS sqs = null;
	private static List<Message> lastReceivedMessages = null;
	public static final String QUEUE_URL = "_queue_url";
	public static final String MESSAGE_INDEX = "_message_index";
	public static final String MESSAGE_ID = "_message_id";
	
	public static AmazonSQS getInstance() {
        if ( sqs == null ) {
        	sqs = new AmazonSQSClient(AwsService.credentials);
        	sqs.setRegion(Region.getRegion(Regions.US_EAST_1));
        }
        return sqs;
	}
	
	public static CreateQueueResult createQueue(String queueName) {
		CreateQueueRequest req = new CreateQueueRequest(queueName);
		return getInstance().createQueue(req);
	}
	
	
	public static List<String> getQueueUrls(){
		return getInstance().listQueues().getQueueUrls();
	}
	
	
	public static List<Message> getQueuedMessages(String queueUrl) {
		ReceiveMessageRequest req = new ReceiveMessageRequest(queueUrl);
		return lastReceivedMessages = getInstance().receiveMessage(req).getMessages();
	}
	
	public static List<String> receiveMessageBodies(String queueUrl) {
		List<String> messageBodies = new ArrayList<String>();
		ReceiveMessageRequest req = new ReceiveMessageRequest(queueUrl);
		lastReceivedMessages = getInstance().receiveMessage(req).getMessages();
		for(Message m : lastReceivedMessages) {
			messageBodies.add(m.getBody());
		}
		return messageBodies;
	}
	
	
	public static List<String> receiveMessageIds(String queueUrl) {
		List<String> messageIds = new ArrayList<String>();
		ReceiveMessageRequest req = new ReceiveMessageRequest(queueUrl);
		lastReceivedMessages = getInstance().receiveMessage(req).getMessages();
		for(Message m : lastReceivedMessages) {
			messageIds.add(m.getMessageId());
		}
		return messageIds;
	}
	
	
	public static String getMessageBody(int messageIndex) {
		if (lastReceivedMessages == null) {
			return "";
		} else {
			return lastReceivedMessages.get(messageIndex).getBody();
		}
	}
	
	
	public static SendMessageResult sendMessage(String queueUrl, String body) {
		SendMessageRequest req = new SendMessageRequest(queueUrl, body);
		return getInstance().sendMessage(req);
	}
	
	
	public static void deleteMessage(String queueUrl, String msgHandle) {
		DeleteMessageRequest req = new DeleteMessageRequest(queueUrl, msgHandle);
		getInstance().deleteMessage(req);
	}
	
	
	public static String getQueueArn(String queueUrl) {
		GetQueueAttributesRequest req = new GetQueueAttributesRequest(queueUrl).withAttributeNames("QueueArn");
		return getInstance().getQueueAttributes(req).getAttributes().get("QueueArn");
	}
	
	
	public static void allowNotifications(String queueUrl, String topicArn) {
		HashMap<String, String> attributes = new HashMap<String, String>();
		attributes.put("Policy", generateSqsPolicyForTopic(getQueueArn(queueUrl), topicArn));
		getInstance().setQueueAttributes(new SetQueueAttributesRequest(queueUrl, attributes));
	}
	
	private static String generateSqsPolicyForTopic(String queueArn, String topicArn) {
        String policy =
            "{ " +
            "  \"Version\":\"2008-10-17\"," +
            "  \"Id\":\"" + queueArn + "/policyId\"," +
            "  \"Statement\": [" +
            "    {" +
            "        \"Sid\":\"" + queueArn + "/statementId\"," +
            "        \"Effect\":\"Allow\"," +
            "        \"Principal\":{\"AWS\":\"*\"}," +
            "        \"Action\":\"SQS:SendMessage\"," +
            "        \"Resource\": \"" + queueArn + "\"," +
            "        \"Condition\":{" +
            "            \"StringEquals\":{\"aws:SourceArn\":\"" + topicArn + "\"}" +
            "        }" +
            "    }" +
            "  ]" +
            "}";
        	LOGI("POLICY", policy);
        return policy;
    }
}
