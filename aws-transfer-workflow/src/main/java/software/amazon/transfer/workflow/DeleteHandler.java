package software.amazon.transfer.workflow;

import software.amazon.awssdk.services.transfer.TransferClient;
import software.amazon.awssdk.services.transfer.model.DeleteWorkflowRequest;
import software.amazon.awssdk.services.transfer.model.InternalServiceErrorException;
import software.amazon.awssdk.services.transfer.model.InvalidRequestException;
import software.amazon.awssdk.services.transfer.model.ResourceNotFoundException;
import software.amazon.awssdk.services.transfer.model.TransferException;
import software.amazon.cloudformation.exceptions.CfnGeneralServiceException;
import software.amazon.cloudformation.exceptions.CfnInvalidRequestException;
import software.amazon.cloudformation.exceptions.CfnNotFoundException;
import software.amazon.cloudformation.exceptions.CfnServiceInternalErrorException;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.Logger;
import software.amazon.cloudformation.proxy.OperationStatus;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.ProxyClient;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class DeleteHandler extends BaseHandlerStd {
    @Override
    public ProgressEvent<ResourceModel, CallbackContext> handleRequest(
            final AmazonWebServicesClientProxy proxy,
            final ResourceHandlerRequest<ResourceModel> request,
            final CallbackContext callbackContext,
            final ProxyClient<TransferClient> proxyClient,
            final Logger logger) {

        final ResourceModel model = request.getDesiredResourceState();

        final DeleteWorkflowRequest deleteWorkflowRequest = DeleteWorkflowRequest.builder()
                .workflowId(model.getWorkflowId())
                .build();
        try (TransferClient client = proxyClient.client()) {
            proxy.injectCredentialsAndInvokeV2(deleteWorkflowRequest, client::deleteWorkflow);
            logger.log(
                    String.format("%s %s deleted successfully", ResourceModel.TYPE_NAME, model.getPrimaryIdentifier()));
        } catch (InvalidRequestException e) {
            throw new CfnInvalidRequestException(e.getMessage() + " " + deleteWorkflowRequest.toString(), e);
        } catch (InternalServiceErrorException e) {
            throw new CfnServiceInternalErrorException("deleteWorkflow", e);
        } catch (ResourceNotFoundException e) {
            throw new CfnNotFoundException(
                    ResourceModel.TYPE_NAME, model.getPrimaryIdentifier().toString());
        } catch (TransferException e) {
            throw new CfnGeneralServiceException(e.getMessage(), e);
        }

        return ProgressEvent.<ResourceModel, CallbackContext>builder()
                .status(OperationStatus.SUCCESS)
                .build();
    }
}
