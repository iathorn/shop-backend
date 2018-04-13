package shop;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.Key;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.transform.impl.AddInitTransformer;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import shop.errorItem.*;
import shop.models.account.Account;
import shop.models.account.LoginError;
import shop.models.account.SignupError;
import shop.models.cart.Cart;
import shop.models.cart.CartDeleteIndex;
import shop.models.cart.CartError;
import shop.models.cart.CartLog;
import shop.models.checkout.Checkout;
import shop.utils.password.AES256Util;
import shop.ImageItem;

import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@RestController
public class ShopController {

    private final String basePath = "/shop/upload/";

    @Autowired
    private Environment env;

    @Autowired
    ShopService shopService;

    // @RequestMapping(value = "/alert")
    // public String index() {
    //     return "redirect:/pages/final.html";
    // }

    

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

        String publishedDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

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

    @RequestMapping(value = "/api/upload", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<?> uploadImage(ImageItem imageItem, @RequestParam("file") MultipartFile file) {
        String saveName = String.valueOf(new Date().getTime());

        OutputStream fos = null;

        try {
            fos = new FileOutputStream(basePath + File.separator + saveName + ".jpg");
            fos.write(file.getBytes());
            imageItem.setImageName(saveName + ".jpg");
            return new ResponseEntity<>(imageItem, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

    }

    @RequestMapping(value = "/api/images", method = RequestMethod.GET)
    public void showImage(HttpServletRequest request, HttpServletResponse response) {
        String saveName = request.getQueryString();
        if (saveName == null) {
            return;
        }

        File f = new File(basePath + saveName);
        InputStream is = null;
        try {
            is = new FileInputStream(f);
            OutputStream oos = response.getOutputStream();

            byte[] buf = new byte[8192];
            int c = 0;
            while ((c = is.read(buf, 0, buf.length)) > 0) {
                oos.write(buf, 0, c);
                oos.flush();
            }
            oos.close();
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @RequestMapping(value = "/api/uploadItem", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<?> uploadItem(Item item, @RequestBody Map<String, ?> body) {
        String title = body.get("title").toString();
        String markdown = body.get("markdown").toString();
        int price = Integer.parseInt(body.get("price").toString());
        String images = body.get("images").toString();

        item.setTitle(title);
        item.setBody(markdown);
        item.setPrice(price);
        item.setImageNames(images);

        try {
            String publishedDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
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

    // Items

    @RequestMapping(value = "/api/item", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<?> getItemList() {
        List<Item> itemList;

        try {
            itemList = shopService.getItemList();
            return new ResponseEntity<>(itemList, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/api/item/{id}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<?> getItemById(@PathVariable Long id) {
        try {
            Item item = shopService.getItemById(id);
            if (item == null) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(item, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        }
    }

    @RequestMapping(value="/api/item/{id}", method=RequestMethod.DELETE)
    @ResponseBody
    public ResponseEntity<?> removeItemById(@PathVariable Long id) {
        

        try {
            Boolean deleted = shopService.deleteById(id);
            if(deleted) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            } else {
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } catch(Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value="/api/item/{id}", method=RequestMethod.PATCH)
    @ResponseBody
    public ResponseEntity<?> updateItemById(@PathVariable Long id, @RequestBody Map<String, String> bodies, Item item)
    {
        String title = bodies.get("title");
        String body = bodies.get("body");
        String price = bodies.get("price");
        String imageNames = bodies.get("imageNames");
        try {
            item.setId(id);
            item.setTitle(title);
            item.setBody(body);
            item.setPrice(Integer.parseInt(price));
            item.setImageNames(imageNames);
            shopService.updateById(item);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch(Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Auth (member)

    @RequestMapping(value = "/api/member", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<?> signup(Account account, @RequestBody Map<String, String> body) {
        String userID = body.get("userID");
        String userName = body.get("userName");
        String userEmail = body.get("userEmail");
        String userPassword = body.get("userPassword");
        String userPostAddress = body.get("userPostAddress");
        String userPostCode = body.get("userPostCode");
        String userDetailAddress = body.get("userDetailAddress");

        String userID_userName_regex = "^[a-zA-Z]{1}[a-zA-Z0-9_]{4,11}$";
        String userName_regex = ".*[ㄱ-ㅎㅏ-ㅣ가-힣]+.*";
        String userEmail_regex = "^[_a-zA-Z0-9-\\.]+@[\\.a-zA-Z0-9-]+\\.[a-zA-Z]+$";

        Pattern VALID_USERID_USERNAME_REGEX = Pattern.compile(userID_userName_regex, Pattern.CASE_INSENSITIVE);
        Pattern VALID_USEREMAIL_REGEX = Pattern.compile(userEmail_regex, Pattern.CASE_INSENSITIVE);
        Pattern VALID_USERNAME_REGEX = Pattern.compile(userName_regex, Pattern.CASE_INSENSITIVE);

        Matcher matcher_userID = VALID_USERID_USERNAME_REGEX.matcher(userID);
        Matcher matcher_userName = VALID_USERNAME_REGEX.matcher(userName);
        Matcher matcher_userEmail = VALID_USEREMAIL_REGEX.matcher(userEmail);
        // assertTrue(matcher.find());

        if (!matcher_userID.find()) {
            SignupError signupError = new SignupError();
            signupError.setErrorCode(100);
            signupError.setErrorLog("부적절한 아이디입니다");
            return new ResponseEntity<>(signupError, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        if (userName.length() <= 0 || userName.equals("")) {
            SignupError signupError = new SignupError();
            signupError.setErrorCode(200);
            signupError.setErrorLog("부적절한 이름입니다.");
            return new ResponseEntity<>(signupError, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        // if (!matcher_userName.find()) {
        //     SignupError signupError = new SignupError();
        //     signupError.setErrorCode(200);
        //     signupError.setErrorLog("부적절한 닉네임입니다.");
        //     return new ResponseEntity<>(signupError, HttpStatus.INTERNAL_SERVER_ERROR);
        // }

        if (!matcher_userEmail.find()) {
            SignupError signupError = new SignupError();
            signupError.setErrorCode(300);
            signupError.setErrorLog("부적절한 이메일입니다.");
            return new ResponseEntity<>(signupError, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        if (userPassword.length() < 4 || !(userPassword instanceof String)) {
            SignupError signupError = new SignupError();
            signupError.setErrorCode(400);
            signupError.setErrorLog("패스워드는 4자이상으로 해주세요.");
            return new ResponseEntity<>(signupError, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        if (userPostAddress.equals("") || userPostAddress.length() <= 0 || userPostCode.equals("")
                || userPostCode.length() <= 0 || userDetailAddress.equals("") || userDetailAddress.length() <= 0) {
            SignupError signupError = new SignupError();
            signupError.setErrorCode(700);
            signupError.setErrorLog("주소가 제대로 입력되지 않았습니다.");
            return new ResponseEntity<>(signupError, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        if (shopService.getUserByUserID(userID)) {
            SignupError signupError = new SignupError();
            signupError.setErrorCode(600);
            signupError.setErrorLog("이미 있는 아이디 입니다.");
            return new ResponseEntity<>(signupError, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        try {
            AES256Util util = new AES256Util("myHashKey123456#@#$0098877^^^^");
            userPassword = util.encrypt(userPassword);
        } catch (Exception e) {
            e.printStackTrace();
            SignupError signupError = new SignupError();
            signupError.setErrorCode(500);
            signupError.setErrorLog("패스워드 암호화 오류.");
            return new ResponseEntity<>(signupError, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        String createdAt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

        try {
            account.setUserID(userID);
            account.setUserName(userName);
            account.setUserEmail(userEmail);
            account.setUserPassword(userPassword);
            account.setUserPostAddress(userPostAddress);
            account.setUserPostCode(userPostCode);
            account.setUserDetailAddress(userDetailAddress);
            account.setCreatedAt(createdAt);
            account = shopService.getUserById(shopService.signup(account));
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/api/member/checkid", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<?> checkid(@RequestBody Map<String, String> body) {
        String userID = body.get("userID");
        String userID_userName_regex = "^[a-zA-Z]{1}[a-zA-Z0-9_]{4,11}$";
        Pattern VALID_USERID_USERNAME_REGEX = Pattern.compile(userID_userName_regex, Pattern.CASE_INSENSITIVE);

        Matcher matcher_userID = VALID_USERID_USERNAME_REGEX.matcher(userID);
        if (!matcher_userID.find()) {
            SignupError signupError = new SignupError();
            signupError.setErrorCode(100);
            signupError.setErrorLog("부적절한 아이디입니다");
            return new ResponseEntity<>(signupError, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        try {
            Boolean existing = shopService.getUserByUserID(userID);
            if (!existing) {
                return new ResponseEntity<>(HttpStatus.OK);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        SignupError signupError = new SignupError();
        signupError.setErrorCode(600);
        signupError.setErrorLog("이미 존재하는 아이디입니다.");
        return new ResponseEntity<>(signupError, HttpStatus.INTERNAL_SERVER_ERROR);

    }

    @RequestMapping(value = "/api/member/login", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<?> login(@RequestBody Map<String, String> body, HttpSession session) {
        String userID = body.get("userID");
        String userPassword = body.get("userPassword");
        try {
            Boolean exisiting = shopService.getUserByUserID(userID);
            if (!exisiting) {
                LoginError loginError = new LoginError();
                loginError.setErrorCode(100);
                loginError.setErrorLog("존재하지 않는 아이디입니다.");
                return new ResponseEntity<>(loginError, HttpStatus.UNAUTHORIZED);
            }

            String hashedPassword = shopService.getHashedPassword(userID);
            AES256Util util = new AES256Util("myHashKey123456#@#$0098877^^^^");
            String decrypted = util.decrypt(hashedPassword);

            if (decrypted.equals(userPassword)) {
                session.setAttribute(userID, true);
                System.out.println(session.getAttribute(userID));
                return new ResponseEntity<>(true, HttpStatus.OK);
            }

            LoginError loginError = new LoginError();
            loginError.setErrorCode(200);
            loginError.setErrorLog("비밀번호가 일치하지 않습니다.");

            return new ResponseEntity<>(loginError, HttpStatus.UNAUTHORIZED);

        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/api/member/login/check", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<?> checkLogin(HttpSession session, @RequestBody Map<String, String> body) {
        String userID = body.get("userID");
        if (session.getAttribute(userID) != null) {
            if ((Boolean) session.getAttribute(userID) == true && session.getAttribute(userID) != null) {

                return new ResponseEntity<>(true, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(false, HttpStatus.OK);
            }
        } else {
            return new ResponseEntity<>(false, HttpStatus.OK);
        }
    }

    @RequestMapping(value = "/api/member/logout", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<?> memberLogout(HttpSession session, @RequestBody Map<String, String> body) {
        String userID = body.get("userID");
        session.setAttribute(userID, null);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @RequestMapping(value = "/api/member/info", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<?> getUserInfo(@RequestBody Map<String, String> body) {
        String userID = body.get("userID");

        try {
            Boolean isExisting = shopService.getUserByUserID(userID);
            if (!isExisting) {
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
            Account account = shopService.getUserInfo(userID);
            account.setUserPassword(null);
            return new ResponseEntity<>(account, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Cart
    Map<String, String[]> cartMap = new HashMap<String, String[]>();

    @RequestMapping(value = "/api/cart", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<?> addCart(@RequestBody Map<String, String> body, HttpServletRequest request,
            HttpSession session) {

        Map<String, String[]> testMap = (Map<String, String[]>) session.getAttribute("cartSession1");

        String id = body.get("id").toString();
        String title = body.get("title");
        String amount = body.get("amount");
        String thumbnailImage = body.get("thumbnailImage");
        String totalPrice = body.get("totalPrice");
        System.out.println("id: " + id);

        try {
            if (amount.equals("0") || Integer.parseInt(amount) <= 0) {
                CartError cartError = new CartError();
                cartError.setErrorCode(100);
                cartError.setErrorLog("수량이 제대로 선택되지 않았습니다.");
                return new ResponseEntity<>(cartError, HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } catch (Exception e) {
            e.printStackTrace();
            CartError cartError = new CartError();
            cartError.setErrorCode(100);
            cartError.setErrorLog("수량이 제대로 선택되지 않았습니다.");
            return new ResponseEntity<>(cartError, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        if (testMap == null) {
            testMap = new HashMap<String, String[]>();
        }
        if (testMap.get(id) != null) {
            int beforeAmount = Integer.parseInt(testMap.get(id)[2]);
            int updatedAmount = Integer.parseInt(amount);
            int amountResult = beforeAmount + updatedAmount;
            String[] updatedCartDetailArray = { id, title, amount, thumbnailImage, totalPrice };
            testMap.put(id, updatedCartDetailArray);
            session.setAttribute("cartSession1", testMap);

            CartLog cartLog = new CartLog();
            cartLog.setCartLog("이미 장바구니에 담겨있어서 수량을 조정하였습니다.");

            // testMap.forEach((k,v)->for(int i = 0;i<4;i+);
            Iterator<String> keys = testMap.keySet().iterator();
            while (keys.hasNext()) {
                String key = keys.next();
                String[] eachCartSession = testMap.get(key);
                for (int i = 0; i < 4; i++) {
                    System.out.println(eachCartSession[i]);
                }
            }

            // System.out.println(testMap.get("1")[0]);
            // System.out.println(testMap.get("1")[1]);
            // System.out.println(testMap.get("1")[2]);
            // System.out.println(testMap.get("1")[3]);

            return new ResponseEntity<>(cartLog, HttpStatus.OK);
        } else {
            String[] cartDetailArray = { id, title, amount, thumbnailImage, totalPrice };
            testMap.put(id, cartDetailArray);
            session.setAttribute("cartSession1", testMap);

            Iterator<String> keys = testMap.keySet().iterator();
            while (keys.hasNext()) {
                String key = keys.next();
                String[] eachCartSession = testMap.get(key);
                for (int i = 0; i < 4; i++) {
                    System.out.println(eachCartSession[i]);
                }
            }

            CartLog cartLog = new CartLog();
            cartLog.setCartLog("장바구니에 담겼습니다.");
            return new ResponseEntity<>(cartLog, HttpStatus.OK);
        }

        // String[] cartDetailArray = {title, amount, thumbnailImage, totalPrice};
        // if(cartMap.get(id) != null) {
        //     int beforeAmount = Integer.parseInt(cartMap.get(id)[1]);
        //     int updatedAmount = Integer.parseInt(amount);
        //     int amountResult = beforeAmount + updatedAmount;
        //     String [] updatedCartDetailArray = {title, amountResult + "", thumbnailImage, totalPrice};
        //     cartMap.put(id, updatedCartDetailArray);
        //     System.out.println("here");
        //     System.out.println(cartMap.get(id));
        // } else {
        //     cartMap.put(id, cartDetailArray);
        //     System.out.println("here2");
        // }

        // System.out.println("CartMap: " + cartMap.get(id)[1]);

        // session.setAttribute("cartSession", testMap.get(id)[1]);

        // System.out.println("sessionTest: " + testMap.get(id)[1]);
        // System.out.println("sessionNull: " + testMap.get("10000"));

        // ArrayList cartTitleList = (ArrayList) request.getSession().getAttribute("cartTitleList");
        // if (cartTitleList == null) {
        //     cartTitleList = new ArrayList();
        // }
        // request.getSession().setAttribute("cartTitleList", cartTitleList);
        // cartTitleList.add(title);

        // ArrayList cartAmountList = (ArrayList) request.getSession().getAttribute("cartAmountList");
        // if (cartAmountList == null) {
        //     cartAmountList = new ArrayList();
        // }
        // request.getSession().setAttribute("cartAmountList", cartAmountList);
        // cartAmountList.add(amount);

        // ArrayList cartThumbnailImageList = (ArrayList) request.getSession().getAttribute("cartThumbnailImageList");
        // if (cartThumbnailImageList == null) {
        //     cartThumbnailImageList = new ArrayList();
        // }
        // request.getSession().setAttribute("cartThumbnailImageList", cartThumbnailImageList);
        // cartThumbnailImageList.add(thumbnailImage);

        // ArrayList cartTotalPriceList = (ArrayList) request.getSession().getAttribute("cartTotalPriceList");
        // if (cartTotalPriceList == null) {
        //     cartTotalPriceList = new ArrayList();
        // }
        // request.getSession().setAttribute("cartTotalPriceList", cartTotalPriceList);
        // cartTotalPriceList.add(totalPrice);

    }

    @RequestMapping(value = "/api/cart", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<?> getCartList(HttpSession session) {
        Map<String, String[]> testMap = (Map<String, String[]>) session.getAttribute("cartSession1");

        System.out.println("testMap: " + testMap);
        // if(testMap == {}) {
        //     System.out.println("here");
        //     return new ResponseEntity<>(true, HttpStatus.OK);
        // }

        Iterator<String> keys = testMap.keySet().iterator();
        // ArrayList<String[]> responseBody = new ArrayList<>();
        ArrayList<Cart> cartList = new ArrayList<>();
        while (keys.hasNext()) {
            String key = keys.next();
            String[] eachCartSession = testMap.get(key);
            // for (int i = 0; i < 4; i++) {
            //     System.out.println(eachCartSession[i]);
                
            // }
            // responseBody.add(eachCartSession);
            Cart cart = new Cart();
            cart.setId(Integer.parseInt(eachCartSession[0]));
            cart.setTitle(eachCartSession[1]);
            cart.setAmount(Integer.parseInt(eachCartSession[2]));
            cart.setThumbnailImage(eachCartSession[3]);
            cart.setTotalPrice(Integer.parseInt(eachCartSession[4]));
            cartList.add(cart);
        }

        return new ResponseEntity<>(cartList, HttpStatus.OK);


         

        // ArrayList cartTitleList = (ArrayList) request.getSession().getAttribute("cartTitleList");
        // if (cartTitleList == null) {
        //     return new ResponseEntity<>(false, HttpStatus.OK);
        // }

        // ArrayList cartAmountList = (ArrayList) request.getSession().getAttribute("cartAmountList");
        // if (cartAmountList == null) {
        //     return new ResponseEntity<>(false, HttpStatus.OK);
        // }

        // ArrayList cartThumbnailImageList = (ArrayList) request.getSession().getAttribute("cartThumbnailImageList");
        // if (cartThumbnailImageList == null) {
        //     return new ResponseEntity<>(false, HttpStatus.OK);
        // }

        // ArrayList cartTotalPriceList = (ArrayList) request.getSession().getAttribute("cartTotalPriceList");
        // if (cartTotalPriceList == null) {
        //     return new ResponseEntity<>(false, HttpStatus.OK);
        // }

        // ArrayList<String> cartList = new ArrayList<>();
        // int amount = 0;
        // int totalPrice = 0;
        // for(int i = 0;i<cartTitleList.size();i++){
        //     cartList.add(cartTitleList.get(i).toString() + "/" + cartAmountList.get(i).toString() + "/" + 
        //     cartThumbnailImageList.get(i).toString() + "/" + cartTotalPriceList.get(i).toString() + "/");
        //     amount += Integer.parseInt(cartAmountList.get(i).toString());
        //     totalPrice += Integer.parseInt(cartTotalPriceList.get(i).toString());

        // }
        // cartList.add(amount + "/" + totalPrice);

        // return new ResponseEntity<>(cartList, HttpStatus.OK);

    }

    @RequestMapping(value = "/api/cart", method = RequestMethod.DELETE)
    @ResponseBody
    public ResponseEntity<?> removeCartList(HttpSession session) {
        Map<String, String[]> testMap = (Map<String, String[]>) session.getAttribute("cartSession1");

        session.setAttribute("cartSession1", null);


        // request.getSession().setAttribute("cartTitleList", null);
        // request.getSession().setAttribute("cartAmountList", null);
        // request.getSession().setAttribute("cartThumbnailImageList", null);
        // request.getSession().setAttribute("cartTotalPriceList", null);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @RequestMapping(value = "/api/cart/{id}", method = RequestMethod.DELETE)
    @ResponseBody
    public ResponseEntity<?> removeCartById(@PathVariable int id, HttpServletRequest request, HttpSession session) {
        Map<String, String[]> testMap = (Map<String, String[]>) session.getAttribute("cartSession1");
        testMap.remove(id + "");
        Iterator<String> keys = testMap.keySet().iterator();
        // ArrayList<String[]> responseBody = new ArrayList<>();
        ArrayList<Cart> cartList = new ArrayList<>();
        while (keys.hasNext()) {
            String key = keys.next();
            String[] eachCartSession = testMap.get(key);
            // for (int i = 0; i < 4; i++) {
            //     System.out.println(eachCartSession[i]);
                
            // }
            // responseBody.add(eachCartSession);
            Cart cart = new Cart();
            cart.setId(Integer.parseInt(eachCartSession[0]));
            cart.setTitle(eachCartSession[1]);
            cart.setAmount(Integer.parseInt(eachCartSession[2]));
            cart.setThumbnailImage(eachCartSession[3]);
            cart.setTotalPrice(Integer.parseInt(eachCartSession[4]));
            cartList.add(cart);
        }
        // ArrayList cartTitleList = (ArrayList) request.getSession().getAttribute("cartTitleList");

        // ArrayList cartAmountList = (ArrayList) request.getSession().getAttribute("cartAmountList");

        // ArrayList cartThumbnailImageList = (ArrayList) request.getSession().getAttribute("cartThumbnailImageList");

        // ArrayList cartTotalPriceList = (ArrayList) request.getSession().getAttribute("cartTotalPriceList");

        // cartTitleList.remove(id);
        // cartAmountList.remove(id);
        // cartThumbnailImageList.remove(id);
        // cartTotalPriceList.remove(id);

        return new ResponseEntity<>(cartList, HttpStatus.OK);

    }

    // Checkout

    @RequestMapping(value = "/api/checkout/complete", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<?> checkout(@RequestParam Map<String, String> body, Checkout checkout) {
        String imp_uid = body.get("imp_uid").toString();
        String merchant_uid = body.get("merchant_uid").toString();
        String paid_amount = body.get("paid_amount").toString();
        String apply_num = body.get("apply_num").toString();
        String buyer_email = body.get("buyer_email").toString();
        String buyer_name = body.get("buyer_name").toString();
        String buyer_tel = body.get("buyer_tel").toString();
        String buyer_addr = body.get("buyer_addr").toString();
        String buyer_postcode = body.get("buyer_postcode").toString();
        String buyer_request_message = body.get("buyer_request_message").toString();


        String createdAt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

        checkout.setImp_uid(imp_uid);
        checkout.setMerchant_uid(merchant_uid);
        checkout.setPaid_amount(paid_amount);
        checkout.setApply_num(apply_num);
        checkout.setBuyer_email(buyer_email);
        checkout.setBuyer_name(buyer_name);
        checkout.setBuyer_tel(buyer_tel);
        checkout.setBuyer_addr(buyer_addr);
        checkout.setBuyer_postcode(buyer_postcode);
        checkout.setBuyer_request_message(buyer_request_message);
        checkout.setCreatedAt(createdAt);

        try {
            checkout = shopService.getCheckoutById(shopService.checkout(checkout));
            return new ResponseEntity<>(true, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}