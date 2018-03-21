import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class ShopDAO {
    @Autowired
    JdbcTemplate jdbcTemplate;

    // public void writePost()
}