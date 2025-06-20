package backend_service.shop.service;

import backend_service.shop.dto.request.CartRequestDTO;
import backend_service.shop.dto.response.CartResponse;
import backend_service.shop.dto.response.OrderResponse;
import backend_service.shop.dto.response.system.PageResponse;
import backend_service.shop.entity.Cart;

public interface CartService {

    /**
     * Creates a new cart for a user.
     *
     * @param request the cart creation request, containing userId and other optional info
     * @return the created CartResponse
     */
    CartResponse createCart(CartRequestDTO request);

    /**
     * Retrieves a cart by its ID.
     *
     * @param id the ID of the cart
     * @return the corresponding CartResponse
     */
    CartResponse getCartById(Long id);

    /**
     * Retrieves a user's cart by user ID.
     *
     * @param userId the ID of the user
     * @return the user's CartResponse
     */
    CartResponse getCartByUserId(Long userId);

    /**
     * Updates the cart entity.
     *
     * @param cart the Cart entity to update
     */
    void updateCart(Cart cart);

    /**
     * Deletes a cart by its ID.
     *
     * @param id the ID of the cart to delete
     */
    void deleteCart(Long id);

    /**
     * Retrieves a paginated list of all carts.
     *
     * @param page zero-based page index
     * @param size the number of items per page
     * @return a PageResponse containing CartResponse data
     */
    PageResponse<?> getAllCart(int page, int size);

    /**
     * Clears all items from a user's cart.
     *
     * @param userId the ID of the user whose cart should be cleared
     */
    void clearCart(Long userId);

    /**
     * Recalculates the total price and quantity of the cart.
     *
     * @param cartId the ID of the cart to recalculate
     */
    void recalculateCartTotal(Long cartId);

    /**
     * Proceeds to checkout for the given user's cart and creates an order.
     *
     * @param userId the ID of the user checking out
     * @return an OrderResponse containing order summary and confirmation
     */
    OrderResponse checkoutCart(Long userId);
}
