import com.mundor.fogardixital.core.api.DriverConfiguration;
import com.mundor.fogardixital.core.api.DriverLifecycle;
import com.mundor.fogardixital.core.api.PollableComponent;
import com.mundor.fogardixital.core.api.command.Command;
import com.mundor.fogardixital.core.api.command.CommandSubscriber;
import com.mundor.fogardixital.core.api.command.OnCommandPublishListener;
import com.mundor.fogardixital.core.api.exceptions.ComponentException;
import com.mundor.fogardixital.core.api.observation.ObservationPublisher;


public class ExampleDriver implements DriverLifecycle, PollableComponent,
		OnCommandPublishListener {
	
	
	private CommandSubscriber commandSubscriber;
	private ObservationPublisher observationPublisher;

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
	public void onCommandPublish(Command arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void poll() {
		// TODO Auto-generated method stub

	}

	@Override
	public void init(DriverConfiguration arg0) throws ComponentException {
		// TODO Auto-generated method stub
		
		this.commandSubscriber = arg0.getCommandSubscriber();
		this.observationPublisher = arg0.getObservationPublisher();
		

	}

}
