package software.amazon.transfer.webapp;

import software.amazon.awssdk.services.transfer.TransferClient;
import software.amazon.cloudformation.LambdaWrapper;

public class ClientBuilder {
    public static TransferClient getClient() {
        return TransferClient.builder().httpClient(LambdaWrapper.HTTP_CLIENT).build();
    }
}
