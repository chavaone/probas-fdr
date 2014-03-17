 
package com.mundor.fogardixital.bolseiros;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
 
import com.google.gson.Gson;
 
import com.mundor.fogardixital.core.api.AdapterConfiguration;
import com.mundor.fogardixital.core.api.AdapterLifecycle;
import com.mundor.fogardixital.core.api.command.Command;
import com.mundor.fogardixital.core.api.command.CommandPublisher;
import com.mundor.fogardixital.core.api.exceptions.ComponentException;
import com.mundor.fogardixital.core.api.exceptions.ServiceNotFoundException;
 
import com.mundor.fogardixital.core.services.restb.RESTBService;
import com.mundor.fogardixital.restb.Message;
import com.mundor.fogardixital.restb.MessageResponse;
import com.mundor.fogardixital.restb.exceptions.CanNotProcessServiceIdException;
import com.mundor.fogardixital.restb.exceptions.IncorrectMessageFormatException;
 
import com.mundor.fogardixital.restb.processors.MessageProcessor;



public class CommandAdapter implements AdapterLifecycle, MessageProcessor{
 
        protected final static Logger logger = LoggerFactory.getLogger(CommandAdapter.class.getPackage().getName());
        private final String serviceId = "remoteCommand";
        private CommandPublisher cmp;
        private Gson gson;
       
        @Override
        public void start() throws ComponentException {
                logger.info( "Nothig to start");
        }
 
        @Override
        public void stop() throws ComponentException {
                logger.info( "Nothing to stop");
        }
 
        @Override
        public void destroy() throws ComponentException {      
                logger.info( "Nothing to destroy");
        }
 
       
        @Override
        public void init(AdapterConfiguration adapterConfiguration)
                        throws ComponentException {
        		gson = new Gson();
        		try {
                        adapterConfiguration.getService(RESTBService.class)
                                .getManager().registerListener(serviceId, this);
                } catch (ServiceNotFoundException e) {
                        logger.error("Unable to start adapter. Error while getting RESTBManager service: {}", e);
                        throw new ComponentException("Unable to start adapter. Error while getting RESTBManager service", e);
                }      
                cmp = adapterConfiguration.getCommandPublisher();
        }
       
       
        private MessageResponse matchHeaders(MessageResponse toRespond, Message message){
               
                if (message.hasSTBRequestId() && (!toRespond.hasSTBRequestId()))
                        toRespond.addHeader(Message.STB_REQUEST_ID, message.getSTBRequestId());
                if (message.hasHeader(Message.REQUEST_ID) && (!toRespond.hasHeader(Message.REQUEST_ID)))
                        toRespond.addHeader(Message.REQUEST_ID, message.getRequestId());
                if (message.hasHeader(Message.SERVICE_ID) && !toRespond.hasHeader(Message.SERVICE_ID))
                toRespond.addHeader(Message.SERVICE_ID, message.getServiceId());
 
                return toRespond;
        }
 
        @Override
        public MessageResponse processMessage(Message message)
                        throws CanNotProcessServiceIdException,
                        IncorrectMessageFormatException {
               
                if (message == null) throw new IncorrectMessageFormatException("Message is null");
               
                if (!serviceId.equals(message.getServiceId())) throw
                        new CanNotProcessServiceIdException(message.getServiceId(), "Expected was "+serviceId);
 
                if (message.getContent() == null) throw new IncorrectMessageFormatException("Content of the message is null");
               
                String json = new String(message.getContent());
                logger.debug("Command received is {}", json);
                try{
                       
                        Command com = gson.fromJson(json, Command.class);
                        if (com != null){
                                logger.debug("About to publish command: {}", com);
                                cmp.publish(com);
                        }
                }
                catch (Exception e){
                        throw new IncorrectMessageFormatException("Error while obtaining command from message: "+e.toString());
                }
               
                return matchHeaders(MessageResponse.Responses.OK.builder().getAsResponse(), message);
        }
 
       
}