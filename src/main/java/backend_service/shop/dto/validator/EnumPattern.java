package backend_service.shop.dto.validator;

import jakarta.validation.Constraint;
import java.lang.annotation.*;
import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Documented
@Retention(RUNTIME)
@Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE})
@Constraint(validatedBy = EnumPatternValidator.class)
public @interface EnumPattern {
    String name();
    String regexp();
    String message() default "Invalid enum value. Must match the pattern: {regexp}";
    Class<?>[] groups() default {};
    Class<? extends jakarta.validation.Payload>[] payload() default {};
}
