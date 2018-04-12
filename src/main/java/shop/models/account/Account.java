package shop.models.account;

public class Account {
    private long id;
    private String userID;
    private String userName;
    private String userEmail;
    private String userPassword;
    private String userPostAddress;
    private String userPostCode;
    private String userDetailAddress;
    private String createdAt;



    /**
     * @param id the id to set
     */
    public void setId(long id) {
        this.id = id;
    }
    /**
     * @return the id
     */
    public long getId() {
        return id;
    }
    


    /**
     * @return the userID
     */
    public String getUserID() {
        return userID;
    }
    /**
     * @param userID the userID to set
     */
    public void setUserID(String userID) {
        this.userID = userID;
    }

    /**
     * @return the userName
     */
    public String getUserName() {
        return userName;
    }
    /**
     * @param userName the userName to set
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }

    /**
     * @return the userEmail
     */
    public String getUserEmail() {
        return userEmail;
    }

    /**
     * @param userEmail the userEmail to set
     */
    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    /**
     * @return the userPassword
     */
    public String getUserPassword() {
        return userPassword;
    }

    /**
     * @param userPassword the userPassword to set
     */
    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }


    /**
     * @param userPostAddress the userPostAddress to set
     */
    public void setUserPostAddress(String userPostAddress) {
        this.userPostAddress = userPostAddress;
    }
    /**
     * @return the userPostAddress
     */
    public String getUserPostAddress() {
        return userPostAddress;
    }

    /**
     * @param userPostCode the userPostCode to set
     */
    public void setUserPostCode(String userPostCode) {
        this.userPostCode = userPostCode;
    }
    /**
     * @return the userPostCode
     */
    public String getUserPostCode() {
        return userPostCode;
    }

    /**
     * @param userDetailAddress the userDetailAddress to set
     */
    public void setUserDetailAddress(String userDetailAddress) {
        this.userDetailAddress = userDetailAddress;
    }
    /**
     * @return the userDetailAddress
     */
    public String getUserDetailAddress() {
        return userDetailAddress;
    }

    /**
     * @return the createdAt
     */
    public String getCreatedAt() {
        return createdAt;
    }

    /**
     * @param createdAt the createdAt to set
     */
    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
    
    
    
}