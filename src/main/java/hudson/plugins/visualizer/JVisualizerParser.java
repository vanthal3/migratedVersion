package hudson.plugins.visualizer;

import hudson.ExtensionList;
import hudson.ExtensionPoint;
import hudson.model.AbstractBuild;
import hudson.model.Describable;
import hudson.model.Hudson;
import hudson.model.TaskListener;
import org.kohsuke.stapler.DataBoundConstructor;

import java.io.*;
import java.util.Collection;

/**
 * Parses visualizer result files into {@link JVisualizerReport}s. This object
 * is persisted with {@link JVisualizerPublisher} into the project
 * configuration.
 * 
 * @author Kohsuke Kawaguchi
 */
public abstract class JVisualizerParser implements
    Describable<JVisualizerParser>, ExtensionPoint {
  /**
   * GLOB patterns that specify the visualizer report.
   */
  public final String glob;

  @DataBoundConstructor
  protected JVisualizerParser(String glob) {this.glob = (glob == null || glob.length() == 0) ? getDefaultGlobPattern()
        : glob;
  }

  public JVisualizerParserDescriptor getDescriptor() {
    return (JVisualizerParserDescriptor) Hudson.getInstance()
        .getDescriptorOrDie(getClass());
  }

  /**
   * Parses the specified reports into {@link JVisualizerReport}s.
   */
  public abstract Collection<JVisualizerReport> parse(
      AbstractBuild<?, ?> build, Collection<File> reports, TaskListener listener)
      throws IOException;

  public abstract String getDefaultGlobPattern();

  /**
   * All registered implementations.
   */
  public static ExtensionList<JVisualizerParser> all() {
    return Hudson.getInstance().getExtensionList(JVisualizerParser.class);
  }

  public String getReportName() {
    return this.getClass().getName().replaceAll("^.*\\.(\\w+)Parser.*$", "$1");
  }

}
