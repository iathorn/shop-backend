package shop;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.transform.impl.AddInitTransformer;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import shop.errorItem.*;

import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@RestController
public class ShopController {

    @Autowired
    private Environment env;

    @Autowired
    ShopService shopService;

    

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
    @RequestMapping(value="/api/post/alert/{id}", method=RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<?> getAlertItemById(@PathVariable Long id){
        try {
            AlertItem alertItem = shopService.getAlertItemById(id);
            if(alertItem == null) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(alertItem, HttpStatus.OK);
        } catch(Exception e){
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        }
    }

    @RequestMapping(value="/api/post/alert/{id}", method=RequestMethod.DELETE)
    @ResponseBody
    public ResponseEntity<?> removeAlertItem(@PathVariable Long id){
        try {
            if(shopService.removeAlertItem(id)){
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            } 
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch(Exception e){
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value="/api/post/alert/{id}", method=RequestMethod.PATCH)
    @ResponseBody
    public ResponseEntity<?> updateAlertItem(@PathVariable Long id, 
        @RequestBody Map<String, String> body){
            
        try {
            AlertItem alertItem = shopService.getAlertItemById(id);
            if(alertItem == null) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            alertItem.setTitle(body.get("title"));
            alertItem.setBody(body.get("body"));
            shopService.updateAlertItem(alertItem);
            
            return new ResponseEntity<>(alertItem, HttpStatus.OK);
        } catch(Exception e){
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @RequestMapping(value="/api/post/alert", method=RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<?> getAlertItemList(@RequestParam(value="page", defaultValue="1", required=false) Long page) {
        List<AlertItem> alertItemList;
        HttpHeaders headers = new HttpHeaders();

        if(page < 1){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        try {
            alertItemList = shopService.getAlertItemList(page);
            headers.add("Last-Page", (int)Math.ceil(shopService.getAlertItemSize() / 10) + 1 + "");
            return new ResponseEntity<>(alertItemList, headers, HttpStatus.OK);
        } catch(Exception e){
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }



    @RequestMapping(value="/api/post/alert", method=RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<?> writeAlertItem(AlertItem alertItem,
     @RequestBody Map<String, String> body) {

        if(!(body.get("title") instanceof String) || body.get("title").equals("") || 
            body.get("title") == null) {
            ErrorItem errorItem = new ErrorItem();
            errorItem.setContent("공지 제목을 입력하여 주세요.");
            return new ResponseEntity<>(errorItem, HttpStatus.UNPROCESSABLE_ENTITY);
        }
        if(!(body.get("body") instanceof String) || body.get("body").equals("") || 
        body.get("body") == null) {
            ErrorItem errorItem = new ErrorItem();
            errorItem.setContent("공지 내용을 입력하여 주세요.");
            return new ResponseEntity<>(errorItem, HttpStatus.UNPROCESSABLE_ENTITY);
        }

        String publishedDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());

        try {
            alertItem.setTitle(body.get("title"));
            alertItem.setBody(body.get("body"));
            alertItem.setPublishedDate(publishedDate);
            alertItem = shopService.getAlertItemById(shopService.writeAlertItem(alertItem));
            return new ResponseEntity<>(alertItem, HttpStatus.OK);
        } catch(Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        
    }

    


}