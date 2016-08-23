package hudson.plugins.visualizer;


import java.io.Serializable;


/**
 * An HttpSample may send AssertionResults which holds information
 * about the success of the assertions.
 *
 * This POJO belongs under {@link HttpSample}.
 *
 */


public class AssertionResult implements Serializable {
    public String Name;
    public String Failure;
    public String Error;
    public String FailureMessage;
    private int id;


    public AssertionResult(int myid){
        id = myid;
    }

    public void setName(String name){
        Name=name;
    }


    public String getName(){
        return Name;
    }

    public void setFailure(String failure){
        Failure=failure;
    }

    public String isFailure(){
        return Failure;
    }

    public void setError(String error){
        Error=error;
    }

    public String isError(){
        return Error;
    }

    public int getId(){return id;}

    public void setFailureMessage(String fm){
        FailureMessage= fm;
    }

    public String getFailureMessage(){
        if (FailureMessage==null){
            String s=new String();
            s="No Failure Msg";
            return s;
        }
        return FailureMessage;
    }

}
