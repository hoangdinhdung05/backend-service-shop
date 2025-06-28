package backend_service.shop.service.impl;

import backend_service.shop.entity.Invoice;
import backend_service.shop.entity.Order;
import backend_service.shop.helper.InvoicePdfGenerator;
import backend_service.shop.repository.InvoiceRepository;
import backend_service.shop.service.EmailService;
import backend_service.shop.service.InvoiceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.io.File;
import java.time.LocalDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
public class InvoiceServiceImpl implements InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final EmailService emailService;


    /**
     * @param order
     */
    @Override
    public void generateInvoiceForOrder(Order order) {
        log.info("Create invoice pdf loading ...............");

        try {

            Invoice invoice = buildInvoice(order);

            invoice = invoiceRepository.save(invoice);

            //Create pdf
            File pdfFile = InvoicePdfGenerator.generateInvoicePdf(order);

            emailService.sendInvoiceEmail(
                    order.getUser().getEmail(),
                    "Hóa đơn cho đơn hàng #" + order.getId(),
                    "Cảm ơn bạn đã đặt hàng. Vui lòng xem hóa đơn đính kèm.",
                    pdfFile
            );

            // 4. Cập nhật đường dẫn file PDF
            invoice.setPdfPath(pdfFile.getAbsolutePath());
            invoice = invoiceRepository.save(invoice); // Lưu hóa đơn lần 2

            log.info("Đã tạo và gửi hóa đơn cho đơn hàng ID {}", order.getId());

        } catch (Exception e) {
            log.info("Generate invoice pdf fail {}", e.getMessage(), e);
            throw new RuntimeException("No create pdf", e);
        }
    }

    private Invoice buildInvoice(Order order) {
        return Invoice.builder()
                .order(order)
                .invoiceCode("INV_" + order.getId())
                .amount(order.getTotalPrice())
                .paymentMethod(order.getPaymentMethod())
                .isPaid(false)
                .issuedAt(LocalDateTime.now())
                .build();
    }
}

