package com.ifresh.customer.helper;

import com.android.volley.toolbox.ImageLoader;


public class Constant {

    //online path
    public static String BASEPATH = "http://ec2-65-0-127-124.ap-south-1.compute.amazonaws.com:3000/api/";
    public static String IMAGEBASEPATH= "http://ec2-65-0-127-124.ap-south-1.compute.amazonaws.com:3000/uploads/";

    //Staging server path
    //online path
    //public static String BASEPATH = "http://ec2-65-0-127-124.ap-south-1.compute.amazonaws.com:5000/api/";
    //public static String IMAGEBASEPATH= "http://ec2-65-0-127-124.ap-south-1.compute.amazonaws.com:5000/uploads/";



    //offline path
    //public static String BASEPATH = "http://192.168.1.7:3000/api/";
    //public static String IMAGEBASEPATH= "http://192.168.1.7:3000/uploads/";


    public static String BANNERIMAGEPATH = IMAGEBASEPATH+"banner_img/";
    public static String CATEGORYIMAGEPATH = IMAGEBASEPATH+"catagory_img/";
    public static String PRODUCTIMAGEPATH = IMAGEBASEPATH+"product_img/";
    public static String FIRMIMAGEPATH = IMAGEBASEPATH+"firm_img/";
    public static String SETTINGIMAGEPATH = IMAGEBASEPATH+"setting_img/";
    public static String USERIMAGEPATH = IMAGEBASEPATH+"user_img/";
    public static String OFFER_IMAGE = IMAGEBASEPATH + "offer_banners/";
    public static String UPLOAD_IMAGE_SHOW = IMAGEBASEPATH + "user_order/";

    public static String ALL_CATEGORYIMAGEPATH = IMAGEBASEPATH + "all_catagory/";
    public static String CHECKADDRESS = "address/checkaddress";

    public static String SECTIONPRODUCT =  "franchise/getfranchiseproducts/";
    public static String GET_WALLET_BAL_URL = "walletlog/gethistory/";
    public static String GET_FRIEND_URL = "user/getmyfriends/";

    public static String UPLOAD_IMAGE = "order/uploadorderimg";
    public static String PLACING_IMAGEORDER = "order/palceimgorder";
    public static String ORDER_DELETE = "order/deleteorderimg/";

    public static String RESEND_OTP = "user/resendotp";

    public static String BANNERIMAGE = "franchise/getfrbanner/";
    public static String GET_COUNTRY =  "country/index/";
    public static String GET_STATE =  "state/getstatebycid/";
    public static String GET_CITY =  "city/getcitybystateid/";
    public static String GET_AREA =  "area/index/";
    public static String GET_SUBAREA = "subarea/index/";
    public static String LOGIN =  "user/login";
    public static String SENDDEVICEID =  "user/isdeviceexist";

    public static String GUEST =  "user/getguest";
    public static String CHANGENO =  "user/changeno";
    public static String SAVE_DELIVERYADDRESS =  "address/save";

    public static String SETTINGS_PAGE =  "settings/index";
    public static String CHANGEPASSWORD =  "changepassword";
    public static String FORGETPASSWORD =  "forgetpassword";
    public static String EDITPROFILE =  "/user/edit";
    public static String VARIFYOTP = "user/varifyOtp";
    public static String GETCATEGORY =  "franchise/getfrcats/";
    public static String GETFRENCHISE = "franchise/getfruser/";
    public static String ADDRESSSAVE = "address/save";
    public static String GET_DEFULTADD = "address/edit/";
    public static String SET_DEFULTADD="address/default/";
    public static String SET_DELETEADD="address/delete";
    public static String GET_USERDEFULTADD = "address/wholeaddress";
    public static String GET_PRODUCTLIST = "franchise/getsubcatsandproductbycat/";
    public static String GET_WALLETBAL = "user/getwalletbalance/";
    public static String GET_ORDERSEND = "order/placeorder";
    public static String GET_TRACKORDER = "order/trackingorder";
    public static String GET_ORDERCANCEL = "order/updatestatus";
    public static String GET_ORDERCONFORMATION = "order/findorderbeforecancel/";

    public static String GET_SEARCHPRODUCT = "search/product";
    public static String GET_CMSPAGE = "settings/cms/";
    public static String GET_ORDERPROCESS="orderprocess/detail";
    public static String GET_GETPRODUCTBYID ="franchise/getincartproductdetail";
    public static String GET_CONFIGSETTING =  "settings/getconfigs";
    public static String GET_OFFER = "offer/index/";
    public static String GET_CHECKADDRESS ="address/checkaddress";
    public static String GET_OFFERPRODUCT="list/offerChild/";

    public static String GET_NOTIFICATION="notify/getnotification/";


    public static String SUCESS = "sucess";

    public static Integer is_permission_grant = 0;

    public static int  thatThingHappened = 0;




    //public static String MAINBASEUrl = "http://ifresh.justoprint.com/admin/";
    public static String MAINBASEUrl = "http://ifresh.co.in/admin/";
    //public static String MAINBASEUrl = "http://13.233.244.40/admin/";
    public static String BaseUrl = MAINBASEUrl + "api-firebase/";

    public static ImageLoader imageLoader = AppController.getInstance().getImageLoader();
    public static String FAQ_URL = MAINBASEUrl + "pages_web/faq.php";
    public static String SETTINGHOME = BaseUrl + "settinghome.php";
    public static String GETUSERINFO = BaseUrl + "userprofile.php";


    public static String SliderUrl = BaseUrl + "slider-images.php";
    public static String CategoryUrl = BaseUrl + "get-categories.php";
    public static String Get_RazorPay_OrderId = BaseUrl + "create-razorpay-order.php";
    public static String SubcategoryUrl = BaseUrl + "get-subcategories-by-category-id.php";
    public static String FeaturedProductUrl = BaseUrl + "sections.php";
    public static String GET_SECTION_URL = BaseUrl + "sections.php";


    public static String RegisterUrl = BaseUrl + "user-registration.php";
    public static String PAPAL_URL = MAINBASEUrl + "paypal/create-payment.php";
    public static String LoginUrl = BaseUrl + "login.php";
    public static String OFFER_URL = BaseUrl + "offer-images.php";
    public static String PRODUCT_SEARCH_URL = BaseUrl + "products-search.php";
    public static String SETTING_URL = BaseUrl + "settings.php";
    public static String GET_PRODUCT_BY_SUB_CATE = BaseUrl + "get-products-by-subcategory-id.php";
    public static String GET_PRODUCT_DETAIL_URL = BaseUrl + "get-product-by-id.php";
    public static String CITY_URL = BaseUrl + "get-cities.php";
    public static String GET_AREA_BY_CITY = BaseUrl + "get-areas-by-city-id.php";
    public static String ORDERPROCESS_URL = BaseUrl + "order-process.php";
    public static String USER_DATA_URL = BaseUrl + "get-user-data.php";

    public static String PROMO_CODE_CHECK_URL = BaseUrl + "validate-promo-code.php";
    public static String VALIDATE_PROMO_CODE = "validate_promo_code";
    public static String DISCOUNTED_AMOUNT = "discounted_amount";
    public static String REGISTER_DEVICE = "register-device";
    public static String AccessKey = "accesskey";
    public static String AccessKeyVal = "90336";
    public static String GetVal = "1";

    public static String AUTHORIZATION = "Authorization";
    public static String GET_PRIVACY = "get_privacy";
    public static String GET_TERMS = "get_terms";
    public static String GET_CONTACT = "get_contact";
    public static String GET_ABOUT_US = "get_about_us";
    public static String GET_OFFER_IMAGE = "get-offer-images";
    public static String GET_ALL_SECTIONS = "get-all-sections";
    public static String CANCELLED = "cancelled";
    public static String GET_NOTIFICATIONS = "get-notifications";
    public static String GET_WALLETBALANCE = "get-user-wallet";

    public static String SHORT_LINK="short_link";
    public static String EARN_MSG="earn_msg";
    public static String USER_REFER_AMT="use_refer_code_amount";
    public static String FRIEND_ONE="friend_one_earn";
    public static String FRIEND_SECOND="friend_two_earn";
    public static String EXPIRY_DAY="expiry_day";

    public static String MSG_1="msg_1";
    public static String MSG_2="msg_2";
    public static String MSG_3="msg_3";
    public static String SHARE_MSG = "share_msg";
    public static String SETTING_HOME = "settinghome";
    public static String PARENT_ID="parent_id";
    public static String KEY_FCM_ID="";
    public static String YOUTUBECODE="";
    public static String  DEVICE_REG_MSG="";



    public static String GET_TIME_SLOT = "get_time_slots";
    public static String RETURNED = "returned";
    public static String GET_USER_DATA = "get_user_data";
    public static String KEY_BALANCE = "balance";
    public static String KEY_REFER_EARN_BONUS = "refer-earn-bonus";
    public static String KEY_REFER_EARN_STATUS = "is-refer-earn-on";
    public static String KEY_MAX_EARN_AMOUNT = "max-refer-earn-amount";
    public static String KEY_MIN_WIDRAWAL = "minimum-withdrawal-amount";
    public static String KEY_WALLET_USED = "wallet_used";
    public static String KEY_WALLET_BALANCE = "wallet_balance";
    public static String KEY_VERSION_CODE = "current_version";
    public static String KEY_VERSION_NAME = "version_code_name";
    public static String KEY_MIN_VERSION_REQUIRED = "minimum_version_required";
    public static String KEY_UPDATE_STATUS = "is-version-system-on";
    public static String KEY_ORDER_RETURN_DAY_LIMIT = "max-product-return-days";
    public static String FIRST_NAME = "first_name";
    public static String LAST_NAME = "last_name";
    public static String PAYER_EMAIL = "payer_email";
    public static String COUNTRY_CODE = "country_code";
    public static String TIME_SLOTS = "time_slots";
    public static String LAST_ORDER_TIME = "last_order_time";
    public static String ITEM_NAME = "item_name";
    public static String ITEM_NUMBER = "item_number";
    public static String UPDATE_ORDER_ITEM_STATUS = "update_order_item_status";
    public static String ORDER_ITEM_ID = "order_item_id";
    public static String PAYMENT_METHODS = "payment_methods";
    public static String PAY_M_KEY = "payumoney_merchant_key";
    public static String PAYU_M_ID = "payumoney_merchant_id";
    public static String PAYU_SALT = "payumoney_salt";
    public static String RAZOR_PAY_KEY = "razorpay_key";
    public static String share_url = "http://ifresh.co.in/";
    public static String REFER_EARN_BONUS = "";
    public static String MAX_EARN_AMOUNT = "";
    public static String MIN_EARN_ORDER = "";
    public static String CITY_NAME = "city_name";
    public static String AREA_NAME = "area_name";
    public static String REFERRAL_CODE = "referral_code";
    public static String FRIEND_CODE = "friends_code";
    public static String VERSION_CODE;
    public static String REQUIRED_VERSION;
    public static String VERSION_STATUS;
    public static String SOLDOUT_TEXT = "Sold Out";
    public static int GRIDCOLUMN = 2;
    public static String LOAD_ITEM_LIMIT = "10";

    public static int MAX_PRODUCT_LIMIT = 25;
    public static String SORT = "sort";
    public static String TYPE = "type";
    public static String IMAGE = "image";
    public static String NAME = "name";




    public static String MESSAGE_w = "message";
    public static String AMOUNT_w = "amount";
    public static String DATE_w = "date_created";
    public static String DATE_e = "expiry_date";

    public static String AC_w = "type";


    public static String KEY_MEASUREMENT="key_measurment";
    public static String KEY_ADDRESS="key_address";
    public static String KEY_TIMESLOT="key_slot";
    public static String KEY_DAYSLOT="key_dslot";
    public static String KEY_PAYMENT_TYPE="key_payment_type";
    public static String KEY_PAYMENT_METHOD="key_payment_method";
    public static String KEY_STATUS="status";










    public static String TYPE_ID = "type_id";
    public static String ID = "_id";
    public static String FNAME = "fname";
    public static String LNAME = "lname";


    public static String SUBTITLE = "subtitle";
    public static String PRODUCTS = "products";
    public static String SUC_CATE_ID = "subcategory_id";
    public static String DESCRIPTION = "description";
    public static String STATUS = "status";
    public static String DATE_ADDED = "date_added";
    public static String TITLE = "title";
    public static String SECTION_STYLE = "style";
    public static String SHORT_DESC = "short_description";
    public static String REGISTER = "register";
    public static String EMAIL = "email";
    public static String MOBILE = "mobile";
    public static String PASSWORD = "password";
    public static String FCM_ID = "fcm_id";
    public static String CITY = "city";
    public static String AREA = "area";
    public static String STREET = "street";
    public static String PINCODE = "pincode";
    public static String ERROR = "error";
    public static String VERIFY_USER = "verify-user";
    public static String VERIFY_EMAIL = "verify-user-email";
    public static String GET_SLIDER_IMAGE = "get-slider-images";
    public static String USER_ID = "user_id";
    public static String DOB = "dob";
    public static String CREATEDATE = "created_at";
    public static String DATE_CREATED = "date_created";
    public static String APIKEY = "apikey";
    public static String TAX_AMOUNT = "tax_amount";
    public static String TAX_PERCENT = "tax_percentage";
    public static String DELIVERY_TIME = "delivery_time";
    public static String TXTN_DATE = "transaction_date";

    public static String SELECTEDPRODUCT_POS = "";
    public static String FORGOT_PSW_EMAIL = "forgot-password-email";
    public static String FORGOT_PSW_MOBILE = "forgot-password-mobile";
    public static String EDIT_PROFILE = "edit-profile";
    public static String CHANGE_PASSWORD = "change-password";
    public static String CATEGORY_ID = "category_id";
    public static String SUB_CATEGORY_ID = "subcategory_id";
    public static String CAT_ID = "cat_id";
    public static String PRODUCT_SEARCH = "products-search";
    public static String SEARCH = "search";
    public static String FROMSEARCH = "search";
    public static String Add_TRANSACTION = "add_transaction";
    public static String GET_PAYMENT_METHOD = "get_payment_methods";
    public static String CONTACT = "contact";
    public static String DATA = "data";
    public static String SECTIONS = "sections";
    public static String VARIANT = "variants";
    public static String PRODUCT_ID = "product_id";
    public static String FRANCHISCID  ="franchiseId";
    public static String FRANCHISEPID  ="frProductId";

    public static String MEASUREMENT = "measurement";
    public static String MEASUREMENT_UNIT_ID = "measurement_unit_id";
    public static String PRICE = "price";
    public static String DISCOUNT = "discount";
    public static String DISCOUNTED_PRICE = "discounted_price";
    public static String SERVE_FOR = "serve_for";
    public static String STOCK = "stock";
    public static String STOCK_UNIT_ID = "stock_unit_id";
    public static String MEASUREMENT_UNIT_NAME = "measurement_unit_name";
    public static String STOCK_UNIT_NAME = "stock_unit_name";
    public static String SETTINGS = "settings";
    public static String GET_SETTINGS = "get_settings";
    public static String paypal_method = "paypal_payment_method";
    public static String payu_method = "payumoney_payment_method";
    public static String razor_pay_method = "razorpay_payment_method";
    public static String KEY_REFER_EARN_METHOD = "refer-earn-method";
    public static String KEY_MIN_REFER_ORDER_AMOUNT = "min-refer-earn-order-amount";
    public static String REFER_EARN_ORDER_AMOUNT = "";
    public static String REFER_EARN_METHOD = "";
    public static String REFER_EARN_ACTIVE = "";


    public static String AUTHTOKEN="authtoken";
    public static String COUNTRY_ID ="country_id";
    public static String STATE_ID ="state_id";
    public static String CITY_ID ="city_id";
    public static String AREA_ID ="area_id";
    public static String SUBAREA_ID ="subarea_id";
    public static String COUNTRY_N ="country_name";
    public static String STATE_N ="state_name";
    public static String CITY_N="city_name";
    public static String AREA_N="area_name";
    public static String SUBAREA_N="subarea_name";
    public static String ADDRESS_SAVEMSG = "Address Save Successfully";
    public static String PASSWORD_CHANGE_MSG="Password Change Successfully";
    public static String ADDRESS_DEFAULT_CHANGE_MSG = "Your Default Address Has Been Changed";
    public static String ADDRESS_DELETE_MSG = "Your Selected Address Has Been Deleted";
    public static String SELECT_DEFAULT_ADD = "Please Select Default Address Among Three Address";


    public static String HOME_ADD="home_add";
    public static String HOME_ADD_ID="home_id";

    public static String OFFICE_ADD="office_add";
    public static String OFFICE_ADD_ID="office_id";

    public static String OTHER_ADD="other_add";
    public static String OTHER_ADD_ID="other_id";

    public static String DEFAULT_ADD="default_add";

    public static String NODEFAULT_ADD="No Address Found";

    public static String FEATUREPRODUCT = "Feature Product";
    public static String SUBTITLE_1 = "";




    //settings
    public static Boolean ISACCEPTMINORDER =false;
    public static Double SETTING_DELIVERY_CHARGE = 0.0;
    public static Double SETTING_TAX = 0.0;
    public static Double SETTING_MINIMUM_AMOUNT_FOR_FREE_DELIVERY = 0.0;
    public static String SETTING_CURRENCY_SYMBOL = "\u20B9";
    public static String SETTING_MAIL_ID = "";
    public static String free_delivery_message="";
    public static Double WALLET_BALANCE = 0.0;
    public static Double MINIMUM_WITHDRAW_AMOUNT = 0.0;
    public static int ORDER_DAY_LIMIT = 0;
    public static String country_code = "";
    public static String U_ID = "";
    public static String REPLY_TO = "reply_to";
    public static String MINIMUM_AMOUNT = "min_amount";
    public static String DELIEVERY_CHARGE = "delivery_charge";
    public static String CURRENCY = "currency";
    public static String TAX = "tax";
    public static String CAT = "cat";
    public static String SUBCAT = "subcat";
    public static String LIMIT = "limit";
    public static String OFFSET = "offset";
    public static String LATITUDE = "latitude";
    public static String LONGITUDE = "longitude";
    public static String ACTIVE_STATUS = "active_status";

    public static String OTHER_IMAGES = "other_images";
    public static String ADD_ORDER_TRANS = "add_order_and_transaction";
    public static String AMOUNT = "amount";
    public static String PROMO_DISCOUNT = "promo_discount";
    public static String DISCOUNT_AMT = "discount_rupees";
    public static String TOTAL = "total";
    public static String PRODUCT_VARIANT_ID = "product_variant_id";
    public static String PRODUCT_ID_2 = "productId";

    public static String QUANTITY = "quantity";
    public static String USER_NAME = "user_name";
    public static String DELIVERY_CHARGE = "delivery_charge";
    public static String PAYMENT_METHOD = "payment_method";
    public static String ADDRESS = "address";
    public static String TRANS_ID = "txn_id";
    public static String MESSAGE = "message";
    public static String FINAL_TOTAL = "final_total";
    public static String GETORDERS = "get_orders";
    public static String ORDER_ID = "order_id";
    public static String UPDATE_ORDER_STATUS = "update_order_status";
    public static String PLACE_ORDER = "place_order";

    public static String NEW = "new";
    public static String OLD = "old";
    public static String HIGH = "high";
    public static String LOW = "low";
    public static String PRICE_L_H = "1";
    public static String PRICE_H_L = "-1";
    public static String PRODUCT_O_N = "-1";
    public static String PRODUCT_N_O = "-1";
    public static String ISAREACHAGE="";



    public static String SUB_TOTAL = "sub_total";
    public static String DELIVER_BY = "deliver_by";
    public static String UNIT = "unit";
    public static String SLUG = "slug";
    public static String PROMO_CODE = "promo_code";
    public static String TOKEN = "token";
    public static boolean isOrderCancelled;
    public static CharSequence[] filtervalues = {"Sort By A-Z", "Sort By Z-A", " Price Highest to Lowest ", " Price Lowest to Highest "};
    public static String FRND_CODE = "";
    public static String PLAY_STORE_LINK = "http://shorturl.at/fhlvO";
    public static String ALPHA_NUMERIC_STRING = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789abcdefghjiklmnopqrstuvwxyz";


    //set your jwt secret key here...key must same in PHP and Android
    public static String JWT_KEY = "replace_with_your_strong_jwt_secret_key";

    public static String verificationCode;
    public static String PAYPAL = "";
    public static String PAYUMONEY = "";
    public static String RAZORPAY = "";

    public static String MERCHANT_ID = "";
    public static String MERCHANT_KEY = "";
    public static String MERCHANT_SALT = "";
    public static String RAZOR_PAY_KEY_VALUE = "";




    public static String randomAlphaNumeric(int count) {
        StringBuilder builder = new StringBuilder();
        while (count-- != 0) {
            int character = (int) (Math.random() * ALPHA_NUMERIC_STRING.length());
            builder.append(ALPHA_NUMERIC_STRING.charAt(character));
        }
        return builder.toString();
    }

}
