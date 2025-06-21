package backend_service.shop.service;

import java.io.File;

public interface EmailService {

    /**
     * Use send response data to Client about order information
     *
     * @param to
     * @param subject
     * @param content
     * @param pdfAttachment
     */
    void sendInvoiceEmail(String to, String subject, String content, File pdfAttachment);

}
