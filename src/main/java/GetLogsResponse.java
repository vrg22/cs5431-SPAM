
public class GetLogsResponse {
	
	private String[] logs;
	private String[] origLogNames;
	
  public GetLogsResponse(String[] logs, String[] origNames) {
      this.logs = logs;
      this.origLogNames = origNames;
  }

  public String[] getLogs() {
  	return logs;
  }
  
  public String[] getLogNames() {
	return origLogNames;
  }
}
