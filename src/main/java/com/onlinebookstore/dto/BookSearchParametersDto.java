package com.onlinebookstore.dto;

import java.math.BigDecimal;

public record BookSearchParametersDto(String[] titles,
                                      String[] authors,
                                      BigDecimal priceFrom,
                                      BigDecimal priceTo) {
}
