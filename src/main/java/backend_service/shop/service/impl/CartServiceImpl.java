package backend_service.shop.service.impl;

import backend_service.shop.dto.request.CartRequestDTO;
import backend_service.shop.dto.response.CartDetailResponse;
import backend_service.shop.dto.response.CartResponse;
import backend_service.shop.dto.response.OrderResponse;
import backend_service.shop.dto.response.system.PageResponse;
import backend_service.shop.entity.Cart;
import backend_service.shop.entity.CartDetail;
import backend_service.shop.entity.Product;
import backend_service.shop.exception.ResourceNotFoundException;
import backend_service.shop.repository.CartRepository;
import backend_service.shop.repository.ProductRepository;
import backend_service.shop.repository.UserRepository;
import backend_service.shop.service.CartDetailService;
import backend_service.shop.service.CartService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final CartDetailService cartDetailService;

    /**
     * Create cart by request
     *
     * @param request
     * @return
     */
    @Override
    public CartResponse createCart(CartRequestDTO request) {

        //check user exists
        var user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        //check car exists by user id
        cartRepository.findByUserId(request.getUserId()).ifPresent(c -> {
            throw new IllegalStateException("User already has a cart");
        });

        //create cart
        Cart cart = Cart.builder()
                .user(user)
                .totalQuantity(0)
                .totalPrice(BigDecimal.ZERO)
                .build();
        cart = cartRepository.save(cart);

        log.info("Create cart successfully. CartId={}", cart.getId());

        return convertToResponse(cart);
    }

    /**
     * Get info cart by cart id
     *
     * @param id
     * @return
     */
    @Override
    public CartResponse getCartById(Long id) {
        Cart cart = cartRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found"));

        log.info("Get cart by id={}", cart.getId());

        return convertToResponse(cart);
    }

    /**
     * Admin use checkout method by user id
     *
     * @param userId
     * @return
     */
    @Override
    public CartResponse getCartByUserId(Long userId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found for userId: " + userId));

        log.info("Find cart by user id={}", userId);

        return convertToResponse(cart);
    }

    /**
     * Update cart
     *
     * @param cart
     */
    @Override
    public void updateCart(Cart cart) {
        BigDecimal totalCartPrice = BigDecimal.ZERO;
        int totalQuantity = 0;

        for (CartDetail detail : cart.getCartDetails()) {
            BigDecimal detailTotal = detail.getUnitPrice().multiply(BigDecimal.valueOf(detail.getQuantity()));
            detail.setTotalPrice(detailTotal);

            totalCartPrice = totalCartPrice.add(detailTotal);
            totalQuantity += detail.getQuantity();
        }

        cart.setTotalPrice(totalCartPrice);
        cart.setTotalQuantity(totalQuantity);
    }

    /**
     * Admin use method delete in system
     *
     * @param id
     */
    @Override
    public void deleteCart(Long id) {
        Cart cart = cartRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found"));
        cartRepository.delete(cart);
    }

    @Override
    public PageResponse<?> getAllCart(int page, int size) {
        Page<Cart> cartPage = cartRepository.findAll(PageRequest.of(page, size));

        List<CartResponse> responseList = cartPage.stream()
                .map(this::convertToResponse)
                .toList();

        return PageResponse.builder()
                .page(page)
                .size(size)
                .total(cartPage.getTotalPages())
                .items(responseList)
                .build();
    }


    /**
     * Clean cart
     *
     * @param userId
     */
    @Override
    public void clearCart(Long userId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found"));

        cart.getCartDetails().clear();
        cart.setTotalPrice(BigDecimal.ZERO);
        cart.setTotalQuantity(0);
        cartRepository.save(cart);
    }

    @Override
    public void recalculateCartTotal(Long cartId) {

    }

    @Override
    public OrderResponse checkoutCart(Long userId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found"));

        if (cart.getCartDetails().isEmpty()) {
            throw new IllegalStateException("Cart is empty");
        }

        // TODO: Gọi OrderService để tạo Order + OrderDetail từ cart
        // => Sau đó clear cart

        clearCart(userId);

        return null; // Trả về OrderResponse sau khi tạo xong
    }


    /**
     * Use update cart total
     *
     * @param cart
     */
    private void updateCartTotal(Cart cart) {
        int totalQuantity = 0;
        BigDecimal totalPrice = BigDecimal.ZERO;

        for (CartDetail detail : cart.getCartDetails()) {
            totalQuantity += detail.getQuantity();
            totalPrice = totalPrice.add(detail.getTotalPrice());
        }

        cart.setTotalQuantity(totalQuantity);
        cart.setTotalPrice(totalPrice);
    }

    /**
     * Convert global
     *
     * @param cart
     * @return
     */
    private CartResponse convertToResponse(Cart cart) {
        return CartResponse.builder()
                .id(cart.getId())
                .userId(cart.getUser().getId())
                .totalQuantity(cart.getTotalQuantity())
                .totalPrice(cart.getTotalPrice())
                .cartDetails(
                        cart.getCartDetails() != null
                                ? cart.getCartDetails().stream()
                                .map(this::convertToCartDetailResponse)
                                .toList()
                                : List.of()
                )
                .build();
    }

    /**
     * Convert cart detail to response
     *
     * @param detail
     * @return
     */
    private CartDetailResponse convertToCartDetailResponse(CartDetail detail) {
        return CartDetailResponse.builder()
                .id(detail.getId())
                .productId(detail.getProduct().getId())
                .productName(detail.getProduct().getName())
                .quantity(detail.getQuantity())
                .unitPrice(detail.getUnitPrice())
                .totalPrice(detail.getTotalPrice())
                .build();
    }
}
