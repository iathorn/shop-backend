package shop;

import java.awt.Checkbox;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import shop.models.account.Account;
import shop.models.checkout.Checkout;

@Service
public class ShopService {
    @Autowired
    ShopDAO shopDAO;

    public AlertItem getAlertItemById(Long id) {
        return shopDAO.getAlertItemById(id);
    }

    public boolean removeAlertItem(Long id){
        return shopDAO.removeAlertItem(id);
    }

    public void updateAlertItem(AlertItem alertItem){
        shopDAO.updateAlertItem(alertItem);
    }

    public List<AlertItem> getAlertItemList(Long page){
        return shopDAO.getAlertItemList(page);
    }

    public long getAlertItemSize(){
        return shopDAO.getAlertItemSize();
    }

    public long writeAlertItem(AlertItem alertItem) {
        return shopDAO.writeAlertItem(alertItem);
    }

    // item

    public Item getItemById(long id){
        return shopDAO.getItemById(id);
    }

    public long writeItem(Item item){
        return shopDAO.writeItem(item);
    }

    public List<Item> getItemList() {
        return shopDAO.getItemList();
    }

    public boolean deleteById(long id) {
        return shopDAO.deleteItemById(id);
    }

    public void updateById(Item item) {
        shopDAO.updateById(item);
    }

    // Auth (member)

    public long signup(Account account) {
        return shopDAO.signup(account);
    }

    public Account getUserById(long id) {
        return shopDAO.getUserById(id);
    }

    public Boolean getUserByUserID(String userID) {
        return shopDAO.getUserByUserID(userID);
    }

    public String getHashedPassword(String userID) {
        return shopDAO.getHashedPassword(userID);
    }

    public Account getUserInfo(String userID) {
        return shopDAO.getUserInfo(userID);
    }

    // Checkout

    public long checkout(Checkout checkout) {
        return shopDAO.checkout(checkout);
    }

    public Checkout getCheckoutById(long id) {
        return shopDAO.getCheckoutById(id);
    }
    
}