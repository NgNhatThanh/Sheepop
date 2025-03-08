package com.app.bdc_backend.service.shipment;

import com.app.bdc_backend.model.dto.BasicShippingOrderInfo;
import com.app.bdc_backend.model.dto.ShipmentInfo;

public interface IShipmentService {

    BasicShippingOrderInfo calculateShipmentFee(ShipmentInfo info) throws Exception;

}
