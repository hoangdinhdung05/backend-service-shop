package backend_service.shop.service;

import backend_service.shop.dto.request.CartDetailRequestDTO;
import backend_service.shop.dto.response.CartDetailResponse;
import backend_service.shop.dto.response.system.PageResponse;
import java.util.List;

public interface CartDetailService {

    /**
     * Add a product to a specific cart.
     *
     * @param cartId  ID of the cart
     * @param request DTO containing productId and quantity
     * @return CartDetailResponse of the added or updated item
     */
    CartDetailResponse addToCart(Long cartId, CartDetailRequestDTO request);

    /**
     * Update the quantity of a specific cart detail item.
     *
     * @param cartDetailId ID of the cart detail item
     * @param quantity     New quantity (if <= 0, item will be removed)
     * @return Updated CartDetailResponse, or null if removed
     */
    CartDetailResponse updateQuantity(Long cartDetailId, int quantity);

    /**
     * Remove a specific item from the cart.
     *
     * @param cartDetailId ID of the cart detail item
     */
    void removeProductFromCart(Long cartDetailId);

    /**
     * Remove all items in a given cart.
     *
     * @param cartId ID of the cart
     */
    void deleteAllByCartId(Long cartId);

    /**
     * Recalculate total quantity and total price of the cart.
     *
     * @param cartId ID of the cart
     */
    void updateCartTotal(Long cartId);

    /**
     * Get all cart detail items for a given cart.
     *
     * @param cartId ID of the cart
     * @return List of CartDetailResponse
     */
    List<CartDetailResponse> getCartDetails(Long cartId);

    /**
     * Admin: Get paginated list of all cart detail items in the system.
     *
     * @param page Zero-based page index
     * @param size Page size
     * @return Paginated response of CartDetailResponse
     */
    PageResponse<?> getListCartDetail(int page, int size);
}
