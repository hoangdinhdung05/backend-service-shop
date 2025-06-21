package backend_service.shop.entity;

import backend_service.shop.util.PaymentMethod;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "Invoice")
@Table(name = "tbl_invoice")
public class Invoice extends AbstractEntity<Long> {

    @OneToOne
    private Order order;

    private String invoiceCode;
    private BigDecimal amount;
    private PaymentMethod paymentMethod;
    private Boolean isPaid;
    private String pdfPath;                 // Đường dẫn file hóa đơn PDF
    private String qrPath;                  // Nếu có thanh toán QR
    private LocalDateTime issuedAt;
}
