package com.app.bdc_backend.service.shipment;

import com.app.bdc_backend.model.BasicShippingOrderInfo;
import com.app.bdc_backend.model.ShipmentInfo;

public interface IShipmentService {

    BasicShippingOrderInfo calculateShipmentFee(ShipmentInfo info) throws Exception;

}
