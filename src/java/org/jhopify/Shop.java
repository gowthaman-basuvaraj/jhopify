package org.jhopify;


import org.jhopify.api.OrderAPI;

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

public class Shop {
    public static void main(String[] args) throws IOException, JAXBException, URISyntaxException {
        List<Order> hyprotein = OrderAPI.getAllOrders("<username>", "<pass>", "<store-name>");
        hyprotein.stream().forEach(o -> System.out.println(o.getOrderNumber()));
    }
}
