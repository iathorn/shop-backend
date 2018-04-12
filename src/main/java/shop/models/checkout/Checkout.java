package shop.models.checkout;

public class Checkout {
    private long id;
    private String imp_uid;
    private String merchant_uid;
    private String paid_amount;
    private String apply_num;
    private String buyer_email;
    private String buyer_name;
    private String buyer_tel;
    private String buyer_addr;
    private String buyer_postcode;
    private String buyer_request_message;
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
     * @return the imp_uid
     */
    public String getImp_uid() {
        return imp_uid;
    }
    /**
     * @param imp_uid the imp_uid to set
     */
    public void setImp_uid(String imp_uid) {
        this.imp_uid = imp_uid;
    }

    /**
     * @return the merchant_uid
     */
    public String getMerchant_uid() {
        return merchant_uid;
    }
    /**
     * @param merchant_uid the merchant_uid to set
     */
    public void setMerchant_uid(String merchant_uid) {
        this.merchant_uid = merchant_uid;
    }

    /**
     * @return the paid_amount
     */
    public String getPaid_amount() {
        return paid_amount;
    }
    /**
     * @param paid_amount the paid_amount to set
     */
    public void setPaid_amount(String paid_amount) {
        this.paid_amount = paid_amount;
    }

    /**
     * @return the apply_num
     */
    public String getApply_num() {
        return apply_num;
    }
    /**
     * @param apply_num the apply_num to set
     */
    public void setApply_num(String apply_num) {
        this.apply_num = apply_num;
    }

    /**
     * @param buyer_email the buyer_email to set
     */
    public void setBuyer_email(String buyer_email) {
        this.buyer_email = buyer_email;
    }
    /**
     * @return the buyer_email
     */
    public String getBuyer_email() {
        return buyer_email;
    }

    /**
     * @param buyer_name the buyer_name to set
     */
    public void setBuyer_name(String buyer_name) {
        this.buyer_name = buyer_name;
    }
    /**
     * @return the buyer_name
     */
    public String getBuyer_name() {
        return buyer_name;
    }

    /**
     * @param buyer_tel the buyer_tel to set
     */
    public void setBuyer_tel(String buyer_tel) {
        this.buyer_tel = buyer_tel;
    }
    /**
     * @return the buyer_tel
     */
    public String getBuyer_tel() {
        return buyer_tel;
    }

    /**
     * @param buyer_addr the buyer_addr to set
     */
    public void setBuyer_addr(String buyer_addr) {
        this.buyer_addr = buyer_addr;
    }
    /**
     * @return the buyer_addr
     */
    public String getBuyer_addr() {
        return buyer_addr;
    }
    /**
     * @param buyer_postcode the buyer_postcode to set
     */
    public void setBuyer_postcode(String buyer_postcode) {
        this.buyer_postcode = buyer_postcode;
    }
    /**
     * @return the buyer_postcode
     */
    public String getBuyer_postcode() {
        return buyer_postcode;
    }

    /**
     * @param buyer_request_message the buyer_request_message to set
     */
    public void setBuyer_request_message(String buyer_request_message) {
        this.buyer_request_message = buyer_request_message;
    }
    /**
     * @return the buyer_request_message
     */
    public String getBuyer_request_message() {
        return buyer_request_message;
    }

    /**
     * @param createdAt the createdAt to set
     */
    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
    /**
     * @return the createdAt
     */
    public String getCreatedAt() {
        return createdAt;
    }
    
}