package shop.models.account;

public class LoginError {
    private int errorCode;
    private String errorLog;

    /**
     * @return the errorCode
     */
    public int getErrorCode() {
        return errorCode;
    }
    /**
     * @param errorCode the errorCode to set
     */
    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    /**
     * @return the errorLog
     */
    public String getErrorLog() {
        return errorLog;
    }
    /**
     * @param errorLog the errorLog to set
     */
    public void setErrorLog(String errorLog) {
        this.errorLog = errorLog;
    }
}