package net.sourceforge.tess4j.util;

public class LoggUtil extends Exception {

    /**
     * Since this class is serializable, the UID is required for runtime and persistence.
     */
    private static final long serialVersionUID = -1722303030127419704L;

    /**
     * This method will get the class name of the throwing class and return it as a string, overriding the default
     * toString method.
     */
    @Override
    public String toString() {

        String className = this.getClass().getName();
        StackTraceElement[] trace = this.getStackTrace();
        className = trace[0].getClassName();

        return className;
    }
}

