package shop;


import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import shop.models.account.Account;
import shop.models.checkout.Checkout;

@Repository
public class ShopDAO {
    @Autowired
    JdbcTemplate jdbcTemplate;

    public AlertItem getAlertItemById(long id) {
        String sql = "SELECT * FROM alert WHERE id = ?";
        AlertItem alertItem = jdbcTemplate.queryForObject(sql, new AlertItemMapper(), id);
        return alertItem;
    }

    public boolean removeAlertItem(long id){
        String sql = "DELETE FROM alert WHERE id = ?";
        int update = jdbcTemplate.update(sql, id);
        return update == 1;
    }

    public void updateAlertItem(AlertItem alertItem) {
        String sql = "UPDATE alert SET title = ?, body = ? WHERE id = ?";
        jdbcTemplate.update(sql, new Object[] {alertItem.getTitle(), alertItem.getBody(), alertItem.getId()});

    }

    public List<AlertItem> getAlertItemList(Long page) {
        // String sql = "SELECT * FROM alert ORDER BY id DESC";
        String sql = "SELECT * FROM alert ORDER BY id DESC LIMIT 10 OFFSET " + (page - 1) * 10;
        return jdbcTemplate.query(sql, new AlertItemMapper());
    }

    public long getAlertItemSize() {
        String sql = "SELECT count(*) FROM alert";
        long cnt = jdbcTemplate.queryForObject(sql, Long.class);
        return cnt;
    }

    public long writeAlertItem(AlertItem alertItem) {
        String sql = "INSERT INTO alert (title, body, publishedDate)" + " VALUES(?, ?, ?)";
        int update = jdbcTemplate.update(sql, new Object[]{alertItem.getTitle(), alertItem.getBody(), alertItem.getPublishedDate()});
        long cnt = 0;
        if(update == 1) {
            cnt = jdbcTemplate.queryForObject("SELECT max(id) FROM alert", Long.class);
        }

        return cnt;
    }


    // item

    public long writeItem(Item item){
        String sql = "INSERT INTO item (title, body, price, imageNames, publishedDate)" + " VALUES(?, ?, ? ,? ,?)";
        int update = jdbcTemplate.update(sql, new Object[]{item.getTitle(), item.getBody(), item.getPrice(), item.getImageNames(), item.getPublishedDate()});
        long cnt = 0;
        if(update == 1){
            cnt = jdbcTemplate.queryForObject("SELECT max(id) FROM item", Long.class);
        }

        return cnt;
    }

    public Item getItemById(long id) {
        String sql = "SELECT * FROM item WHERE id = ?";
        Item item = jdbcTemplate.queryForObject(sql, new ItemMapper(), id);
        return item;
    }

    public List<Item> getItemList() {
        // String sql = "SELECT * FROM alert ORDER BY id DESC";
        String sql = "SELECT * FROM item ORDER BY id DESC";
        return jdbcTemplate.query(sql, new ItemMapper());
    }

    public boolean deleteItemById(long id) {
        String sql = "DELETE FROM item WHERE id = ?";
        int update = jdbcTemplate.update(sql, id);
        return update == 1;
    }

    public void updateById(Item item){
        String sql = "UPDATE item SET title = ?, body = ?, price = ?, imageNames = ? WHERE id = ?";
        jdbcTemplate.update(sql, new Object[] {item.getTitle(), item.getBody(), item.getPrice(), item.getImageNames(), item.getId()});
    }


    // Auth (Member)

    public long signup(Account account) {
        String sql = "INSERT INTO member (userID, userName, userEmail, userPassword, userPostAddress, userPostCode, userDetailAddress, createdAt) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        int update = jdbcTemplate.update(sql, new Object[]{account.getUserID(), account.getUserName(), account.getUserEmail(), account.getUserPassword(), account.getUserPostAddress(), account.getUserPostCode(), account.getUserDetailAddress(), account.getCreatedAt()});
        long cnt = 0;
        if(update == 1) {
            cnt = jdbcTemplate.queryForObject("SELECT max(id) FROM member", Long.class);
        }
        return cnt;
    }

    public Account getUserById(long id){
        String sql = "SELECT * FROM member WHERE id = ?";
        Account account = jdbcTemplate.queryForObject(sql, new MemberMapper(), id);
        return account;
    }

    public Boolean getUserByUserID(String userID) {
        long cnt = 0;
        cnt = jdbcTemplate.queryForObject("SELECT count(*) FROM member WHERE userID = ?", Long.class, userID);
        if(cnt == 1) {
            return true;
        } 
        return false;
    }

    public String getHashedPassword(String userID) {
        String sql = "SELECT * FROM member WHERE userID = ?";
        Account account = jdbcTemplate.queryForObject(sql, new MemberMapper(), userID);
        return account.getUserPassword();

    }

    public Account getUserInfo(String userID) {
        String sql = "SELECT * FROM member WHERE userID = ?";
        Account account = jdbcTemplate.queryForObject(sql, new MemberMapper(), userID);
        return account;
    }

    // Checkout

    public long checkout(Checkout checkout) {
        String sql = "INSERT INTO checkout (imp_uid, merchant_uid, paid_amount, apply_num, buyer_email, buyer_name, buyer_tel, buyer_addr, buyer_postcode, buyer_request_message, createdAt) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        int update = jdbcTemplate.update(sql, new Object[]{checkout.getImp_uid(), checkout.getMerchant_uid(), checkout.getPaid_amount(), checkout.getApply_num(), checkout.getBuyer_email(), checkout.getBuyer_name(), checkout.getBuyer_tel(), checkout.getBuyer_addr(), checkout.getBuyer_postcode(), checkout.getBuyer_request_message(), checkout.getCreatedAt()});
        long cnt = 0;
        if(update == 1) {
            cnt = jdbcTemplate.queryForObject("SELECT max(id) FROM checkout", Long.class);
        }
        return cnt;
    }

    public Checkout getCheckoutById(long id) {
        String sql = "SELECT * FROM checkout WHERE id = ?";
        Checkout checkout = jdbcTemplate.queryForObject(sql, new CheckoutMapper(), id);
        return checkout;
    }

    // Mappers

    protected static final class AlertItemMapper implements RowMapper<AlertItem> {
        public AlertItem mapRow(ResultSet rs, int rowNum)
                throws SQLException {
            
            AlertItem alertItem = new AlertItem();
            alertItem.setId(rs.getLong("id"));
            alertItem.setTitle(rs.getString("title"));
            alertItem.setBody(rs.getString("body"));
            alertItem.setPublishedDate(rs.getString("publishedDate"));
            return alertItem;
        }
    }

    protected static final class ItemMapper implements RowMapper<Item> {
        public Item mapRow(ResultSet rs, int rowNum)
                throws SQLException {
            
            Item item = new Item();
            item.setId(rs.getLong("id"));
            item.setTitle(rs.getString("title"));
            item.setBody(rs.getString("body"));
            item.setPrice(rs.getInt("price"));
            item.setImageNames(rs.getString("imageNames"));
            item.setPublishedDate(rs.getString("publishedDate"));
            return item;
        }
    }

    protected static final class MemberMapper implements RowMapper<Account> {
        public Account mapRow(ResultSet rs, int rowNum)
            throws SQLException {
                Account account = new Account();
                account.setUserID(rs.getString("userID"));
                account.setUserName(rs.getString("userName"));
                account.setUserEmail(rs.getString("userEmail"));
                account.setUserPassword(rs.getString("userPassword"));
                account.setUserPostAddress(rs.getString("userPostAddress"));
                account.setUserPostCode(rs.getString("userPostCode"));
                account.setUserDetailAddress(rs.getString("userDetailAddress"));
                account.setCreatedAt(rs.getString("createdAt"));
                return account;
            }
    }

    protected static final class CheckoutMapper implements RowMapper<Checkout> {
        public Checkout mapRow(ResultSet rs, int rowNum) throws SQLException {
            Checkout checkout = new Checkout();
            checkout.setImp_uid(rs.getString("imp_uid"));
            checkout.setMerchant_uid(rs.getString("merchant_uid"));
            checkout.setPaid_amount(rs.getString("paid_amount"));
            checkout.setApply_num(rs.getString("apply_num"));
            checkout.setBuyer_email(rs.getString("buyer_email"));
            checkout.setBuyer_name(rs.getString("buyer_name"));
            checkout.setBuyer_tel(rs.getString("buyer_tel"));
            checkout.setBuyer_addr(rs.getString("buyer_addr"));
            checkout.setBuyer_postcode(rs.getString("buyer_postcode"));
            checkout.setBuyer_request_message(rs.getString("buyer_request_message"));
            checkout.setCreatedAt(rs.getString("createdAt"));
            return checkout;
        }
    }
}