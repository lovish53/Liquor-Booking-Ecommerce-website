package com.liquor.liquor.service;

import com.liquor.liquor.dto.CheckoutRequest;
import com.liquor.liquor.dto.CheckoutResponse;

public interface CheckoutService {

    CheckoutResponse checkout(CheckoutRequest request);

}
