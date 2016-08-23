package hudson.plugins.visualizer;

import hudson.DescriptorExtensionList;
import hudson.model.Descriptor;
import hudson.model.Hudson;

/**
 * @author Kohsuke Kawaguchi
 */
public abstract class JVisualizerParserDescriptor extends
    Descriptor<JVisualizerParser> {

  /**
   * Internal unique ID that distinguishes a parser.
   */
  public final String getId() {
    return getClass().getName();
  }

  /**
   * Returns all the registered {@link JVisualizerParserDescriptor}s.
   */
  public static DescriptorExtensionList<JVisualizerParser, JVisualizerParserDescriptor> all() {
    return Hudson.getInstance().<JVisualizerParser, JVisualizerParserDescriptor>getDescriptorList(JVisualizerParser.class);
  }

  public static JVisualizerParserDescriptor getById(String id) {
    for (JVisualizerParserDescriptor d : all())
      if (d.getId().equals(id))
        return d;
    return null;
  }
}
