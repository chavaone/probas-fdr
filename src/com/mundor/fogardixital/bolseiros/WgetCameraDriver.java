package com.mundor.fogardixital.bolseiros;


import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mundor.fogardixital.core.api.DriverConfiguration;
import com.mundor.fogardixital.core.api.DriverLifecycle;
import com.mundor.fogardixital.core.api.command.Command;
import com.mundor.fogardixital.core.api.command.OnCommandPublishListener;
import com.mundor.fogardixital.core.api.exceptions.ComponentException;
import com.mundor.fogardixital.core.api.observation.Measure;
import com.mundor.fogardixital.core.api.observation.Observation;
import com.mundor.fogardixital.core.api.observation.ObservationPublisher;

public class WgetCameraDriver implements DriverLifecycle, OnCommandPublishListener {
	
    private final static Logger logger = LoggerFactory.getLogger(WgetCameraDriver.class.getPackage().getName());
	private ObservationPublisher op;
	private URL wget_url;

	@Override
	public void destroy() throws ComponentException {
		// TODO Auto-generated method stub

	}

	@Override
	public void start() throws ComponentException {
		// TODO Auto-generated method stub

	}

	@Override
	public void stop() throws ComponentException {
		// TODO Auto-generated method stub

	}

	@Override
	public void onCommandPublish(Command cmd) {
		if (cmd == null)
			return;
		if (! cmd.getCommand().equals("get"))
			return;
		
		logger.info("GET received");
		
		String photo_b64;
		try {
			photo_b64 = this.getPhoto();
			Measure measure = new Measure.Builder("camera", "photo").value("photo", photo_b64, "string").build();
			op.publish(new Observation(measure));
			logger.info("Send Photo");
		} catch (IOException e) {
			logger.error("Error getting photo:" + e.getLocalizedMessage());
		}
		

	}
	
	private String getPhoto() throws IOException{

		try (InputStream openStream = this.wget_url.openStream();)  {
			int contentLength = this.wget_url.openConnection().getContentLength();
			byte[] img_bytes = new byte[contentLength];
			openStream.read(img_bytes);
			return Base64.encodeBase64(img_bytes).toString();
		} catch(IOException e) {
			throw e;
		}
	}

	@Override
	public void init(DriverConfiguration driverconf) throws ComponentException {			
		Map<String, String> parameters = driverconf.getParameters();
		
		if (! parameters.containsKey("url"))
			throw new ComponentException("URL is a mandatory parameter", null);
		
		try {
			this.wget_url = new URL(parameters.get("url"));
		} catch (MalformedURLException e) {
			throw new ComponentException("Invalid URL format", e);
		}
		
		this.op = driverconf.getObservationPublisher();
		driverconf.getCommandSubscriber().subscribe(this, "wget_camera");
		

	}
}
