package backend_service.shop.service.impl;

import backend_service.shop.dto.request.CartDetailRequestDTO;
import backend_service.shop.dto.response.CartDetailResponse;
import backend_service.shop.dto.response.system.PageResponse;
import backend_service.shop.entity.Cart;
import backend_service.shop.entity.CartDetail;
import backend_service.shop.entity.Product;
import backend_service.shop.exception.ResourceNotFoundException;
import backend_service.shop.repository.CartDetailRepository;
import backend_service.shop.repository.CartRepository;
import backend_service.shop.repository.ProductRepository;
import backend_service.shop.service.CartDetailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class CartDetailServiceImpl implements CartDetailService {

    private final CartRepository cartRepository;
    private final CartDetailRepository cartDetailRepository;
    private final ProductRepository productRepository;

    @Override
    @Transactional
    public CartDetailResponse addToCart(Long cartId, CartDetailRequestDTO request) {
        Cart cart = getCartById(cartId);
        Product product = getProductById(request.getProductId());

        Optional<CartDetail> optionalDetail = cart.getCartDetails().stream()
                .filter(cd -> cd.getProduct().getId().equals(product.getId()))
                .findFirst();

        CartDetail cartDetail;
        if (optionalDetail.isPresent()) {
            cartDetail = optionalDetail.get();
            int newQuantity = cartDetail.getQuantity() + request.getQuantity();
            cartDetail.setQuantity(newQuantity);
            cartDetail.setTotalPrice(cartDetail.getUnitPrice().multiply(BigDecimal.valueOf(newQuantity)));
            log.info("Updated quantity for existing product in cart: {}", product.getId());
        } else {
            cartDetail = CartDetail.builder()
                    .cart(cart)
                    .product(product)
                    .quantity(request.getQuantity())
                    .unitPrice(product.getPrice())
                    .totalPrice(product.getPrice().multiply(BigDecimal.valueOf(request.getQuantity())))
                    .build();
            cart.getCartDetails().add(cartDetail);
            log.info("Added new product to cart: {}", product.getId());
        }

        updateCartTotal(cart);
        return convertToResponse(cartDetail);
    }

    @Override
    @Transactional
    public CartDetailResponse updateQuantity(Long cartDetailId, int quantity) {
        CartDetail detail = getCartDetailById(cartDetailId);

        if (quantity <= 0) {
            removeProductFromCart(cartDetailId);
            return null;
        }

        detail.setQuantity(quantity);
        detail.setTotalPrice(detail.getUnitPrice().multiply(BigDecimal.valueOf(quantity)));

        updateCartTotal(detail.getCart());
        return convertToResponse(detail);
    }

    @Override
    @Transactional
    public void removeProductFromCart(Long cartDetailId) {
        CartDetail detail = getCartDetailById(cartDetailId);
        Cart cart = detail.getCart();

        cart.getCartDetails().removeIf(cd -> cd.getId().equals(cartDetailId));
        cartDetailRepository.deleteById(cartDetailId);

        updateCartTotal(cart);
        log.info("Removed cart detail ID {}", cartDetailId);
    }

    @Override
    @Transactional
    public void deleteAllByCartId(Long cartId) {
        Cart cart = getCartById(cartId);
        cartDetailRepository.deleteAll(cart.getCartDetails());
        cart.getCartDetails().clear();
        updateCartTotal(cart);
        log.info("Cleared all cart details for cart ID {}", cartId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CartDetailResponse> getCartDetails(Long cartId) {
        Cart cart = getCartById(cartId);
        return cart.getCartDetails().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<?> getListCartDetail(int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<CartDetail> cartDetailPage = cartDetailRepository.findAll(pageRequest);

        List<CartDetailResponse> content = cartDetailPage.getContent().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());

        return PageResponse.builder()
                .page(page)
                .size(size)
                .total(cartDetailPage.getTotalPages())
                .items(content)
                .build();
    }

    @Override
    @Transactional
    public void updateCartTotal(Long cartId) {
        Cart cart = getCartById(cartId);
        updateCartTotal(cart);
    }

    private void updateCartTotal(Cart cart) {
        BigDecimal totalPrice = cart.getCartDetails().stream()
                .map(CartDetail::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        int totalQuantity = cart.getCartDetails().stream()
                .mapToInt(CartDetail::getQuantity)
                .sum();

        cart.setTotalPrice(totalPrice);
        cart.setTotalQuantity(totalQuantity);
        cartRepository.save(cart);
    }

    /**
     *
     *
     * @param cartId
     * @return
     */
    private Cart getCartById(Long cartId) {
        return cartRepository.findById(cartId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found with ID: " + cartId));
    }

    /**
     *
     * @param productId
     * @return
     */
    private Product getProductById(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with ID: " + productId));
    }

    /**
     *
     * @param id
     * @return
     */
    private CartDetail getCartDetailById(Long id) {
        return cartDetailRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cart detail not found with ID: " + id));
    }

    /**
     * Convert to response
     *
     * @param detail
     * @return
     */
    private CartDetailResponse convertToResponse(CartDetail detail) {
        return CartDetailResponse.builder()
                .id(detail.getId())
                .cartId(detail.getCart().getId())
                .productId(detail.getProduct().getId())
                .productName(detail.getProduct().getName())
                .unitPrice(detail.getUnitPrice())
                .quantity(detail.getQuantity())
                .totalPrice(detail.getTotalPrice())
                .build();
    }
}