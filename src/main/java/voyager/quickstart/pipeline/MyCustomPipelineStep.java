package voyager.quickstart.pipeline;

import java.util.Date;

import voyager.api.domain.model.entry.Entry;
import voyager.api.pipeline.PipelinePayload;
import voyager.api.pipeline.PipelineStep;
import voyager.api.pipeline.StepAction;


public class MyCustomPipelineStep implements PipelineStep {

  public MyCustomPipelineStep() {
    
  }
  
  @Override
  public StepAction process(PipelinePayload payload) throws Exception {
   
    Entry entry = payload.getEntry();
    entry.addWarning("custom_step", 
      "this entry used a custom step at: "+new Date() );
    
    if(false) {
      // If you want to terminate the pipeline (and cancel indexing)
      // return the STOP action
      return StepAction.STOP;
    }
    return StepAction.CONTINUE;
  }
}
