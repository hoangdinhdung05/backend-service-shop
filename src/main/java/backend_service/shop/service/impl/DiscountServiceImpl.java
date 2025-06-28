package backend_service.shop.service.impl;

import backend_service.shop.dto.request.DiscountRequestDTO;
import backend_service.shop.dto.response.DiscountResponse;
import backend_service.shop.dto.response.system.PageResponse;
import backend_service.shop.entity.Discount;
import backend_service.shop.repository.DiscountRepository;
import backend_service.shop.service.DiscountService;
import backend_service.shop.util.DiscountStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class DiscountServiceImpl implements DiscountService {

    private final DiscountRepository discountRepository;

    /**
     * Create a new discount.
     *
     * @param requestDTO the discount creation request
     * @return the created discount response
     */
    @Override
    public DiscountResponse create(DiscountRequestDTO requestDTO) {
        //check exists
        if(isExistsByCode(requestDTO.getCode())) {
            throw new IllegalArgumentException("Discount code already exists");
        }

        Discount discount = Discount.builder()
                .code(requestDTO.getCode())
                .description(requestDTO.getDescription())
                .discountType(requestDTO.getDiscountType())
                .value(requestDTO.getValue())
                .maxUses(requestDTO.getMaxUses())
                .maxUsesPerUser(requestDTO.getMaxUsesPerUser())
                .minOrderAmount(requestDTO.getMinOrderAmount())
                .startDate(requestDTO.getStartDate())
                .endDate(requestDTO.getEndDate())
                .status(DiscountStatus.ACTIVE)
                .build();

        Discount save = discountRepository.save(discount);

        log.info("Create discount successfully discount_id={}", save.getId());

        return toConvertResponse(save);
    }

    /**
     * Update an existing discount.
     *
     * @param id         the ID of the discount to update
     * @param requestDTO the discount update request
     * @return the updated discount response
     */
    @Override
    public DiscountResponse update(Long id, DiscountRequestDTO requestDTO) {
        Discount discount = discountRepository.findById(id).
                orElseThrow(() -> new IllegalArgumentException("Discount not found with id={}" + id));

        //check code
        if(!discount.getCode().equals(requestDTO.getCode()) && isExistsByCode(requestDTO.getCode())) {
            throw new IllegalArgumentException("Discount code already exists");
        }

        discount.setCode(requestDTO.getCode());
        discount.setDescription(requestDTO.getDescription());
        discount.setDiscountType(requestDTO.getDiscountType());
        discount.setValue(requestDTO.getValue());
        discount.setMaxUses(requestDTO.getMaxUses());
        discount.setMaxUsesPerUser(requestDTO.getMaxUsesPerUser());
        discount.setMinOrderAmount(requestDTO.getMinOrderAmount());
        discount.setStartDate(requestDTO.getStartDate());
        discount.setEndDate(requestDTO.getEndDate());

        Discount updated = discountRepository.save(discount);
        log.info("Update discount successfully discount_id={}", updated.getId());
        return toConvertResponse(updated);
    }

    /**
     * Delete a discount by ID.
     *
     * @param id the ID of the discount to delete
     */
    @Override
    public void delete(Long id) {
        Discount discount = discountRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Discount not found with id=" + id));
        discountRepository.delete(discount);

        log.info("Deleted discount with id={}", id);
    }

    /**
     * Get a discount by ID.
     *
     * @param id the ID of the discount
     * @return the discount response
     */
    @Override
    public DiscountResponse getById(Long id) {
        Discount discount = discountRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Discount not found with id=" + id));
        return toConvertResponse(discount);
    }

    /**
     * Get all discounts with pagination.
     *
     * @param page the page number (0-based)
     * @param size the number of items per page
     * @return paginated list of discounts
     */
    @Override
    public PageResponse<?> getAll(int page, int size) {
        Page<Discount> discountPage = discountRepository.findAll(PageRequest.of(page, size));

        List<DiscountResponse> responseList = discountPage.stream()
                .map(this::toConvertResponse)
                .toList();

        return PageResponse.builder()
                .page(page)
                .size(size)
                .items(responseList)
                .total(discountPage.getTotalElements())
                .build();
    }

    /**
     * Validate and return a discount by code.
     * Typically used when applying a discount to an order.
     *
     * @param code the discount code
     * @return the valid Discount entity
     */
    @Override
    public Discount getValidDiscountByCode(String code) {
        Discount discount = discountRepository.findByCodeIgnoreCase(code)
                .orElseThrow(() -> new IllegalArgumentException("Invalid discount code"));

        if (Boolean.FALSE.equals(discount.getStatus())) {
            throw new IllegalArgumentException("Discount is inactive");
        }

        LocalDateTime now = LocalDateTime.now();
        if ((discount.getStartDate() != null && now.isBefore(discount.getStartDate()))
                || (discount.getEndDate() != null && now.isAfter(discount.getEndDate()))) {
            throw new IllegalArgumentException("Discount is not valid at this time");
        }

        return discount;
    }


    private boolean isExistsByCode(String code) {
        return discountRepository.existsByCode(code);
    }

    /**
     * Check discount status
     *
     * @param discount
     * @return
     */
    private DiscountStatus resolveDiscountStatus(Discount discount) {
        if (Boolean.FALSE.equals(discount.getStatus())) return DiscountStatus.INACTIVE;

        LocalDateTime now = LocalDateTime.now();
        if (discount.getStartDate() != null && now.isBefore(discount.getStartDate()))
            return DiscountStatus.UPCOMING;

        if (discount.getEndDate() != null && now.isAfter(discount.getEndDate()))
            return DiscountStatus.EXPIRED;

        return DiscountStatus.ACTIVE;
    }

    /**
     * Convert Discount => Response
     *
     * @param discount
     * @return
     */
    private DiscountResponse toConvertResponse(Discount discount) {
        return DiscountResponse.builder()
                .code(discount.getCode())
                .description(discount.getDescription())
                .discountType(discount.getDiscountType()) // thay vì FIXED
                .value(discount.getValue())
                .maxUses(discount.getMaxUses())
                .maxUsesPerUser(discount.getMaxUsesPerUser())
                .startDate(discount.getStartDate())
                .endDate(discount.getEndDate())
                .minOrderAmount(discount.getMinOrderAmount())
                .status(resolveDiscountStatus(discount)) // tính status động
                .build();
    }
}
