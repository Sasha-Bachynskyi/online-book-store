package com.onlinebookstore.dto.book;

import java.math.BigDecimal;

public record BookSearchParametersDto(String[] titles,
                                      String[] authors,
                                      BigDecimal priceFrom,
                                      BigDecimal priceTo) {
}
