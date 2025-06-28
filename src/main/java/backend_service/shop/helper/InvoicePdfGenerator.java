package backend_service.shop.helper;

import backend_service.shop.entity.Order;
import backend_service.shop.entity.OrderDetail;
import com.lowagie.text.Document;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;
import lombok.extern.slf4j.Slf4j;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;

@Slf4j
public class InvoicePdfGenerator {

    public static File generateInvoicePdf(Order order) throws IOException {
        String dirPath = "src/main/resources/static/invoices";
        File dir = new File(dirPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        String fileName = "invoice_" + order.getId() + ".pdf";
        File file = new File(dir, fileName);

        Document document = new Document();
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            PdfWriter.getInstance(document, fos);
            document.open();

            document.add(new Paragraph("HÓA ĐƠN ĐƠN HÀNG"));
            document.add(new Paragraph("Mã đơn hàng: " + order.getId()));
            document.add(new Paragraph("Khách hàng: " + order.getUser().getEmail()));
            document.add(new Paragraph("Tổng tiền: " + formatMoney(order.getTotalPrice())));
            document.add(new Paragraph("Số lượng: " + order.getTotalQuantity()));
            document.add(new Paragraph("Ghi chú: " + order.getNote()));
            document.add(new Paragraph("Địa chỉ giao hàng: " + order.getShippingAddress()));
            document.add(new Paragraph("Phương thức thanh toán: " + order.getPaymentMethod()));
            document.add(new Paragraph("Trạng thái đơn hàng: " + order.getStatus()));

            //check discount
            if (order.getDiscount() != null) {
                document.add(new Paragraph("Mã giảm giá: " + order.getDiscount().getCode()));
                document.add(new Paragraph("Số tiền giảm: " + formatMoney(order.getDiscountAmount())));
                document.add(new Paragraph("Tổng tiền sau giảm: " + order.getFinalPrice()));
            } else {
                document.add(new Paragraph("Tổng tiền sau giảm: " + formatMoney(order.getTotalPrice())));
            }

            document.add(new Paragraph("Danh sách sản phẩm:"));
            for (OrderDetail detail : order.getOrderDetails()) {
                document.add(new Paragraph("- " + detail.getProduct().getName() +
                        ": SL=" + detail.getQuantity() + ", Giá=" + formatMoney(detail.getTotalPrice())));
            }

        } catch (Exception e) {
            log.error("PDF generation error: ", e);
            throw new RuntimeException("Lỗi tạo PDF", e);
        } finally {
            if (document.isOpen()) {
                document.close(); // Document sẽ tự flush stream
            }
            if (fos != null) {
                fos.close(); // Chỉ đóng sau khi document.close()
            }
        }

        return file;
    }

    private static String formatMoney(BigDecimal money) {
        return String.format("%,.0f VND", money);
    }


}
