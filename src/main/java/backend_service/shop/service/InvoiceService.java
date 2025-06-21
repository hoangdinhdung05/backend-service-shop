package backend_service.shop.service;

import backend_service.shop.entity.Order;

public interface InvoiceService {

    /**
     * Create pdf file to save order information
     *
     * @param order
     */
    void generateInvoiceForOrder(Order order);

}
