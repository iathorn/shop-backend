package shop;


public class Admin {
    String adminPassword;


    public Admin(String adminPassword){
        this.adminPassword = adminPassword;
    }

    /**
     * @param adminPassword the adminPassword to set
     */
    public void setAdminPassword(String adminPassword) {
        this.adminPassword = adminPassword;
    }

    /**
     * @return the adminPassword
     */
    public String getAdminPassword() {
        return adminPassword;
    }
}