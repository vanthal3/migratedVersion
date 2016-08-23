package hudson.plugins.visualizer;

import java.util.Date;
import java.io.Serializable;
import java.util.Map;
import java.util.HashMap;
import java.util.Random;

/**
 * HTTP POJO. Holds information about the Request.
 */
public class HttpSample implements Serializable {

	private static final long serialVersionUID = -3531990216789038711L;

	private  Map<Integer, AssertionResult> assertions;
	private Integer httpId;
	public String httpCode = "";
	public long duration;
	public boolean successful;
	public String responseMessage;
	public Date date;
	public String Uri;
	public String threadname;
	public double sizeInKb;
	public String errorCount;


	public HttpSample(Integer myid){
		httpId=myid;
		assertions=new HashMap<Integer, AssertionResult>();
	}

	public int addAssertion(){
		int arId=createID();
		AssertionResult ar =new AssertionResult(arId);
		assertions.put(arId, ar);
		return arId;
	}

	public Map<Integer, AssertionResult> getAssertions(){
		return assertions;
	}

	public void setErrorCount(String ec){
		errorCount=ec;
	}

	public String getErrorCount(){
		return errorCount;
	}
	public boolean isSuccessful() {
		return successful;
	}

	public void setThreadName(String threadn){
		threadname=threadn;
	}

	public String getThreadname(){
		return threadname;
	}

	public String getResponseMessage(){
		return responseMessage;
	}

	public AssertionResult getARObject(Integer id){
		return assertions.get(id);
	}

	public long getDuration() {
		return duration;
	}

	public void setResponseMessage(String rm){
		responseMessage=rm;
	}

	public Date getDate() {
		return date;
	}

	public String getUri() {
		return Uri;
	}

	public void setDuration(long dur) {
		duration = dur;
	}

	public void setSuccessful(boolean success) {
		successful = success;
	}

	public void setDate(Date time) {
		date = time;
	}

	public void setUri(String uri) {
		Uri = uri;
	}

	public Integer getHttpId(){
		return httpId;
	}

	public String getHttpCode() {
		return httpCode;
	}

	public void setHttpCode(String httpCd) {
		httpCode = httpCd;
	}

	public int compareTo(HttpSample o) {
		return (int) (getDuration() - o.getDuration());
	}

	public double getSizeInKb() {
		return sizeInKb;
	}

	public void setSizeInKb(double d) {
		sizeInKb = d;
	}

	public Integer createID(){

		Random r = new Random();
		Integer id = r.nextInt(9999);
		return id;
	}
}
