package com.altuncode.myshop.controllers.admin;


import com.altuncode.myshop.model.Person;
import com.altuncode.myshop.model.ProductOrder;
import com.altuncode.myshop.model.enums.OrderStatusEnum;
import com.altuncode.myshop.services.PersonService;
import com.altuncode.myshop.services.ProductOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller("MainController")
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class MainController {

    private final PersonService personService;
    private final ProductOrderService productOrderService;

    @Autowired
    public MainController(@Qualifier("PersonService") PersonService personService, @Qualifier("ProductOrderService") ProductOrderService productOrderService) {
        this.personService = personService;
        this.productOrderService = productOrderService;
    }

    // Get all products
    @GetMapping({"/", ""})
    public String allGET(Model model) {
        model.addAttribute("personCount", personService.getPersonCount());
        model.addAttribute("productOrderCount", productOrderService.getProductOrderCount());
        return "admin/index";
    }

    @GetMapping({"/user", "/user/"})
    public String allGETusers(Model model, @RequestParam(defaultValue = "1") int page) {
        Page<Person> personList = personService.getAllPersonList(PageRequest.of(page-1, 10, Sort.by(Sort.Direction.DESC, "id")));
        model.addAttribute("personList", personList);
        model.addAttribute("totalItems", personList.getTotalElements());
        model.addAttribute("totalPage", personList.getTotalPages());
        model.addAttribute("currentPage", page);
        model.addAttribute("pageSize", 10);
        return "admin/userlist";
    }

    @GetMapping({"/order", "/order/"})
    public String allGETorders(Model model, @RequestParam(defaultValue = "1") int page) {
        Page<ProductOrder> productOrders = productOrderService.getAllProductOrderWithPage(PageRequest.of(page-1, 10, Sort.by(Sort.Direction.DESC, "id")));
        model.addAttribute("productOrders", productOrders);
        model.addAttribute("totalItems", productOrders.getTotalElements());
        model.addAttribute("totalPage", productOrders.getTotalPages());
        model.addAttribute("currentPage", page);
        model.addAttribute("pageSize", 10);
        return "admin/order/list";
    }

    // Altun buna baxdi
    // get one product
    @GetMapping({"/order/view/{id}", "/order/view/{id}/"})
    public String oneOrderGET(@PathVariable("id") Long id, Model model) {
        ProductOrder productOrder = productOrderService.getProductOrderById(id);
        if (productOrder == null) {
            return "redirect:/admin/order"; // Redirect if product not found
        }
        model.addAttribute("productOrder", productOrder);
        return "admin/order/order";
    }

    @PostMapping({"/order/update", "/order/update/"})
    public String editOrder(@RequestParam("id") Long id, @RequestParam("status") OrderStatusEnum status) {
        ProductOrder productOrder = productOrderService.getProductOrderById(id);
        if (productOrder == null) {
            return "redirect:/admin/order"; // Redirect if product not found
        }
        productOrderService.updateStatusOfProductOrderByAdmin(id,status);
        return "redirect:/admin/order/view/" + id;
    }

}
