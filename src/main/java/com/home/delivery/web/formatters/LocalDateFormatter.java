package com.home.delivery.web.formatters;

import org.springframework.context.MessageSource;
import org.springframework.format.Formatter;

import javax.inject.Inject;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Created by evgeny on 09.05.15.
 */
public class LocalDateFormatter implements Formatter<LocalDate> {

    private static final String MESSAGE_ID = "date_format";
    private final MessageSource messageSource;

    @Inject
    public LocalDateFormatter(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @Override
    public LocalDate parse(String s, Locale locale) throws ParseException {
        return LocalDate.from(getFormatter(locale).parse(s));
    }

    @Override
    public String print(LocalDate localDate, Locale locale) {
        return getFormatter(locale).format(localDate);
    }

    private DateTimeFormatter getFormatter(Locale locale) {
        String dateFormat = messageSource.getMessage(MESSAGE_ID, null, locale);
        return DateTimeFormatter.ofPattern(dateFormat);
    }
}
