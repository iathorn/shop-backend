package shop;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    

    
}