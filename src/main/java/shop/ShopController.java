package shop;

import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.transform.impl.AddInitTransformer;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@RestController
public class ShopController {

    @Autowired
    private Environment env;

    

    @RequestMapping(value="/")
    public String index() {
        return "index";
    }

    // Auth
    @RequestMapping(value="/api/auth/login/admin", method=RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<?> adminLogin(@RequestBody Map<String, String> password, HttpSession session) {
        if (env.getProperty("admin.password").equals(password.get("password"))) {
            session.setAttribute("adminLogged", true);
            return new ResponseEntity<>(true, HttpStatus.OK);
        } else {
            session.setAttribute("adminLogged", false);
            return new ResponseEntity<>(false, HttpStatus.UNAUTHORIZED);
        }
    }

    @RequestMapping(value="/api/auth/check/admin", method=RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<?> checkAdminLogin(HttpSession session) {
        if(session.getAttribute("adminLogged") != null) {
            if((Boolean) session.getAttribute("adminLogged") != null && session.getAttribute("adminLogged") != null) {
                return new ResponseEntity<>(true, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(false, HttpStatus.OK);
            }
        } else {
            return new ResponseEntity<>(false, HttpStatus.OK);
        }
        
    }

    @RequestMapping(value="api/auth/logout/admin", method=RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<?> adminLogout(HttpSession session) {
        session.setAttribute("adminLogged", null);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // Editor / Post
    // @RequestMapping(value="/api/post", method=RequestMethod.POST)
    // @ResponseBody
    // public ResponseEntity<?> writePost(@RequestBody Map<String, String> title, Map<String, String> body, Map<String, String> tags) {
        
    // }


}