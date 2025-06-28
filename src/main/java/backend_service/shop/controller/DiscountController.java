package backend_service.shop.controller;

import backend_service.shop.dto.request.DiscountRequestDTO;
import backend_service.shop.dto.response.DiscountResponse;
import backend_service.shop.dto.response.system.PageResponse;
import backend_service.shop.dto.response.system.ResponseData;
import backend_service.shop.dto.response.system.ResponseError;
import backend_service.shop.service.DiscountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/discount")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Discount Controller")
public class DiscountController {

    private final DiscountService discountService;

    @Operation(summary = "Create a new discount")
    @PostMapping("/create")
    public ResponseData<?> createDiscount(@Valid @RequestBody DiscountRequestDTO request) {
        try {
            DiscountResponse response = discountService.create(request);
            return new ResponseData<>(HttpStatus.CREATED.value(), "Create discount successfully", response);
        } catch (Exception e) {
            log.error("Create discount failed: {}", e.getMessage(), e);
            return new ResponseError(HttpStatus.BAD_REQUEST.value(), e.getMessage());
        }
    }

    @Operation(summary = "Update an existing discount")
    @PutMapping("/{id}")
    public ResponseData<?> updateDiscount(@PathVariable @Min(1) Long id,
                                          @Valid @RequestBody DiscountRequestDTO request) {
        try {
            DiscountResponse response = discountService.update(id, request);
            return new ResponseData<>(HttpStatus.OK.value(), "Update discount successfully", response);
        } catch (Exception e) {
            log.error("Update discount failed: {}", e.getMessage(), e);
            return new ResponseError(HttpStatus.BAD_REQUEST.value(), e.getMessage());
        }
    }

    @Operation(summary = "Delete a discount by ID")
    @DeleteMapping("/{id}")
    public ResponseData<?> deleteDiscount(@PathVariable @Min(1) Long id) {
        try {
            discountService.delete(id);
            return new ResponseData<>(HttpStatus.OK.value(), "Delete discount successfully");
        } catch (Exception e) {
            log.error("Delete discount failed: {}", e.getMessage(), e);
            return new ResponseError(HttpStatus.NOT_FOUND.value(), e.getMessage());
        }
    }

    @Operation(summary = "Get discount by ID")
    @GetMapping("/{id}")
    public ResponseData<?> getDiscountById(@PathVariable @Min(1) Long id) {
        try {
            DiscountResponse response = discountService.getById(id);
            return new ResponseData<>(HttpStatus.OK.value(), "Get discount successfully", response);
        } catch (Exception e) {
            log.error("Get discount failed: {}", e.getMessage(), e);
            return new ResponseError(HttpStatus.NOT_FOUND.value(), e.getMessage());
        }
    }

    @Operation(summary = "Get all discounts with pagination")
    @GetMapping("/list")
    public ResponseData<?> getAllDiscounts(@RequestParam(defaultValue = "0") int page,
                                           @RequestParam(defaultValue = "20") @Min(1) int size) {
        try {
            PageResponse<?> response = discountService.getAll(page, size);
            return new ResponseData<>(HttpStatus.OK.value(), "Get all discounts successfully", response);
        } catch (Exception e) {
            log.error("Get all discounts failed: {}", e.getMessage(), e);
            return new ResponseError(HttpStatus.BAD_REQUEST.value(), e.getMessage());
        }
    }

    @Operation(summary = "Get and validate discount by code")
    @GetMapping("/code/{code}")
    public ResponseData<?> getDiscountByCode(@PathVariable String code) {
        try {
            var discount = discountService.getValidDiscountByCode(code);
            return new ResponseData<>(HttpStatus.OK.value(), "Discount code is valid", discount);
        } catch (Exception e) {
            log.error("Get discount by code failed: {}", e.getMessage(), e);
            return new ResponseError(HttpStatus.BAD_REQUEST.value(), e.getMessage());
        }
    }
}
