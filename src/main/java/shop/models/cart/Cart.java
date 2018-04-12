package shop.models.cart;

public class Cart {
    private int id;
    private String title;
    private int amount;
    private String thumbnailImage;
    private int totalPrice;


    /**
     * @param id the id to set
     */
    public void setId(int id) {
        this.id = id;
    }
    /**
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * @param title the title to set
     */
    public void setTitle(String title) {
        this.title = title;
    }
    /**
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * @param amount the amount to set
     */
    public void setAmount(int amount) {
        this.amount = amount;
    }
    /**
     * @return the amount
     */
    public int getAmount() {
        return amount;
    }

    /**
     * @param thumbnailImage the thumbnailImage to set
     */
    public void setThumbnailImage(String thumbnailImage) {
        this.thumbnailImage = thumbnailImage;
    }
    /**
     * @return the thumbnailImage
     */
    public String getThumbnailImage() {
        return thumbnailImage;
    }

    /**
     * @param totalPrice the totalPrice to set
     */
    public void setTotalPrice(int totalPrice) {
        this.totalPrice = totalPrice;
    }
    /**
     * @return the totalPrice
     */
    public int getTotalPrice() {
        return totalPrice;
    }
    
}