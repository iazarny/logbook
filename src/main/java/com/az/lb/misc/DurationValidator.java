package com.az.lb.misc;

import com.vaadin.flow.data.binder.ValidationResult;
import com.vaadin.flow.data.binder.ValueContext;
import com.vaadin.flow.data.validator.AbstractValidator;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

import java.time.Duration;
import java.time.format.DateTimeParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DurationValidator extends AbstractValidator<String> {

    private static final Pattern PATTERN =
            Pattern.compile("(?:([-+]?[0-9]+)D)?" +
                            "(?:([-+]?[0-9]+)H)?(?:([-+]?[0-9]+)M)?(?:([-+]?[0-9]+)(?:[.,]([0-9]{0,9}))?S)?",
                    Pattern.CASE_INSENSITIVE);


    public DurationValidator(String errorMessage) {
        super(errorMessage);
    }

    @Override
    public ValidationResult apply(String value, ValueContext context) {
        final String toValidate = normalizeDurationString(value);
        if (StringUtils.isNotBlank(toValidate)) {
            try {
                Duration.parse(toValidate);
                return ValidationResult.ok();
            } catch (DateTimeParseException e) {
                return ValidationResult.error(" Not comply \"[d m h s]\" format. \n Example 1d27h. But was " + value);
            }
        }
        return ValidationResult.ok();
    }

    public Duration getDuration(String value) {
        try {
            return Duration.parse(normalizeDurationString(value));
        } catch (DateTimeParseException  | NullPointerException e) {
            return Duration.ZERO;
        }
    }

    String normalizeDurationString(String value) {
        String normalizedToValidate = null;
        if (null != value) {
            final String toValidate = ObjectUtils
                    .defaultIfNull(value, "")
                    .toUpperCase()
                    .replace(" ", "");
            final Matcher matcher = PATTERN.matcher(toValidate);
            normalizedToValidate = toValidate;
            if (matcher.matches()) {
                String dayMatch = matcher.group(1);
                String hourMatch = matcher.group(2);
                String minuteMatch = matcher.group(3);
                String secondMatch = matcher.group(4);
                if (StringUtils.isNotBlank(hourMatch) || StringUtils.isNotBlank(minuteMatch) || StringUtils.isNotBlank(secondMatch) ) {
                    // time only
                    normalizedToValidate = "P" + normalizeTime(hourMatch, minuteMatch, secondMatch);
                }

                if (StringUtils.isNotBlank(dayMatch)) {
                    normalizedToValidate = "P" + dayMatch + "D";
                    normalizedToValidate += normalizeTime(hourMatch, minuteMatch, secondMatch);
                }
            }
        }
        return normalizedToValidate;
    }

    private String  normalizeTime(String hourMatch, String minuteMatch, String secondMatch) {
        String normalizedToValidate = "";
        if (StringUtils.isNotBlank(hourMatch)) {
            normalizedToValidate += hourMatch + "H";
        }
        if (StringUtils.isNotBlank(minuteMatch)) {
            normalizedToValidate += minuteMatch + "M";
        }
        if (StringUtils.isNotBlank(secondMatch)) {
            normalizedToValidate += secondMatch + "S";
        }
        if (StringUtils.isNotBlank(normalizedToValidate)) {
            normalizedToValidate = "T" + normalizedToValidate;
        }
        return normalizedToValidate;
    }

}
