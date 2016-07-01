package com.davinvicky.service.domain.request;

import io.swagger.annotations.ApiModel;
import com.davinvicky.common.domain.request.Request;

/**
 * @Author Iain Lumsden
 * Pojo that represents a JSON request to the get a new invoice number.
 * Created by lumsdei on 06/04/2016.
 *
 * This class may not have anything but the LoggingMDC uses the class name as the operation. So its useful to have this
 * class.
 */
@ApiModel(value = "Request to get new Invoice Number")
public class GetInvoiceNumberRequest extends Request {
}
