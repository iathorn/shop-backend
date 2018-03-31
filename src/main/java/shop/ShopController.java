package shop;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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
import org.springframework.web.multipart.MultipartFile;

import shop.errorItem.*;
import shop.ImageItem;

import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@RestController
public class ShopController {


    private final String basePath = "/tmp/upload/";

    @Autowired
    private Environment env;

    @Autowired
    ShopService shopService;

    

    @RequestMapping(value = "/")
    public String index() {
        return "index";
    }

    // Auth
    @RequestMapping(value = "/api/auth/login/admin", method = RequestMethod.POST)
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

    @RequestMapping(value = "/api/auth/check/admin", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<?> checkAdminLogin(HttpSession session) {
        if (session.getAttribute("adminLogged") != null) {
            if ((Boolean) session.getAttribute("adminLogged") != null && session.getAttribute("adminLogged") != null) {
                return new ResponseEntity<>(true, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(false, HttpStatus.OK);
            }
        } else {
            return new ResponseEntity<>(false, HttpStatus.OK);
        }

    }

    @RequestMapping(value = "api/auth/logout/admin", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<?> adminLogout(HttpSession session) {
        session.setAttribute("adminLogged", null);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // Editor / Post
    @RequestMapping(value = "/api/post/alert/{id}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<?> getAlertItemById(@PathVariable Long id) {
        try {
            AlertItem alertItem = shopService.getAlertItemById(id);
            if (alertItem == null) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(alertItem, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        }
    }

    @RequestMapping(value = "/api/post/alert/{id}", method = RequestMethod.DELETE)
    @ResponseBody
    public ResponseEntity<?> removeAlertItem(@PathVariable Long id) {
        try {
            if (shopService.removeAlertItem(id)) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/api/post/alert/{id}", method = RequestMethod.PATCH)
    @ResponseBody
    public ResponseEntity<?> updateAlertItem(@PathVariable Long id, @RequestBody Map<String, String> body) {

        if (!(body.get("title") instanceof String) || body.get("title").equals("") || body.get("title") == null) {
            ErrorItem errorItem = new ErrorItem();
            errorItem.setContent("공지 제목을 입력하여 주세요.");
            return new ResponseEntity<>(errorItem, HttpStatus.UNPROCESSABLE_ENTITY);
        }
        if (!(body.get("body") instanceof String) || body.get("body").equals("") || body.get("body") == null) {
            ErrorItem errorItem = new ErrorItem();
            errorItem.setContent("공지 내용을 입력하여 주세요.");
            return new ResponseEntity<>(errorItem, HttpStatus.UNPROCESSABLE_ENTITY);
        }

        try {
            AlertItem alertItem = shopService.getAlertItemById(id);
            if (alertItem == null) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            alertItem.setTitle(body.get("title"));
            alertItem.setBody(body.get("body"));
            shopService.updateAlertItem(alertItem);

            return new ResponseEntity<>(alertItem, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/api/post/alert", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<?> getAlertItemList(
            @RequestParam(value = "page", defaultValue = "1", required = false) Long page) {
        List<AlertItem> alertItemList;
        HttpHeaders headers = new HttpHeaders();

        if (page < 1) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        try {
            alertItemList = shopService.getAlertItemList(page);
            headers.add("Last-Page", (int) Math.ceil(shopService.getAlertItemSize() / 10) + 1 + "");
            return new ResponseEntity<>(alertItemList, headers, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/api/post/alert", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<?> writeAlertItem(AlertItem alertItem, @RequestBody Map<String, String> body) {

        if (!(body.get("title") instanceof String) || body.get("title").equals("") || body.get("title") == null) {
            ErrorItem errorItem = new ErrorItem();
            errorItem.setContent("공지 제목을 입력하여 주세요.");
            return new ResponseEntity<>(errorItem, HttpStatus.UNPROCESSABLE_ENTITY);
        }
        if (!(body.get("body") instanceof String) || body.get("body").equals("") || body.get("body") == null) {
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
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }


    // Item Editor Upload File Router

    @RequestMapping(value="/api/upload", method=RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<?> uploadImage(ImageItem imageItem ,@RequestParam("file") MultipartFile file){
        String saveName = String.valueOf(new Date().getTime());

        OutputStream fos = null;

        try {
            fos = new FileOutputStream(basePath + File.separator + saveName + ".jpg");
            fos.write(file.getBytes());
            imageItem.setImageName(saveName + ".jpg");
            return new ResponseEntity<>(imageItem, HttpStatus.OK);
        } catch(Exception e){
            e.printStackTrace();
        } finally {
            try {
                if(fos != null) {
                    fos.close();
                }
            } catch(IOException e) {
                e.printStackTrace();
            }
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);



    }

    @RequestMapping(value="/api/images", method=RequestMethod.GET)
    public void showImage(HttpServletRequest request, HttpServletResponse response) {
        String saveName = request.getQueryString();
        if(saveName == null) {
            return;
        }

        File f = new File(basePath + saveName);
        InputStream is = null;
        try{
            is = new FileInputStream(f);
            OutputStream oos = response.getOutputStream();

            byte[] buf = new byte[8192];
            int c = 0;
            while((c = is.read(buf, 0, buf.length)) > 0){
                oos.write(buf, 0, c);
                oos.flush();
            }
            oos.close();
            is.close();
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    @RequestMapping(value="/api/uploadItem", method=RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<?> uploadItem(Item item ,@RequestBody Map<String, ?> body){
        String title = body.get("title").toString();
        String markdown = body.get("markdown").toString();
        int price = Integer.parseInt(body.get("price").toString());
        String images = body.get("images").toString();
        
        item.setTitle(title);
        item.setBody(markdown);
        item.setPrice(price);
        item.setImageNames(images);


        try {
            String publishedDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
            item.setTitle(title);
            item.setBody(markdown);
            item.setPrice(price);
            item.setImageNames(images);
            item.setPublishedDate(publishedDate);
            item = shopService.getItemById(shopService.writeItem(item));
            return new ResponseEntity<>(item, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }



        // String[] imageArray = images.split(",");
        // String imageNames = "";
        // // System.out.println(imageArray.length);
        // for(String image: imageArray){
        //     imageNames += image;
        // }
        // System.out.println(imageNames);
        

        // if()
    }
}