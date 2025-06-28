package backend_service.shop.service;

import backend_service.shop.dto.request.DiscountRequestDTO;
import backend_service.shop.dto.response.DiscountResponse;
import backend_service.shop.dto.response.system.PageResponse;
import backend_service.shop.entity.Discount;

public interface DiscountService {

    /**
     * Create a new discount.
     *
     * @param requestDTO the discount creation request
     * @return the created discount response
     */
    DiscountResponse create(DiscountRequestDTO requestDTO);

    /**
     * Update an existing discount.
     *
     * @param id          the ID of the discount to update
     * @param requestDTO  the discount update request
     * @return the updated discount response
     */
    DiscountResponse update(Long id, DiscountRequestDTO requestDTO);

    /**
     * Delete a discount by ID.
     *
     * @param id the ID of the discount to delete
     */
    void delete(Long id);

    /**
     * Get a discount by ID.
     *
     * @param id the ID of the discount
     * @return the discount response
     */
    DiscountResponse getById(Long id);

    /**
     * Get all discounts with pagination.
     *
     * @param page the page number (0-based)
     * @param size the number of items per page
     * @return paginated list of discounts
     */
    PageResponse<?> getAll(int page, int size);

    /**
     * Validate and return a discount by code.
     * Typically used when applying a discount to an order.
     *
     * @param code the discount code
     * @return the valid Discount entity
     */
    Discount getValidDiscountByCode(String code);
}
