package com.ecommerce.service;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ecommerce.JwtEcommerceApplication;
import com.ecommerce.configuration.JwtRequestFilter;
import com.ecommerce.dao.CartDao;
import com.ecommerce.dao.OrderDetailDao;
import com.ecommerce.dao.ProductDao;
import com.ecommerce.dao.UserDao;
import com.ecommerce.entity.Cart;
import com.ecommerce.entity.OrderDetail;
import com.ecommerce.entity.OrderInput;
import com.ecommerce.entity.OrderProductQuantity;
import com.ecommerce.entity.Product;
import com.ecommerce.entity.TransactionDetails;
import com.ecommerce.entity.User;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;

@Service
public class OrderDetailService {
    private final JwtEcommerceApplication jwtEcommerceApplication;



    private static final String ORDER_PLACED = "Placed";
    
    private static final String KEY = "rzp_test_SOg64dVycvJJgC";
    private static final String KEY_SECRET = "j7BwelPhyM2MNZSSYgFK1bJE";
    private static final String CURRENCY = "INR";

    @Autowired
    private OrderDetailDao orderDetailDao;

    @Autowired
    private ProductDao productDao;

    @Autowired
    private UserDao userDao;
    
    @Autowired
    private CartDao cartDao;

    OrderDetailService(JwtEcommerceApplication jwtEcommerceApplication) {
        this.jwtEcommerceApplication = jwtEcommerceApplication;
    }



    public void placeOrder(OrderInput orderInput, boolean isSingleProductCheckout) {
        List<OrderProductQuantity> productQuantityList = orderInput.getOrderProductQuantityList();

        for (OrderProductQuantity o : productQuantityList) {
            Product product = productDao.findById(o.getProductId()).get();

            String currentUser = JwtRequestFilter.CURRENT_USER;
            User user = userDao.findById(currentUser).get();

            OrderDetail orderDetail = new OrderDetail(
                orderInput.getFullName(),
                orderInput.getFullAddress(),
                orderInput.getContactNumber(),
                orderInput.getAlternateContactNumber(),
                ORDER_PLACED,
                product.getProductDiscountedPrice() * o.getQuantity(),
                orderInput.getTransactionId(),
                product,
                user
            );
            //empty the cart
            if(!isSingleProductCheckout) {
            	List<Cart> carts=cartDao.findByUser(user);
            	carts.stream().forEach(cart -> cartDao.delete(cart));
            }

            orderDetailDao.save(orderDetail);
        }
    }


	public List<OrderDetail> getOrderDetails() {
		// TODO Auto-generated method stub
		String currentUser = JwtRequestFilter.CURRENT_USER;
		User user = userDao.findById(currentUser).get();
		
		return orderDetailDao.findByUser(user);
	}


	public List<OrderDetail> getAllOrderDetails(String status) {
		// TODO Auto-generated method stub
		List<OrderDetail> orderDetails = new ArrayList<>();
		
		if(status.equals("All")) {
			orderDetailDao.findAll().forEach(order -> orderDetails.add(order));
		}
		else {
			orderDetailDao.findByOrderStatus(status).forEach(order -> orderDetails.add(order));
		}
		
		return orderDetails;
	}
	
	public void markOrderAsDelivered(Integer orderId) {
		OrderDetail orderDetail = orderDetailDao.findById(orderId).get();
		
		if(orderDetail != null) {
			orderDetail.setOrderStatus("Delivered");
			orderDetailDao.save(orderDetail);
		}
	}
	
	public TransactionDetails createTransaction(Double amount) {
	    try {
	        JSONObject jsonObject = new JSONObject();
	        jsonObject.put("amount", (amount * 100));
	        jsonObject.put("currency", CURRENCY);

	        RazorpayClient razorpayClient = new RazorpayClient(KEY, KEY_SECRET);

	        Order order = razorpayClient.orders.create(jsonObject);

	        return prepareTransactionDetails(order);
	    } catch (Exception e) {
	        System.out.println(e.getMessage());
	    }

	    return null;
	}
	
	
	private TransactionDetails prepareTransactionDetails(Order order) {
	    String orderId = order.get("id");
	    String currency = order.get("currency");
	    Integer amount = order.get("amount");

	    TransactionDetails transactionDetails = new TransactionDetails(orderId, currency, amount, KEY);
	    return transactionDetails;
	}
}
