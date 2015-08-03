package codemagnus.com.dealogeolib.service.notif;

import android.content.Context;
import android.provider.Settings.Secure;

import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sns.AmazonSNSClient;
import com.amazonaws.services.sns.model.ConfirmSubscriptionResult;
import com.amazonaws.services.sns.model.CreatePlatformEndpointRequest;
import com.amazonaws.services.sns.model.CreatePlatformEndpointResult;
import com.amazonaws.services.sns.model.CreateTopicRequest;
import com.amazonaws.services.sns.model.CreateTopicResult;
import com.amazonaws.services.sns.model.DeleteTopicRequest;
import com.amazonaws.services.sns.model.ListSubscriptionsByTopicRequest;
import com.amazonaws.services.sns.model.PublishRequest;
import com.amazonaws.services.sns.model.PublishResult;
import com.amazonaws.services.sns.model.SubscribeRequest;
import com.amazonaws.services.sns.model.SubscribeResult;
import com.amazonaws.services.sns.model.Subscription;
import com.amazonaws.services.sns.model.Topic;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import codemagnus.com.dealogeolib.service.AwsService;


public class SimpleNotification {

	private static AmazonSNSClient sns = null;
	public static final String TOPIC_ARN = "_Topic_Arn";
	
	public static AmazonSNSClient getInstance() {
		if (sns == null) {
			sns = new AmazonSNSClient(AwsService.credentials);
			sns.setRegion(Region.getRegion(Regions.US_EAST_1));
		}
		return sns;
	}
	
	
	@SuppressWarnings("rawtypes")
	public static List<String> getTopicNames() {
		List topics = getInstance().listTopics().getTopics();
		Iterator iter = topics.iterator();
		List<String> topicNames = new ArrayList<String>(topics.size());
		while(iter.hasNext()) {
			topicNames.add(((Topic)iter.next()).getTopicArn());
		}
		return topicNames;
	}
	
	
	@SuppressWarnings("rawtypes")
	public static List<String> getSubscriptionNamesByTopic(String topicARN) {
		List subscriptions;
		if (topicARN != null) {
			ListSubscriptionsByTopicRequest req = new ListSubscriptionsByTopicRequest(topicARN);
			subscriptions = getInstance().listSubscriptionsByTopic(req).getSubscriptions();
		} else {
			subscriptions = getInstance().listSubscriptions().getSubscriptions();
		}
		Iterator iter = subscriptions.iterator();
		List<String> subscriptionNames = new ArrayList<String>(subscriptions.size());
		while(iter.hasNext()) {
			subscriptionNames.add(((Subscription)iter.next()).getEndpoint());
		}
		return subscriptionNames;
	}
	
	
	public static List<String> getSubscriptionNames() {
		return getSubscriptionNamesByTopic(null);
	}
	
	
	public static CreateTopicResult createTopic(String name) {
		CreateTopicRequest req = new CreateTopicRequest(name);
		return getInstance().createTopic(req);
	}
	
	
	public static void deleteTopic(String name) {
		DeleteTopicRequest req = new DeleteTopicRequest(name);
		getInstance().deleteTopic(req);
	}
	
	
	public static PublishResult publish(String topicARN, String msg) {
		PublishRequest req = new PublishRequest(topicARN, msg);
		return getInstance().publish(req);
	}
	
	
	public static SubscribeResult subscribe(String topicARN, String protocol, String endPoint) {
		SubscribeRequest req = new SubscribeRequest(topicARN, protocol, endPoint);
		return getInstance().subscribe(req);
	}

	public static ConfirmSubscriptionResult confirmSubscription(String topicArn, String token){
//		ConfirmSubscriptionResult confirmSubscriptionResult = new ConfirmSubscriptionResult();
		return getInstance().confirmSubscription(topicArn, token);
	}
	
	public static String getArnFromTopic(String topicName) {
		List<String> listOfTopicARN = getTopicNames();
		Iterator<String> iter = listOfTopicARN.iterator();
		while(iter.hasNext()) {
			String temp = iter.next();
			if(temp.endsWith(topicName))
				return temp;
		}
		return null;
	}
	
	
	public static CreatePlatformEndpointResult createApplicationPlatformEndpoint(Context context) {
		CreatePlatformEndpointRequest req = new CreatePlatformEndpointRequest();
		req.setToken(AwsService.TOKEN);
		req.setPlatformApplicationArn(AwsService.applicationArn);
		req.setCustomUserData(Secure.getString(context.getContentResolver(), Secure.ANDROID_ID));
		return getInstance().createPlatformEndpoint(req);
	}
}
