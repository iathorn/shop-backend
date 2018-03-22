package shop;


import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

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
}