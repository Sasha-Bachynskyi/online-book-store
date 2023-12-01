package com.onlinebookstore.dto.order;

import com.onlinebookstore.model.Order;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderUpdateRequestDto {
    @NotNull
    private Order.Status status;
}
