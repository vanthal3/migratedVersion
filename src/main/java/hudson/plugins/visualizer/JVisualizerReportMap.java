package hudson.plugins.visualizer;

import hudson.model.AbstractBuild;
import hudson.model.ModelObject;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.*;

import hudson.model.TaskListener;
import java.io.FilenameFilter;
import org.kohsuke.stapler.StaplerRequest;

/**
 * Root object of a visualizer report.
 */
public class JVisualizerReportMap implements ModelObject {

  /**
   * The {@link JVisualizerBuildAction} that this report belongs to.
   */
  private transient JVisualizerBuildAction buildAction;
  /**
   * {@link JVisualizerReport}s are keyed by
   * {@link JVisualizerReport#reportFileName}
   * 
   * Test names are arbitrary human-readable and URL-safe string that identifies
   * an individual report.
   */
  private Map<String, JVisualizerReport> performanceReportMap = new LinkedHashMap<String, JVisualizerReport>();
  private static final String PERFORMANCE_REPORTS_DIRECTORY = "visualizer-reports";
  private static final String PLUGIN_NAME = "visualizer";
  private static final String TRENDREPORT_LINK = "trendReport";

  private static AbstractBuild<?, ?> currentBuild = null;

  /**
   * Parses the reports and build a {@link JVisualizerReportMap}.
   * 
   * @throws IOException
   *           If a report fails to parse.
   */
  JVisualizerReportMap(final JVisualizerBuildAction buildaction,
                       TaskListener listener) throws IOException {
    buildAction = buildaction;
    parseReports(getBuild(), listener, new PerformanceReportCollector() {

      public void addAll(Collection<JVisualizerReport> reports) {
        for (JVisualizerReport r : reports) {
          r.setBuildAction(buildAction);
          performanceReportMap.put(r.getReportFileName(), r);
        }
      }
    }, null);
  }

  private void addAll(Collection<JVisualizerReport> reports) {
    for (JVisualizerReport r : reports) {
      r.setBuildAction(buildAction);
      performanceReportMap.put(r.getReportFileName(), r);
    }
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

  public List<JVisualizerReport> getPerformanceListOrdered() {
    List<JVisualizerReport> listPerformance = new ArrayList<JVisualizerReport>(
        getPerformanceReportMap().values());
    Collections.sort(listPerformance);
    return listPerformance;
  }

  public Map<String, JVisualizerReport> getPerformanceReportMap() {
    return performanceReportMap;
  }

  public JVisualizerReport getPerformanceReport(String performanceReportName) {
    return performanceReportMap.get(performanceReportName);
  }


  public String getUrlName() {
    return PLUGIN_NAME;
  }

  void setBuildAction(JVisualizerBuildAction buildAction) {
    buildAction = buildAction;
  }

  public void setPerformanceReportMap(
      Map<String, JVisualizerReport> performanceReportMap) {
    performanceReportMap = performanceReportMap;
  }

  public static String getPerformanceReportFileRelativePath(
      String parserDisplayName, String reportFileName) {
    return getRelativePath(parserDisplayName, reportFileName);
  }

  public static String getPerformanceReportDirRelativePath() {
    return getRelativePath();
  }

  private static String getRelativePath(String... suffixes) {
    StringBuilder sb = new StringBuilder(100);
    sb.append(PERFORMANCE_REPORTS_DIRECTORY);
    for (String suffix : suffixes) {
      sb.append(File.separator).append(suffix);
    }
    return sb.toString();
  }


  public boolean isFailed(String performanceReportName) {
    return getPerformanceReport(performanceReportName) == null;
  }


  private void parseReports(AbstractBuild<?, ?> build, TaskListener listener,
      PerformanceReportCollector collector, final String filename)
      throws IOException {
    File repo = new File(build.getRootDir(),
        JVisualizerReportMap.getPerformanceReportDirRelativePath());

    // files directly under the directory are for JMeter, for compatibility
    // reasons.
    File[] files = repo.listFiles(new FileFilter() {

      public boolean accept(File f) {
        return !f.isDirectory() && !f.getName().endsWith(".serialized");
      }
    });
    // this may fail, if the build itself failed, we need to recover gracefully
    if (files != null) {
      addAll(new JtlFileParser("").parse(build, Arrays.asList(files), listener));
    }

    // otherwise subdirectory name designates the parser ID.
    File[] dirs = repo.listFiles(new FileFilter() {

      public boolean accept(File f) {
        return f.isDirectory();
      }
    });
    // this may fail, if the build itself failed, we need to recover gracefully
    if (dirs != null) {
      for (File dir : dirs) {
        JVisualizerParser p = buildAction.getParserByDisplayName(dir
            .getName());
        if (p != null) {
          File[] listFiles = dir.listFiles(new FilenameFilter() {

            public boolean accept(File dir, String name) {
              if (filename == null && !name.endsWith(".serialized")) {
                return true;
              }
              if (name.equals(filename)) {
                return true;
              }
              return false;
            }
          });
          try {
            collector.addAll(p.parse(build, Arrays.asList(listFiles), listener));
          } catch (IOException ex) {
            listener.getLogger().println("Unable to process directory '"+ dir+"'.");
            ex.printStackTrace(listener.getLogger());
          }
        }
      }
    }

    addPreviousBuildReports();
  }

  private void addPreviousBuildReports() {

    // Avoid parsing all builds.
    if (JVisualizerReportMap.currentBuild == null) {
      JVisualizerReportMap.currentBuild = getBuild();
    } else {
      if (JVisualizerReportMap.currentBuild != getBuild()) {
        JVisualizerReportMap.currentBuild = null;
        return;
      }
    }

    AbstractBuild<?, ?> previousBuild = getBuild().getPreviousCompletedBuild();
    if (previousBuild == null) {
      return;
    }

    JVisualizerBuildAction previousPerformanceAction = previousBuild
        .getAction(JVisualizerBuildAction.class);
    if (previousPerformanceAction == null) {
      return;
    }

    JVisualizerReportMap previousJVisualizerReportMap = previousPerformanceAction
        .getJVisualizerReportMap();
    if (previousJVisualizerReportMap == null) {
      return;
    }

    for (Map.Entry<String, JVisualizerReport> item : getPerformanceReportMap()
        .entrySet()) {
      JVisualizerReport lastReport = previousJVisualizerReportMap
          .getPerformanceReportMap().get(item.getKey());
      if (lastReport != null) {
        item.getValue().setLastBuildReport(lastReport);
      }
    }
  }

  private interface PerformanceReportCollector {

    public void addAll(Collection<JVisualizerReport> parse);
  }

  private String getTrendReportFilename(final StaplerRequest request) {
    JVisualizerPosition jVisualizerPosition = new JVisualizerPosition();
    request.bindParameters(jVisualizerPosition);
    return jVisualizerPosition.getPerformanceReportPosition();
  }
}
