package com.altuncode.myshop.services;


import com.squareup.square.SquareClient;
import com.squareup.square.api.CheckoutApi;
import com.squareup.square.exceptions.ApiException;
import com.squareup.square.models.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.squareup.square.Environment;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;



@Service
public class SquarePaymentService {
    @Value("${square.access.token}")
    private String accessToken;

    @Value("${square.location.id}")
    private String locationId;

    @Value("${square.redirect.url}")
    private String redirectUrl;

    public String createCheckout(Double amount, String currency, String orderID) throws ApiException, IOException {
        SquareClient client = new SquareClient.Builder()
                .accessToken(accessToken)
                .environment(Environment.PRODUCTION) // Change to PRODUCTION when live
                .build();

        CheckoutApi checkoutApi = client.getCheckoutApi();
        Long amountInCents = Math.round(amount * 100);

        // Create Money Object (Amount in cents, e.g. 5000 = $50.00)
        Money money = new Money.Builder()
                .amount(amountInCents)
                .currency(currency)
                .build();

        // Create Order Line Item
        OrderLineItem lineItem = new OrderLineItem.Builder("1")
                .name("Product Purchase")
                .basePriceMoney(money)
                .build();

        List<OrderLineItem> lineItems = new ArrayList<>();
        lineItems.add(lineItem);

        // ✅ Set `locationId` inside Order.Builder()
        Order order = new Order.Builder(locationId)
                .lineItems(lineItems)
                .metadata(Map.of("orderID", orderID))
                .build();

        // Create Order Request
        CreateOrderRequest orderRequest = new CreateOrderRequest.Builder()
                .idempotencyKey(UUID.randomUUID().toString())
                .order(order) // ✅ Pass Order here
                .build();

        // Create Checkout Request
        CreateCheckoutRequest checkoutRequest = new CreateCheckoutRequest.Builder(
                UUID.randomUUID().toString(), // Idempotency Key
                orderRequest
        ).redirectUrl(redirectUrl).build();

        // Create Checkout and Retrieve the Payment URL
        CreateCheckoutResponse response = checkoutApi.createCheckout(locationId, checkoutRequest);
        return response.getCheckout().getCheckoutPageUrl(); // ✅ Redirect user to this URL
    }
}
