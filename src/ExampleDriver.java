import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

import com.mundor.fogardixital.core.api.DriverConfiguration;
import com.mundor.fogardixital.core.api.DriverLifecycle;
import com.mundor.fogardixital.core.api.PollableComponent;
import com.mundor.fogardixital.core.api.command.Command;
import com.mundor.fogardixital.core.api.command.CommandSubscriber;
import com.mundor.fogardixital.core.api.command.OnCommandPublishListener;
import com.mundor.fogardixital.core.api.exceptions.ComponentException;
import com.mundor.fogardixital.core.api.observation.Measure;
import com.mundor.fogardixital.core.api.observation.Observation;
import com.mundor.fogardixital.core.api.observation.ObservationPublisher;


public class ExampleDriver implements DriverLifecycle, OnCommandPublishListener {
	
	
	private CommandSubscriber cs;
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
		
		String photo_b64 = this.getPhoto();
		Measure measure = new Measure.Builder("camera", "photo").value("photo", photo_b64, "string").build();
		op.publish(new Observation(measure));

	}

	private String getPhoto(){

		try (BufferedReader reader = new BufferedReader(new InputStreamReader(this.wget_url.openStream(), "UTF-8"));) {
		    
			String text = "";
			
		    for (String line; (line = reader.readLine()) != null;) {
		        text += line + "\n";
		    }
		    
		    return null;
		} catch (IOException ignore) {
			return null;
		}
		
		
	}

	@Override
	public void init(DriverConfiguration driverconf) throws ComponentException {
		// TODO Auto-generated method stub
		
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
