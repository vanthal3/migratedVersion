package hudson.plugins.visualizer;

import hudson.model.AbstractBuild;
import java.io.IOException;

import java.io.Serializable;
import java.util.*;

import org.xml.sax.SAXException;

/**
 * Represents the core of JmeterVisualizer plugin. It contains all JTL files tested
 *
 * This object belongs under {@link JVisualizerReportMap}.
 */
public class JVisualizerReport implements Serializable,
        Comparable<JVisualizerReport> {

  private static final long serialVersionUID = 675698410989941826L;
  private transient JVisualizerBuildAction buildAction;
  private String reportFileName = null;
  private boolean reportStatus=false;

  /**
   */
  private final Map<Integer, HttpSample> httpSampleMap = new LinkedHashMap<Integer, HttpSample>();
  private final Map<Integer, HttpSample> failedhttpSampleMap = new LinkedHashMap<Integer, HttpSample>();

  private JVisualizerReport lastBuildReport;

  /**
   * A lazy cache of all UriReports, reverse-ordered.
   */
  private transient List<HttpSample> httpSampleOrdered = null;

  /**
   * The amount of http samples that are not successful.
   */
  private int nbError = 0;

  /**
   * The amount of samples in all uriReports combined.
   */
  private int size;

  /**
   * The duration of all samples combined, in milliseconds.
   */
  private long totalDuration = 0;

  /**
   * The size of all samples combined, in kilobytes.
   */
  private double totalSizeInKB = 0;

  /**
   * The longest duration from all samples, or Long.MIN_VALUE when no samples where processed.
   */
  private long max = Long.MIN_VALUE;

  /**
   * The shortest duration from all samples, or Long.MAX_VALUE when no samples where processed.
   */
  private long min = Long.MAX_VALUE;

  public HttpSample httpSample;
  public static String asStaplerURI(String uri) {
    return uri.replace("http:", "").replaceAll("/", "_");
  }
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  public void addSample(HttpSample pHttpSample) throws SAXException {

    synchronized (httpSampleMap) {

      httpSampleMap.put(pHttpSample.getHttpId(), pHttpSample);
      if(!pHttpSample.isSuccessful()){
        failedhttpSampleMap.put(pHttpSample.getHttpId(), pHttpSample);
        reportStatus=true;
      }
      httpSampleOrdered = null;
    }

    if (!pHttpSample.isSuccessful()) {
      nbError++;
    }
    size++;
    totalDuration += pHttpSample.getDuration();
    totalSizeInKB += pHttpSample.getSizeInKb();
    max = Math.max(pHttpSample.getDuration(), max);
    min = Math.min(pHttpSample.getDuration(), min);
  }

  public boolean getReportStatus(){
    return reportStatus;
  }

  public List<HttpSample> getUriListOrdered() {

    synchronized (httpSampleMap) {
      if (httpSampleOrdered == null) {
        httpSampleOrdered = new ArrayList<HttpSample>(httpSampleMap.values());
      }
      return httpSampleOrdered;
    }
  }

  public  Map<Integer, HttpSample> getFailedhttpSampleMap(){
    return failedhttpSampleMap;
  }

  public int compareTo(JVisualizerReport jmReport) {
    if (this == jmReport) {
      return 0;
    }
    return getReportFileName().compareTo(jmReport.getReportFileName());
  }

  public String getHttpCode() {
    return "";
  }

  public AbstractBuild<?, ?> getBuild() {
    return buildAction.getBuild();
  }

  JVisualizerBuildAction getBuildAction() {
    return buildAction;
  }

  public String getDisplayName() {
    return Messages.Report_DisplayName();
  }

  public HttpSample getDynamic(String token) throws IOException {
    return getHttpSampleMap().get(token);
  }

  public String getReportFileName() {
    return reportFileName;
  }

  public Map<Integer, HttpSample> getHttpSampleMap() {
    return httpSampleMap;
  }

  void setBuildAction(JVisualizerBuildAction buildAction) {
    this.buildAction = buildAction;
  }

  public void setReportFileName(String reportFileName) {
    this.reportFileName = reportFileName;
  }

  public int size() {
    return size;
  }

  public void setLastBuildReport(JVisualizerReport lastBuildReport) {
    Map<Integer, HttpSample> lastBuildUriReportMap = lastBuildReport
            .getHttpSampleMap();
    for (Map.Entry<Integer, HttpSample> item : httpSampleMap.entrySet()) {
      HttpSample lastBuildUri = lastBuildUriReportMap.get(item.getKey());

    }
    this.lastBuildReport = lastBuildReport;
  }

}
