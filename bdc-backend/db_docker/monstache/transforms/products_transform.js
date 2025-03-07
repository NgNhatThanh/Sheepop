module.exports = function (doc) {
    // delete doc.description
    // delete doc.shop
    // delete doc.quantity
    // delete doc.weight
    // delete doc.skuList
    // delete doc.mediaList
    // delete doc.revenue
    // delete doc.updatedAt
    // delete doc.restricted
    // delete doc.restrictReason
    // delete doc.restrictStatus
    // delete doc.deleted
    // return doc

    doc.categoryId = doc.category

    return _.pick(
        doc,
        '_id',
        'name',
        'thumbnailUrl',
        'categoryId',
        'sold',
        'revenue',
        'createdAt',
        'averageRating',
        'visible',
        'deleted',
        'restricted'
    )
};