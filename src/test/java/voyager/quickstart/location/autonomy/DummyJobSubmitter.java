package voyager.quickstart.location.autonomy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import voyager.api.discovery.jobs.DiscoveryJob;
import voyager.api.discovery.jobs.JobSubmitter;
import voyager.api.domain.model.entry.DexField;

public class DummyJobSubmitter implements JobSubmitter {
  static final Logger log = LoggerFactory.getLogger(DummyJobSubmitter.class);
  int count = 0;
  
  @Override
  public void close() throws Exception {}

  @Override
  public void submit(DiscoveryJob job) {
    count++;
    log.info("[{}] Submit: {}", count, job.getEntry().getFieldValue(DexField.NAME));
  }
}