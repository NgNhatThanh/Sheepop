module.exports = function (doc) {
    doc.shopId = doc.shop
    doc.userId = doc.user
    doc.orderId = doc.order
    return _.pick(
        doc,
        '_id',
        'shopId',
        'userId',
        'orderId',
        'status',
        'createdAt',
        'total'
    )
};