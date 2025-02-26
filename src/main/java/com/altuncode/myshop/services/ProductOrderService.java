package com.altuncode.myshop.services;

import com.altuncode.myshop.model.ProductOrder;
import com.altuncode.myshop.model.enums.OrderStatusEnum;
import com.altuncode.myshop.repositories.ProductOrderRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("ProductOrderService")
@Transactional
public class ProductOrderService {
    private final EmailService emailService;
    private final ProductOrderRepo productOrderRepo;

    @Autowired
    public ProductOrderService(@Qualifier("EmailService") EmailService emailService, @Qualifier("ProductOrderRepo") ProductOrderRepo productOrderRepo) {
        this.emailService = emailService;
        this.productOrderRepo = productOrderRepo;
    }

    // get one productOrder by id
    public ProductOrder getProductOrderById(Long id) {
        return productOrderRepo.findById(id).orElseThrow(() -> new RuntimeException("ProductOrder not found with id: " + id));
    }

    // get all productOrder with page
    public Page<ProductOrder> getAllProductOrderWithPage(Pageable pageable) {
        return productOrderRepo.findAll(pageable);
    }

    public Long getProductOrderCount() {
        return productOrderRepo.countProductOrders();
    }

    // get all productOrder with page by user id
    public Page<ProductOrder> getAllProductOrderWithPageByUserId(Pageable pageable, Long userId) {
        return productOrderRepo.findAllByPersonId(pageable, userId);
    }

    // get all productOrder with page by user id
    public Page<ProductOrder> getAllProductOrderWithPageByUserEmail(Pageable pageable, String mail) {
        return productOrderRepo.findByPerson_Email(pageable, mail);
    }

    // create a new ProductOrder
    public ProductOrder createProductOrder(ProductOrder productOrder) {
        return productOrderRepo.save(productOrder);
    }

    //update status of ProductOrder by admin
    public void updateStatusOfProductOrderByAdmin(Long productOrderId, OrderStatusEnum newStatus) {
        ProductOrder productOrder = productOrderRepo.findById(productOrderId).get();
        productOrder.setOrderStatusEnum(newStatus);
        productOrderRepo.save(productOrder);
        if(productOrder.getOrderStatusEnum().equals(OrderStatusEnum.ORDERED)){
            emailService.sendMailtoAdmin("New Order by user #" + productOrder.getPerson().getId(), "New Order has been created by user #" + productOrder.getPerson().getId());
            emailService.sendHtmlEmail(productOrder.getPerson().getEmail(), "Your order has been received", "Your order #"+ productOrder.getId() + " has been successfully placed! We're preparing everything for you. Thank you for choosing us!");
        } else if (productOrder.getOrderStatusEnum().equals(OrderStatusEnum.DELIVERED)){
            emailService.sendMailtoAdmin("Order #" + productOrder.getId() + " status changed", "Order #" + productOrder.getId() + " status changed to " + newStatus);
            emailService.sendHtmlEmail(productOrder.getPerson().getEmail(), "Order status changed", "Your order #" + productOrder.getId() +"  has been delivered. We hope you enjoy it. Let us know if you need anything else!");
        } else {
            throw new RuntimeException("Order status can not be changed");
        }
    }

    public void deleteById(Long id){
        productOrderRepo.deleteById(id);
    }

}
