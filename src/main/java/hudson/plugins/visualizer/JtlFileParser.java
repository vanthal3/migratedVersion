package hudson.plugins.visualizer;

import hudson.Extension;
import java.util.Random;

import java.io.*;
import java.util.Date;
import javax.xml.parsers.SAXParserFactory;

import org.kohsuke.stapler.DataBoundConstructor;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Parser for JMeter.
 */
public class JtlFileParser extends AbstractParser {

  private  Integer sampleID;


  @Extension
  public static class DescriptorImpl extends JVisualizerParserDescriptor {
    @Override
    public String getDisplayName() {
      return "JTL File";
    }
  }

  @DataBoundConstructor
  public JtlFileParser(String glob) {
    super(glob);
  }


  @Override
  public String getDefaultGlobPattern() {
    return "**/*.jtl";
  }

  JVisualizerReport parse(File reportFile) throws Exception
  {
    return parseXml(reportFile);

  }

  /**
   * A delegate for {@link #parse(File)} that can process XML data.
   */
  JVisualizerReport parseXml(File reportFile) throws Exception
  {
    final SAXParserFactory factory = SAXParserFactory.newInstance();
    factory.setValidating(false);
    factory.setNamespaceAware(false);

    final JVisualizerReport report = new JVisualizerReport();


    report.setReportFileName(reportFile.getName());

    factory.newSAXParser().parse(reportFile, new DefaultHandler() {
      public HttpSample sample;
      String tempValue;
      StringBuilder sb = new StringBuilder();

      boolean phttp=false;
      boolean pName=false;
      boolean pFailure=false;
      boolean pFailureMessage=false;
      boolean pError=false;
      boolean pAssertion=false;
      Integer idCounter;

      /**
       * XML parsed using http://jakarta.apache.org/jmeter/usermanual/listeners.html
       */
      @Override
      public void startElement(String uri, String localName, String elementName, Attributes attributes) throws SAXException {

        if("httpSample".equalsIgnoreCase(elementName) || "sample".equalsIgnoreCase(elementName)){
          phttp=true;
          sample= new HttpSample(createSampleID());
          final String dateValue;
          if (attributes.getValue("ts") != null) {
            dateValue = attributes.getValue("ts");
          } else {
            dateValue = attributes.getValue("timeStamp");
          }
          sample.setDate( new Date(Long.valueOf(dateValue)) );

          final String durationValue;
          if (attributes.getValue("t") != null) {
            durationValue = attributes.getValue("t");
          } else {
            durationValue = attributes.getValue("time");
          }
          sample.setDuration(Long.valueOf(durationValue));
       final String successfulValue;
          if (attributes.getValue("s") != null) {
            successfulValue = attributes.getValue("s");
          } else {
            successfulValue = attributes.getValue("success");
          }
          sample.setSuccessful(Boolean.parseBoolean(successfulValue));

          final String uriValue;
          if (attributes.getValue("lb") != null) {
            uriValue = attributes.getValue("lb");
          } else {
            uriValue = attributes.getValue("label");
          }
          sample.setUri(uriValue);

          final String threadname;
          if (attributes.getValue("tn") != null) {
            threadname = attributes.getValue("tn");
          } else {
            threadname = attributes.getValue("threadname");
          }
          sample.setThreadName(threadname);

          final String errorCount;
          if (attributes.getValue("ec") != null) {
            errorCount = attributes.getValue("ec");
          } else {
            errorCount = attributes.getValue("errorCount");
          }
          sample.setErrorCount(errorCount);

          final String responseMessage;
          if (attributes.getValue("rm") != null) {
            responseMessage = attributes.getValue("rm");
          } else {
            responseMessage = attributes.getValue("responseMessage");
          }
          sample.setResponseMessage(responseMessage);

          final String httpCodeValue;
          if (attributes.getValue("rc") != null && attributes.getValue("rc").length() <= 3) {
            httpCodeValue = attributes.getValue("rc");
          } else {
            httpCodeValue = "0";
          }
          sample.setHttpCode(httpCodeValue);

          final String sizeInKbValue;
          if (attributes.getValue("by") != null) {
            sizeInKbValue = attributes.getValue("by");
          } else {
            sizeInKbValue = "0";
          }
          sample.setSizeInKb(Double.valueOf(sizeInKbValue) / 1024d);
        } else if ("AssertionResult".equalsIgnoreCase(elementName)) {
          pAssertion = true;

        }else if ("name".equalsIgnoreCase(elementName)) {
          pName = true;

        }else if ("failure".equalsIgnoreCase(elementName)) {
          pFailure = true;

        }else if ("error".equalsIgnoreCase(elementName)) {
          pError = true;

        }else if("failureMessage".equalsIgnoreCase(elementName)){
          pFailureMessage=true;

        }

      }

      @Override
      public void characters(char[] ch, int start, int length) throws SAXException {
        tempValue=new String(ch, start, length);
        if(pFailureMessage){
          sb.append(ch, start, length);
        }

      }


      @Override
      public void endElement(String uri, String localName, String element) {
        if(pAssertion) {
          idCounter=sample.addAssertion();
          pAssertion=false;

        }
        if(pName){
          sample.getARObject(idCounter).setName(tempValue);
          pName=false;
        }
        if(pFailure){
          sample.getARObject(idCounter).setFailure(tempValue);
          pFailure=false;
        }
        if(pError){
          sample.getARObject(idCounter).setError(tempValue);
          pError=false;
        }

        if(pFailureMessage) {
          sample.getARObject(idCounter).setFailureMessage(sb.toString());
          sb = null;
          sb = new StringBuilder();
          pFailureMessage = false;
        }

        if ("httpSample".equalsIgnoreCase(element) || "sample".equalsIgnoreCase(element)) {

          try {
            report.addSample(sample);
          } catch (SAXException e) {
            e.printStackTrace();
          }

        }
      }
    });

    return report;
  }
  
  public Integer createSampleID(){

    Random r = new Random();
    Integer id = r.nextInt(9999);
    sampleID=id;


    return sampleID;
  }





}
