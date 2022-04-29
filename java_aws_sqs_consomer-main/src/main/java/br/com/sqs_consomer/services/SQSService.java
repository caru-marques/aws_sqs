package br.com.sqs_consomer.services;

import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.*;

import java.util.List;

public class SQSService {
    public static void messageReader(){
        AwsCredentialsProvider credentialsProvider = new AwsCredentialsProvider() {
            @Override
            public AwsCredentials resolveCredentials() {
                return new AwsCredentials() {
                    @Override
                    public String accessKeyId() {
                        return System.getenv("AWS_ACCESS_KEY");
                    }
        
                    @Override
                    public String secretAccessKey() {
                        return System.getenv("AWS_SECRET_KEY");
                    }
                };
            }
        };

        SqsClient sqsClient = SqsClient.builder()
                .region(Region.US_EAST_1)
                .credentialsProvider(credentialsProvider)
                .build();

        //Buscar Fila 
        GetQueueUrlRequest request = GetQueueUrlRequest.builder()                
                 .queueName("fila-caru")  
                .queueOwnerAWSAccountId("755977887883").build();
        GetQueueUrlResponse createResult = sqsClient.getQueueUrl(request);
        
        List<Message> messages = receiveMessages(sqsClient, createResult.queueUrl());
        for (Message mess : messages) {
            System.out.println("Mensagem: " + mess.body());
        }

        deleteMessages(sqsClient, createResult.queueUrl(),  messages);

        sqsClient.close();
    }

    public static  List<Message> receiveMessages(SqsClient sqsClient, String queueUrl) {
        ReceiveMessageRequest receiveMessageRequest = ReceiveMessageRequest.builder()
            .queueUrl(queueUrl)
            .waitTimeSeconds(20) // Long Polling 
            .maxNumberOfMessages(5)
            .build();
        List<Message> messages = sqsClient.receiveMessage(receiveMessageRequest).messages();
        return messages;
    }

    public static void deleteMessages(SqsClient sqsClient, String queueUrl,  List<Message> messages) {
        for (Message message : messages) {
            DeleteMessageRequest deleteMessageRequest = DeleteMessageRequest.builder()
                .queueUrl(queueUrl)
                .receiptHandle(message.receiptHandle())
                .build();
            sqsClient.deleteMessage(deleteMessageRequest);
        }
   }
}