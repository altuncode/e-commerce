package com.altuncode.myshop.services;

import com.altuncode.myshop.model.*;
import com.altuncode.myshop.model.dto.AdressDTO;
import com.altuncode.myshop.model.enums.OrderStatusEnum;
import com.altuncode.myshop.repositories.ProductRepo;
import com.altuncode.myshop.repositories.projection.ProductProjectionUser;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service("ShoppingService")
@Transactional
public class ShoppingService {

    private final ProductRepo productRepo;
    private final CityService cityService;
    private final ProductOrderService productOrderService;

    @Autowired
    public ShoppingService(@Qualifier("ProductRepo") ProductRepo productRepo, @Qualifier("CityService") CityService cityService, @Qualifier("ProductOrderService") ProductOrderService productOrderService) {
        this.productRepo = productRepo;
        this.cityService = cityService;
        this.productOrderService = productOrderService;
    }

    // get all cart items list
    public String getCartItemsForSession(HttpSession session) {
        String cart = (String) session.getAttribute("cartItems");
        if (cart == null) {
            cart = "";
            session.setAttribute("cartItems", cart);
            return cart;
        }
        return cart;
    }

    // get all cart items list
    public int getSizeCartItemsForSession(HttpSession session) {
        String cart = (String) session.getAttribute("cartItems");
        if (cart == null || cart.isEmpty()) {
            return 0;
        }else {
            List<String> productIds = new ArrayList<>(List.of(cart.split(",")));
            return productIds.size() / 2;
        }

    }

    // add product to cart
    public void addToCart(HttpSession session, Long productId, int quantity) {
        if(!productRepo.existsActiveProductById(productId)){
            return;
        }
        String cart = getCartItemsForSession(session);
        String productItem = productId + "," + 0;
        for (int i = 0; i < quantity; i++) {
            if (cart.isEmpty()) {
                cart = productItem;
            } else {
                cart = cart + "," + productItem;
            }
        }
        session.setAttribute("cartItems", cart);
    }

    // add product to cart
    public Integer checkQuantityFromDatabase(Long productId) {
        return productRepo.findQuantityByProductId(productId).orElse(0);
    }

    // add product to cart
    public Integer checkQuantityFromCart(Map<Long, AbstractMap.SimpleEntry<Integer, Boolean>> cart, Long id) {
        if (cart == null || !cart.containsKey(id)) {
            return 0; // or any default value that makes sense
        }
        return cart.get(id).getKey();
    }

    // remove all from cart related to product id
    public void removeAllFromCartRelatedToProductId(HttpSession session, Long productId) {
        String cart = getCartItemsForSession(session);

        List<Long> list = Arrays.stream(cart.split(","))
                .map(String::trim)       // Remove any extra spaces
                .map(Long::parseLong)    // Convert to Long
                .collect(Collectors.toList());

        // Create a new list to store the filtered results
        List<Long> updatedList = new ArrayList<>();

        // Iterate through the list in pairs (productId, installStatus)
        for (int i = 0; i < list.size(); i += 2) {
            Long currentProductId = list.get(i);
            Long installStatus = list.get(i + 1);

            // If the productId does not match the one to remove, add it to the updated list
            if (!currentProductId.equals(productId)) {
                updatedList.add(currentProductId);
                updatedList.add(installStatus);
            }
        }

        // Convert the updated list back to a comma-separated string
        String updatedCartString = updatedList.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(","));

        session.setAttribute("cartItems", updatedCartString);
    }

    // remove one product id in cart
    public void removeOneFromCart(HttpSession session, Long productId) {
        String cart = getCartItemsForSession(session);
        List<Long> products = Arrays.stream(cart.split(","))
                .map(String::trim)       // Remove any extra spaces
                .map(Long::parseLong)    // Convert to Long
                .collect(Collectors.toList());

        for (int i = 0; i < products.size(); i += 2) {
            Long currentProductId = products.get(i);

            // If the current productId matches the productId to remove, remove both the productId and its install status
            if (currentProductId.equals(productId)) {
                products.remove(i);   // Remove productId
                products.remove(i);   // Remove install status (same index because of the previous removal)
                break;                // Exit after removing the first occurrence
            }
        }
        // Convert the updated list back to a comma-separated string
        String updatedCartString = products.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(","));

        session.setAttribute("cartItems", updatedCartString);
    }

    // Update the cart based on productId and installation status
    public void updateCartBasedOnInstall(HttpSession session, Long productId, Long install) {
        String cart = getCartItemsForSession(session);
        List<Long> list = Arrays.stream(cart.split(","))
                .map(String::trim)       // Remove any extra spaces
                .map(Long::parseLong)    // Convert to Long
                .collect(Collectors.toList());

        List<Long> updatedCart = new ArrayList<>();
        for (int i = 0; i < list.size(); i += 2) {
            Long currentProductId = list.get(i);
            Long installStatus = list.get(i + 1);

            // If the productId matches, update the install status
            if (currentProductId.equals(productId)) {
                updatedCart.add(currentProductId);
                updatedCart.add(install);
            } else {
                // Otherwise, keep the original productId and installStatus
                updatedCart.add(currentProductId);
                updatedCart.add(installStatus);
            }
        }

        // Convert the updated cart list back to a comma-separated string
        String updatedCartString = updatedCart.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(","));

        // Save the updated cart back to the session
        session.setAttribute("cartItems", updatedCartString);
    }

    // get grouped card item as key, value
    public Map<Long, AbstractMap.SimpleEntry<Integer, Boolean>> getGroupedCartItems(HttpSession session) {
        String cart = getCartItemsForSession(session);
        if (cart.isEmpty()) {
            return new HashMap<>(); // Return empty map if the cart is empty
        }
        // Split the cart string into product IDs
        Map<Long, AbstractMap.SimpleEntry<Integer, Boolean>> result = new HashMap<>();
        String[] productIds = cart.split(",");
        // Use a map to count occurrences of each product ID
        // Iterate through the parsed data
        for (int i = 0; i < productIds.length; i += 2) {
            long productId = Long.parseLong(productIds[i]);       // Parse product ID
            boolean install = Integer.parseInt(productIds[i + 1]) == 1; // Parse install status

            // Update the map
            result.compute(productId, (key, entry) -> {
                if (entry == null) {
                    // Initialize with quantity 1 and install status
                    return new AbstractMap.SimpleEntry<>(1, install);
                } else {
                    // Increment quantity and update install status
                    int quantity = entry.getKey() + 1;
                    boolean updatedInstall = entry.getValue() || install;
                    return new AbstractMap.SimpleEntry<>(quantity, updatedInstall);
                }
            });
        }
        return result;
    }

    // get all list of product by sendimg ids base on cart
    public List<ProductProjectionUser> getAllProductUnique(Map<Long, AbstractMap.SimpleEntry<Integer, Boolean>>  cart){
        List<Long> keysList = new ArrayList<>(cart.keySet());
        return productRepo.findProductsByIdsBaseOnCart(keysList);
    }



    public double calculateItemsPrice(Map<Long, AbstractMap.SimpleEntry<Integer, Boolean>> groupedCartItems,
                                      List<ProductProjectionUser> productList) {
        double total = 0.0;

        for (Map.Entry<Long, AbstractMap.SimpleEntry<Integer, Boolean>> entry : groupedCartItems.entrySet()) {
            Long productId = entry.getKey();
            int quantity = entry.getValue().getKey();

            // Find the matching product from the product list
            ProductProjectionUser product = productList.stream()
                    .filter(p -> p.getId().equals(productId))
                    .findFirst()
                    .orElse(null);

            if (product != null) {
                // Use newPrice if available, otherwise use price
                double price = product.getNewPrice() != null ? product.getNewPrice() : product.getPrice();


                // Multiply by the quantity
                double totalProductPrice = price * quantity;

                // Add to the total price
                total += totalProductPrice;
            }
        }

        return (Math.round(total * 10.0) / 10.0);
    }

    public double calculateInstallPrice(Map<Long, AbstractMap.SimpleEntry<Integer, Boolean>> groupedCartItems,
                                      List<ProductProjectionUser> productList) {
        double total = 0.0;

        for (Map.Entry<Long, AbstractMap.SimpleEntry<Integer, Boolean>> entry : groupedCartItems.entrySet()) {
            Long productId = entry.getKey();
            int quantity = entry.getValue().getKey();
            boolean install = entry.getValue().getValue();

            // Find the matching product from the product list
            ProductProjectionUser product = productList.stream()
                    .filter(p -> p.getId().equals(productId))
                    .findFirst()
                    .orElse(null);

            if (product != null) {
                // Use newPrice if available, otherwise use price
                double price = 0;

                // Add the install price if the install flag is true
                if (install) {
                    price += product.getInstallPrice();
                }

                // Multiply by the quantity
                double totalProductPrice = price * quantity;

                // Add to the total price
                total += totalProductPrice;
            }
        }

        return (Math.round(total * 10.0) / 10.0);
    }

    public double calculateTotalPriceWithoutHST(Map<Long, AbstractMap.SimpleEntry<Integer, Boolean>> groupedCartItems,
                                      List<ProductProjectionUser> productList) {
        double total = 0.0;

        for (Map.Entry<Long, AbstractMap.SimpleEntry<Integer, Boolean>> entry : groupedCartItems.entrySet()) {
            Long productId = entry.getKey();
            int quantity = entry.getValue().getKey();
            boolean install = entry.getValue().getValue();

            // Find the matching product from the product list
            ProductProjectionUser product = productList.stream()
                    .filter(p -> p.getId().equals(productId))
                    .findFirst()
                    .orElse(null);

            if (product != null) {
                // Use newPrice if available, otherwise use price
                double price = product.getNewPrice() != null ? product.getNewPrice() : product.getPrice();

                // Add the install price if the install flag is true
                if (install) {
                    price += product.getInstallPrice();
                }

                // Multiply by the quantity
                double totalProductPrice = price * quantity;

                // Add to the total price
                total += totalProductPrice;
            }
        }

        return (Math.round(total * 10.0) / 10.0);
    }

    public int calculateInstallItemCount(HttpSession session) {
        String cart = getCartItemsForSession(session);
        String[] pairs = cart.split(",");
        int installCount = 0;
        for (int i = 1; i < pairs.length; i += 2) {
            if (pairs[i].equals("1")) {
                installCount++;
            }
        }
        return installCount;
    }

    // complete order
    public ProductOrder completeOrder(Person person, HttpSession session, AdressDTO adressDTO) {
        // userin butun cartlarin aliram
        Map<Long, AbstractMap.SimpleEntry<Integer, Boolean>>  cart = getGroupedCartItems(session);
        List<Long> keysList = new ArrayList<>(cart.keySet());

        // yeni order yaradiram
        ProductOrder productOrder = new ProductOrder();
        productOrder.setPerson(person);
        productOrder.setFullName(adressDTO.getFullName());
        productOrder.setPhoneNumber(adressDTO.getPhoneNumber());
        productOrder.setPickup(adressDTO.getActive());
        if (!adressDTO.getActive()){
            productOrder.setAddress(adressDTO.getAddress());
            productOrder.setAddress2(adressDTO.getAddress2());
            productOrder.setCompanyName(adressDTO.getFullName());
            productOrder.setCity(adressDTO.getCity());
            productOrder.setPostalCode(adressDTO.getPostalCode());
        }
        productOrder.setNote(adressDTO.getNote());
        productOrder.setOrderStatusEnum(OrderStatusEnum.CREATED);
        // You can adjust the initial status as needed

        // order itemlari yaradiram
        List<ProductOrderItem> productOrderItems = new ArrayList<>();
        double deliveryPrice = adressDTO.getActive() ? 0.0 : cityService.getByName(adressDTO.getCity()).getPrice();
        double totalAmount = 0.0 + deliveryPrice;

        // her bir cart itemi order itema cevirirem
        for (Long idFromKeysList : keysList) {

            AbstractMap.SimpleEntry<Integer, Boolean> productDetails = cart.get(idFromKeysList);

            // cart itemdaki producti getirirem
            Product productFromCart = productRepo.findById(idFromKeysList).orElseThrow();
            Double priceAtPurchase;
            if(productFromCart.getNewPrice() != null && productFromCart.getNewPrice() > 0.00 ){
                priceAtPurchase = productFromCart.getNewPrice();
            }else {
                priceAtPurchase = productFromCart.getPrice();
            }

            // order item yaradiram
            ProductOrderItem orderItem = new ProductOrderItem();

            // order itemin melumatlarini set edirem
            orderItem.setProductName(productFromCart.getName());
            orderItem.setProductid(productFromCart.getId().toString());
            orderItem.setProductSize("W" +productFromCart.getWidth() + "\" x H"
                    + productFromCart.getHeight() + "\" x D"
                    + productFromCart.getDepth()+ "\"");
            orderItem.setProductColor(productFromCart.getProductColor().getName());
            orderItem.setNeedsInstallation(productDetails.getValue());
            orderItem.setPriceAtPurchase(priceAtPurchase);
            orderItem.setInstallPriceAtPurchase(productFromCart.getInstallPrice());
            orderItem.setQuantity(productDetails.getKey());

            productOrderItems.add(orderItem);

            // Calculate total cost (including installation if any)
            totalAmount += (priceAtPurchase + (productDetails.getValue()
                    ? productFromCart.getInstallPrice()
                    : 0.0) )* productDetails.getKey();
        }
        productOrder.setProductOrderItems(productOrderItems);
        double withHstPrice = ((totalAmount*13)/100) + totalAmount;
        Double tottal = (Math.round(withHstPrice * 10.0) / 10.0);
        productOrder.setTotalAmount(tottal);

        // Save the order (automatically saves ProductOrderItem entities due to cascade)
        productOrderService.createProductOrder(productOrder);

        // Clear the cart after placing the order
        session.removeAttribute("cartItems");

        return productOrder;
    }

}
